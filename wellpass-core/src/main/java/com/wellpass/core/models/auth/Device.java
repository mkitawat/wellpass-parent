package com.wellpass.core.models.auth;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * A Wellpass device, e.g. for pings
 */
@Entity(value = "devices", noClassnameStored = true)
public class Device {
  @Id
  public ObjectId id;
  public ObjectId userId;
  public String apnsToken;
  public String androidId;
}
