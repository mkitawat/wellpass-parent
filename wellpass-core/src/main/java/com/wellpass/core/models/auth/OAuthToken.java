package com.wellpass.core.models.auth;

import com.wellpass.core.utils.SecurityUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Transient;

@Entity(value = "oauthtokens", noClassnameStored = true)
public class OAuthToken {

  @Id
  public String id = SecurityUtils.nextToken();

  public ObjectId userId;
  public ObjectId clientId;
  public boolean isExpired;

  @Transient
  public User user;

  @Transient
  public OAuthClient client;
}
