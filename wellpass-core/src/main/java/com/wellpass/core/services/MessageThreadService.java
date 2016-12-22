package com.wellpass.core.services;

import com.wellpass.core.clients.PushNotificationClient;
import com.wellpass.core.clients.S3FileUploadClient;
import com.wellpass.core.clients.WPPortalClient;
import com.wellpass.core.daos.DeviceDAO;
import com.wellpass.core.daos.ImageDAO;
import com.wellpass.core.daos.MessageTheadDAO;
import com.wellpass.core.daos.OAuthSystemClientDAO;
import com.wellpass.core.models.auth.Device;
import com.wellpass.core.models.auth.OAuthSystemClient;
import com.wellpass.core.models.auth.Person;
import com.wellpass.core.models.auth.User;
import com.wellpass.core.models.wellbox.Image;
import com.wellpass.core.models.wellbox.Message;
import com.wellpass.core.models.wellbox.MessageThread;
import com.wellpass.core.utils.ModeUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Encapsulates logic and interaction with backends for wellbox/message threads.
 */
public class MessageThreadService {
  private final ExecutorService executor;
  private final Logger log = LoggerFactory.getLogger(MessageThreadService.class);
  private final WPPortalClient wpPortalClient;
  private final PushNotificationClient pushNotificationsService;
  private final OAuthSystemClient oAuthSystemClient;
  private final S3FileUploadClient fileUploadService;
  private final MessageTheadDAO messageTheadDAO;
  private final ImageDAO imageDAO;
  private final DeviceDAO deviceDAO;

  public MessageThreadService(WPPortalClient wpPortalClient,
                              PushNotificationClient pushNotificationClient,
                              MessageTheadDAO messageTheadDAO, ImageDAO imageDAO,
                              OAuthSystemClientDAO oAuthSystemClientDAO,
                              S3FileUploadClient s3FileUploadClient, DeviceDAO deviceDAO) {
      this.messageTheadDAO = messageTheadDAO;
      this.wpPortalClient = wpPortalClient;
      this.pushNotificationsService = pushNotificationClient;
      this.imageDAO = imageDAO;
      this.deviceDAO = deviceDAO;
      this.executor = Executors.newSingleThreadExecutor();
      this.oAuthSystemClient = oAuthSystemClientDAO.findOne("name", OAuthSystemClient.SENSEHEALTH);
      this.fileUploadService = s3FileUploadClient;
  }

  /**
   * Find a message thread by id.
   */
  public MessageThread threadFromId(ObjectId objectId) {
    return messageTheadDAO.findOneById(objectId.toHexString());
  }

  /**
   * Find all message threads for a given person.
   */
  public List<MessageThread> messageThreadsForPerson(Person person) {
    // If we actually page this, also look at unread bit.
    return messageTheadDAO.messageThreadsForPersonAsList(person);
  }

  /**
   * Create a new message thread or append to an existing one.
   */
  public MessageThread createOrAddMessage(ObjectId personId, String id, String name,
                                          Message message) {
    MessageThread thread = getOrCreate(personId, id, name);
    addMessage(thread, message);
    return thread;
  }

  /**
   * Find or create a new message thead.
   */
  private MessageThread getOrCreate(ObjectId personId, String id, String name) {
    if (id != null) {
      MessageThread existing = messageTheadDAO.findOneById(id);
      if (existing != null) {
        return existing;
      }
    }
    return create(personId, name);
  }

  /**
   * Create new message thread.
   */
  public MessageThread create(ObjectId personId, String name) {
    MessageThread m = new MessageThread();
    m.id = new ObjectId();
    m.personId = personId;
    m.name = name;
    m.messages = new ArrayList<>();
    messageTheadDAO.save(m);
    return m;
  }

  /**
   * Append a new message to an existing thread.
   */
  public void addMessage(MessageThread thread, Message message) {
    if (message.imageRaw != null) {
      addImageToMessage(message, true);
    } else if (message.imageUrl != null) {
      addImageToMessage(message, false);
    }

    thread.messages.add(message);
    thread.highestMessageId = message.id;

    if (message.fromUser) {
      thread.highWaterMark = message.id;
    }

    if (message.parentUid != null) {
      this.markParentMessageReplied(thread, message);
    }
    messageTheadDAO.save(thread);
  }

  /**
   * Add an uploaded photo to a message.
   */
  public void addImageToMessage(Message message, Boolean rawData) {
    Image image = new Image();
    image.id = new ObjectId();
    if (rawData) {
      try {
        String s3Url = uploadImageToS3(image, message.imageRaw);
        image.url = s3Url;
      } catch (Exception e) {
        image.raw = message.imageRaw;
      }
    } else {
      image.url = message.imageUrl;
    }

    message.image = image;
    imageDAO.save(image);
  }

