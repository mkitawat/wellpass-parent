package com.wellpass.core.services;

import com.wellpass.core.annotations.UnitTests;
import com.wellpass.core.models.wellbox.Message;
import com.wellpass.core.models.wellbox.MessageThread;
import junit.framework.Assert;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

@Category(UnitTests.class)
public class MessageThreadTest {

  /**
   * Verify a message is removed from a thread if the id matches
   */
  @Test
  public void testRemoveMessageFromThread() throws Exception {
    MessageThread thread = generateMessageThread();
    Message message = thread.messages.get(0);

    thread.removeMessageFromThread(message.id);
    Assert.assertTrue(!thread.messages.contains(message));
  }

  public static MessageThread generateMessageThread() {
    MessageThread thread = new MessageThread();
    thread.id = new ObjectId();
    List<Message> messages = new ArrayList<>();
    messages.add(new Message(new ObjectId()));
    messages.add(new Message(new ObjectId()));
    thread.messages = messages;
    return thread;
  }
}
