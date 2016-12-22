package com.wellpass.core.models.auth;

import org.mongodb.morphia.annotations.Embedded;

import java.time.LocalDate;

/**
 * This class should be treated as the allow-able
 * parameters to be updated on the customAttributes
 * wellbox
 */
@Embedded
public class CustomAttributes {
  public String healthPlanMemberID;
  public String medicaidID;
  public String zipCode;
  public LocalDate dueDate;
  public LocalDate dateOfInsuranceRedetermination;
  public LocalDate dateOfInsuranceTermination;
  public LocalDate dateOfLastPhysical;
  public boolean dentalCovered;
  public boolean isPregnant;
  public boolean isInFamily;
  public boolean isFamilyContact;
}
