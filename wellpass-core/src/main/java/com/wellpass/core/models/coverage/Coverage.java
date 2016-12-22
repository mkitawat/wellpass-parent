package com.wellpass.core.models.coverage;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

public class Coverage {
  @Id
  public ObjectId id;

  public BenefitPlan benefitPlan;

  public Boolean medicalCovered;

  public Boolean dentalCovered;

  public Boolean visionCovered;

  public Boolean pharmacyCovered;
}
