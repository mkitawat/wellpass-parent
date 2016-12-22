package com.wellpass.core.daos;

import com.wellpass.core.models.auth.OAuthSystemClient;
import org.mongodb.morphia.Datastore;

//@Singleton
public class OAuthSystemClientDAO extends DAOImpl<OAuthSystemClient> {
  //  @Inject
  public OAuthSystemClientDAO(Datastore ds) {
    super(ds, OAuthSystemClient.class);
  }

  public OAuthSystemClient getWellpassPortalSystemClient() {
    return findOne("name", OAuthSystemClient.SENSEHEALTH);
  }
}