  /**
   * Upload an image to S3.
   */
  public String uploadImageToS3(Image image, String imageData) {
    String bucketName = "wellpass-images-" + ModeUtils.getMode();
    String s3Url = fileUploadService.putImage(bucketName, image.id.toString(), imageData);
    return s3Url;
  }

  /**
   * Delete a message from a message thread.
   */
  public void deleteMessageFromThread(MessageThread thread, ObjectId messageId) {
    thread.removeMessageFromThread(messageId);
    messageTheadDAO.save(thread);
  }

  /**
   * Send message to Wellpass user from Sensehealth..
   */
  public void pushMessageToDevicesAsync(User user, String text) {
    executor.submit(() -> pushMessageToDevices(user, text));
  }

  private void pushMessageToDevices(User user, String text) {
    List<Device> devices = deviceDAO.findUserDevices(user);
    pushNotificationsService.notifyDevices(devices, text);
  }

  /**
   * Send a reply to Sensehealth from a Wellpass person.
   */
  public void pushReplyToApp(MessageThread thread, Message message) {
    MessageThread threadToSend = thread.cloneWithMessages(message);
    try {
      wpPortalClient.send(threadToSend, oAuthSystemClient, "message", oAuthSystemClient.messageWebhook);
      message.replyStatus = Message.REPLY_STATUS_DELIVERED;
    } catch (IOException e) {
      message.replyStatus = Message.REPLY_STATUS_FAILED;
      log.error("pushReplyToApp: failed to deliver message: {}", e);
    }
    messageTheadDAO.save(thread);
  }

  /**
   * Send a read update status to Sensehealth from a Wellpass person.
   */
  public void pushReadToApp(MessageThread thread) {
    MessageThread threadToSend = thread.cloneWithMessages();
    try {
      wpPortalClient
        .send(threadToSend, oAuthSystemClient, "markread", oAuthSystemClient.messageWebhook);
    } catch (IOException e) {
      log.error("markread call failed {}", e);
    }
  }

  /**
   * Check if the message being posted is a branch message (i.e a reply to a previous message)
   */
  private void markParentMessageReplied(MessageThread thread, Message message) {
    Message parentMessage = thread.messages.stream()
      .filter(m -> Objects.nonNull(m.getUid()))
      .filter(m -> m.getUid().equals(message.getParentUid()))
      .findFirst()
      .orElse(null);

    if (parentMessage != null) {
      parentMessage.hasReply = true;
    }
  }

  /**
   * Update the last viewed message id for a thread.
   */
  public void updateHighWaterMark(MessageThread thread, ObjectId messageId) {
    if (thread.highWaterMark != null && messageId.compareTo(thread.highWaterMark) <= 0) {
      // Guard against possible race where a delayed request muddles the state.
      // Highwater is initially null for a new thread
      return;
    }

    thread.highWaterMark = messageId;
    messageTheadDAO.save(thread);
    pushReadToApp(thread);
  }

  /**
   * Find the largest message ID out of all of these threads.
   */
  public ObjectId computeAfterToken(List<MessageThread> threads) {
    return threads.stream().map(this::computeAfterToken).max(Comparator.naturalOrder())
      .orElse(new ObjectId());
  }

  /**
   * Find the largest message ID in a given thread (should be the last one ;) ).
   */
  public ObjectId computeAfterToken(MessageThread thread) {
    return thread.messages.stream().map(m -> m.id).max(Comparator.naturalOrder())
      .orElse(new ObjectId());
  }

  /**
   * Filter the set of threads to only those with new content since the given afterToken and mutate
   * those threads to only contain new messages.
   */
  public List<MessageThread> applyAfterToken(List<MessageThread> threads, ObjectId afterToken) {
    return threads.stream().map(t -> applyAfterToken(t, afterToken))
      .filter(t -> t.messages.size() > 0).collect(Collectors.toList());
  }

  /**
   * Mutate the messages in a thread to only those new since the given afterToken.
   */
  public MessageThread applyAfterToken(MessageThread thread, ObjectId afterToken) {
    thread.messages =
      thread.messages.stream().filter(m -> m.id.compareTo(afterToken) > 0)
        .collect(Collectors.toList());
    return thread;
  }

  /**
   * Takes an S3 bucket URL and generates a presigned temporary URL
   */
  public String generateSignedUrl(Message message) {
    String bucketName = S3FileUploadClient.s3BucketFromUri(message.image.url);
    String key = S3FileUploadClient.s3KeyFromUri(message.image.url);

    return fileUploadService.signUrl(bucketName, key, message.image.url);
  }

}
