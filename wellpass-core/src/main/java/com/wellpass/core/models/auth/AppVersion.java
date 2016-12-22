package com.wellpass.core.models.auth;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.utils.IndexType;

/**
 * Supported app version by platform
 */
@Entity(value = "app_version", noClassnameStored = true)
@Indexes({
  @Index(fields = {
    @Field(value = "platform", type = IndexType.ASC)},
    options = @IndexOptions(unique = true, sparse = true)
  )
})
public class AppVersion {
  @Id
  public ObjectId id;

  public String platform;
  public String version;
}
