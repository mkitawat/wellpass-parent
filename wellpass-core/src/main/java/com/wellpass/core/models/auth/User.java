package com.wellpass.core.models.auth;

import com.wellpass.core.utils.gson.Exclude;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.utils.IndexType;

/**
 * A temporary class that will become the de facto User wellbox which will be used to handle logins
 */
@Entity(value = "users", noClassnameStored = true)
@Indexes({
  @Index(fields = {
    @Field(value = "email", type = IndexType.ASC)},
    options = @IndexOptions(unique = true, sparse = true)
  )
})
public class User {
  @Id
  public ObjectId id;

  public String email;

  @SuppressWarnings("DefaultAnnotationParam") // for intellij
  @Exclude(serialize = true, deserialize = false)
  public byte[] password;

  @SuppressWarnings("DefaultAnnotationParam")
  @Exclude(serialize = true, deserialize = false)
  public byte[] salt;

  public boolean verified = false;

  @Exclude
  public String verificationCode;

  @Reference(idOnly = true)
  public Person person;
}
