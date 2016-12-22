package com.wellpass.core.models.auth;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * An application that OAuths into WellPass.
 */
@Entity(value = "clients", noClassnameStored = true)
public class OAuthClient {

  public static ObjectId ANDROID = new ObjectId("570582162041962a1cb513cd");
  public static ObjectId IPHONE = new ObjectId("570582162041962a1cb513ce");
  public static ObjectId WEB = new ObjectId("570582162041962a1cb513cf");
  public static ObjectId T4B = new ObjectId("570582162041962a1cb513d0");

  @Id
  public ObjectId id;

  public String name;

  public boolean isOfficial;

  public String profilePhoto;

  // TODO: Want to include in client admin UI but exclude from messagethread JSON

  public String messageWebhook;

  public String enrollWebhook;

  public String readWebhook;

  // TODO: To be safe, we should @Exclude this and create a new object we return the client management UI
  public String webhookSecret;

  public String clientSecret;
}
