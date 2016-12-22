package com.wellpass.core.daos;

import com.wellpass.core.models.auth.User;
import org.mongodb.morphia.Datastore;

//@Singleton
public class UserDAO extends DAOImpl<User> {
  //  @Inject
  public UserDAO(Datastore ds) {
    super(ds, User.class);
  }

  public User findOneByEmail(String email) {
    // todo justin - for some reason this is not working
//    return createQuery().field("email").equalIgnoreCase(lowercaseEmail).get();
    return findOne("email", email.toLowerCase());
  }
}
