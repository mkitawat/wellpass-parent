package com.wellpass.core.daos;

import com.wellpass.core.models.auth.OAuthClient;
import com.wellpass.core.models.auth.OAuthToken;
import com.wellpass.core.models.auth.User;
import org.mongodb.morphia.Datastore;

//@Singleton
public class OAuthTokenDAO extends DAOImpl<OAuthToken> {
  //  @Inject
  public OAuthTokenDAO(Datastore ds) {
    super(ds, OAuthToken.class);
  }

  public OAuthToken getExistingUserToken(User user, OAuthClient client) {
    return createQuery()
      .field("userId").equal(user.id)
      .field("clientId").equal(client.id)
      .field("isExpired").equal(false)
      .get();
  }
}
