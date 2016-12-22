package com.wellpass.core.daos;

import com.wellpass.core.models.auth.UserChangeSet;
import org.mongodb.morphia.Datastore;

//@Singleton
public class UserChangeSetDAO extends DAOImpl<UserChangeSet> {
  //  @Inject
  public UserChangeSetDAO(Datastore ds) {
    super(ds, UserChangeSet.class);
  }


}
