package com.wellpass.core.models.coverage;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value = "pokit_tradingpartners", noClassnameStored = true)
public class VoxivaPokitTradingPartner {
  @Id
  public ObjectId id;
  public String voxivaTenantId;
  public String tradingPartnerId;
}
