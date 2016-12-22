package com.wellpass.core.models.coverage;

import java.time.LocalDate;

public class BenefitPlan {

  public boolean active;

  public String memberId;

  public String payerName;

  public String planNumber;

  public String planDescription;

  public String planLogoLink;

  public String groupNumber;

  public String groupDescription;

  public String insuranceType;

  public String level;

  public LocalDate planBeginDate;

  public LocalDate planEndDate;

  public LocalDate eligibilityBeginDate;

  public LocalDate redeterminationDate;

}

/*
  Member Eligibility file mappings:
    Wellpass_Enterprise_Id -> organizationId
    Membership_Id -> memberId
    Medicaid_Id -> medicaidId
    Benefit_Package_Id -> planNumber
    Group_Number_Id -> groupNumber
    Product_Type -> insuranceType
    Member_Status -> active
    Plan_Effective_Date -> planBeginDate
    Plan_Term_Date -> planEndDate
    Date_of_start_of_redetermination -> redeterminationDate
    Medical_Covered -> medicalCovered
    Dental_Covered -> dentalCovered
    Vision_Covered -> visionCovered
    RxCovered -> prescriptionCovered
    hasConsentedToEnrollment -> hasConsentedToEnrollment
 */
