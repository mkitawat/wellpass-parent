package com.wellpass.core.models.auth;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * This class should be treated as the allow-able
 * parameters to be updated on the user wellbox
 */
@Entity(value = "userchangeset", noClassnameStored = true)
public class UserChangeSet {
  @Id
  public ObjectId id;

  public ObjectId userId;
  public String firstName;
  public String lastName;
  public String mobile;
  public String email;

  /**
   * originating system (i.e sensehealth)
   */
  public String sourceSystem;

  /**
   * the resource type (i.e provider/client)
   */
  public String sourceType;

  /**
   * the primary key of the resource type
   */
  public String sourceId;

  @Embedded
  public CustomAttributes customAttributes;
}
