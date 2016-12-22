package com.wellpass.core.models.wellbox;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value = "image", noClassnameStored = true)
public class Image {
  @Id
  public ObjectId id;

  public String url;
  public String raw;
}
