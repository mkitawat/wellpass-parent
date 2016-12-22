package com.wellpass.core.models.wellbox;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Transient;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Represents all of the messages in a given conversation thread.
 */
@Entity(value = "messagethreads", noClassnameStored = true)
public class MessageThread implements Cloneable {
  @Id
  public ObjectId id;

  public ObjectId personId;
  public String name;
  public ObjectId highestMessageId;
  public ObjectId highWaterMark;

  @Embedded
  public List<Message> messages;
  @Transient
  public DisplayClient client;

  public void removeMessageFromThread(ObjectId messageId) {
    if (messageId == null) {
      throw new RuntimeException("missing parameter messageId");
    }
    Iterator<Message> iter = messages.iterator();

    while (iter.hasNext()) {
      Message m = iter.next();
      if (m.id.equals(messageId)) {
        iter.remove();
        break;
      }
    }
  }

  public MessageThread cloneWithMessages(Message... messages) {
    try {
      MessageThread clone = (MessageThread) this.clone();
      clone.messages = Arrays.asList(messages);
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }
}
