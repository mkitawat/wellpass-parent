package com.wellpass.core.models.wellbox;

import com.wellpass.core.models.auth.OAuthClient;
import org.bson.types.ObjectId;

/**
 * Only specific fields should be returned for a client shown to users.
 */
// todo justin - is this relevant?
public class DisplayClient {
  public final String profilePhoto;
  public final String name;
  public final ObjectId id;

  public DisplayClient(OAuthClient client) {
    this.id = client.id;
    this.name = client.name;
    this.profilePhoto = client.profilePhoto;
  }
}
