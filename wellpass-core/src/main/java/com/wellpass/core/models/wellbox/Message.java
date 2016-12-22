package com.wellpass.core.models.wellbox;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.NotSaved;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.annotations.Transient;


/**
 * The core content of a message to or from a person.
 */
public class Message {

  public static final String REPLY_STATUS_DELIVERED = "delivered";
  public static final String REPLY_STATUS_FAILED = "failed";

  // TODO: Structured reply types?
  public static final String SENDER_TYPE_ORG = "org";
  public static final String SENDER_TYPE_PROVIDER = "provider";
  public static final String SENDER_TYPE_SCRIPT = "script";

  // common attributes
  public ObjectId id;
  public boolean fromUser;
  public String text;
  public String type;
  public boolean hasReply;

  // attributes for messages to person
  public Button[] buttons;
  public String
    senderId;
    // id of the senderType (ex. provider_id, org_id, script_id) in Sensehealth
  public String senderType; // provider, org, script
  public String uid; // unique global id for messages that are sent from sensehealth
  public String parentUid;

  // attributes for messages from person
  public String replyStatus;

  @Reference(idOnly = true)
  public Image image;

  @Transient
  public long timestamp;

  @NotSaved
  public String imageUrl;

  @NotSaved
  public String imageRaw;

  public Message() {
  }

  public Message(ObjectId id) {
    this.id = id;
  }

  public Message(String text, boolean fromUser) {
    this.id = new ObjectId();
    this.text = text;
    this.fromUser = fromUser;
    this.replyStatus = Message.REPLY_STATUS_DELIVERED;
  }

  public String getUid() {
    return uid;
  }

  public String getParentUid() {
    return parentUid;
  }

  public boolean validSenderType() {
    return
      SENDER_TYPE_ORG.equals(senderType) ||
        SENDER_TYPE_PROVIDER.equals(senderType) ||
        SENDER_TYPE_SCRIPT.equals(senderType);
  }
}
