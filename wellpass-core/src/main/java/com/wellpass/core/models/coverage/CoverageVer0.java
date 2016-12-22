package com.wellpass.core.models.coverage;

import com.wellpass.core.mongo.converters.LocalDateConverter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Converters;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.time.LocalDate;

/**
 * Created by pmotipara on 3/29/16.
 * Remove it after confirming its non-usage.
 * Use com.wellpass.coverage.api.EligibilityResponse.
 */
@Entity(value = "coverage", noClassnameStored = true)
@Converters(LocalDateConverter.class)
@Deprecated
public class CoverageVer0 {
  @Id
  public ObjectId id;

  public ObjectId userId;

  public String insuranceCo;

  public String network;

  public String plan;

  public String medicalGroupNo;

  public String dentalGroupMo;

  public String issuer;

  public int deductible;

  public int copay;

  public String cardFrontUrl;

  public String cardBackUrl;

  public LocalDate effectiveStartDate;

  public LocalDate effectiveEndDate;

}
