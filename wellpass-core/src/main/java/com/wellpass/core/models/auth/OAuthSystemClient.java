package com.wellpass.core.models.auth;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * A system that OAuths into WellPass.
 */
@Entity(value = "clients", noClassnameStored = true)
public class OAuthSystemClient {

  public static final String SENSEHEALTH = "SENSEHEALTH";

  @Id
  public ObjectId id;

  public String name;

  public String webhookSecret;

  public String clientSecret;

  public String messageWebhook;

  public String enrollWebhook;
}
