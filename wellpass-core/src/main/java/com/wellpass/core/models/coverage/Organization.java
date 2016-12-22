package com.wellpass.core.models.coverage;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Indexes;

@Entity(value = "organization", noClassnameStored = true)
@Indexes({
  @Index(
    fields = @Field("tracfonePlanId"),
    options = @IndexOptions(unique = true, sparse = true))
})
public class Organization {
  @Id
  public ObjectId id;

  @Indexed(unique = true, dropDups = true)
  public String organizationId;

  // ID for org in Sensehealth
  @Indexed(unique = true, dropDups = true)
  public String shId;

  public String type;
  public String fullName;
  public String shortName;
  public String state;
  public String pokitdokPartnerId;
  public String voxivaTenantId;
  public String tracfonePlanId;
  public boolean lifelineOnly;

  // TODO - FTP location?
}
