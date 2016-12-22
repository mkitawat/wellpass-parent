package com.wellpass.core.daos;

import com.wellpass.core.models.auth.Person;
import com.wellpass.core.models.wellbox.MessageThread;
import org.mongodb.morphia.Datastore;

import java.util.List;

//@Singleton
public class MessageTheadDAO extends DAOImpl<MessageThread> {
  //  @Inject
  public MessageTheadDAO(Datastore ds) {
    super(ds, MessageThread.class);
  }

  public List<MessageThread> messageThreadsForPersonAsList(Person person) {
    return createQuery()
      .filter("personId", person.id)
      .order("- highestMessageId")
      .asList();
  }
}
