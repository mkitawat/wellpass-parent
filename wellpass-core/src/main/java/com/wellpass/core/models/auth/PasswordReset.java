package com.wellpass.core.models.auth;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.utils.IndexType;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Entity(value = "password_reset", noClassnameStored = true)
@Indexes({
  @Index(fields = {
    @Field(value = "email", type = IndexType.ASC)},
    options = @IndexOptions(unique = true)
  )
})
public class PasswordReset {
  @Id
  private ObjectId id = new ObjectId();
  private String email;
  private OffsetDateTime expiration = OffsetDateTime.now(ZoneOffset.UTC).plusHours(24);

  // 0 arg constructor for morphia
  public PasswordReset() {
  }

  public PasswordReset(String email) {
    this.email = email;
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public OffsetDateTime getExpiration() {
    return expiration;
  }

  public void setExpiration(OffsetDateTime expiration) {
    this.expiration = expiration;
  }
}
