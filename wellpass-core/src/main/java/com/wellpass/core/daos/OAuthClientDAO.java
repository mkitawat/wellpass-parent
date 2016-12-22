package com.wellpass.core.daos;

import com.wellpass.core.models.auth.OAuthClient;
import com.wellpass.core.models.wellbox.DisplayClient;
import org.mongodb.morphia.Datastore;

import java.util.List;
import java.util.stream.Collectors;

//@Singleton
public class OAuthClientDAO extends DAOImpl<OAuthClient> {
  //  @Inject
  public OAuthClientDAO(Datastore ds) {
    super(ds, OAuthClient.class);
  }

  public List<DisplayClient> getGalleryClientsAsList() {
    return createQuery()
      .asList()
      .stream()
      .filter(c -> !c.isOfficial)
      .map(c -> new DisplayClient(c))
      .collect(Collectors.toList());
  }
}
