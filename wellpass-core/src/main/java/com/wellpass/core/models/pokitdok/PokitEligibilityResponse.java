package com.wellpass.core.models.pokitdok;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents the JSON object under "data" key in PokitDok Eligibility response. See <a
 * href="https://platform.pokitdok.com/documentation/v4/#eligibility">PokitDok Documentation</a>
 */
public class PokitEligibilityResponse extends PokitBaseResponse {

  public String clientId;
  public Coverage coverage;
  public PharmacyCoverage pharmacy;

  public PokitDependent dependent;
  public PokitSubscriber subscriber;

  public String tradingPartnerId;
  public PokitPayer payer;
  public PokitProvider provider;

  public List<String> serviceTypes;
  public List<String> serviceTypeCodes;

  public boolean validRequest;
  public String rejectReason;
  public String followUpAction;

  public static PokitEligibilityResponse fromJson(String json) {
    return PokitJsonifier.getInstance().fromRootJson(json, PokitEligibilityResponse.class);
  }

  /**
   * Represents the meat of PokitDok Eligibility data.
   * Includes major information like plan details, copay, deductibles, coinsurance,
   * limitations and outOfPocket.
   */
  public static class Coverage {
    public boolean active;
    public String planNumber;
    public String planDescription;

    public String groupNumber;
    public String groupDescription;

    public String insuranceType;
    public String level;

    public LocalDate planDate;
    public LocalDate planBeginDate;
    public LocalDate planEndDate;
    public LocalDate eligibilityBeginDate;
    public LocalDate serviceDate;

    public PokitPrimaryCareProvider primaryCareProvider;

    public List<Copay> copay;
    public List<Coinsurance> coinsurance;
    public List<Limitation> limitations;
    public List<Deductible> deductibles;
    public List<OutOfPocket> outOfPocket;
    public List<NonCovered> nonCovered;

    public List<Contact> contacts;
    public List<HealthCareFacility> healthcareFacilities;

    public List<OtherPayer> otherPayers;

    public List<String> serviceTypes;
    public List<String> serviceTypeCodes;

    public String procedureId;
    public String procedureIdQualifier;
  }

  /**
   * Common super class for Copay, Coinsurance, Deductible, Limitation and OutOfPocket.
   * Holds common dimensions like coverageLevel, inPlanNetwork, delivery, serviceCodes
   * and procedureId.
   */
  public static class CoverageItem {
    public String coverageLevel;
    public String inPlanNetwork;
    public PokitDelivery delivery;

    // Do we need serviceTypes. maybe not.
    public List<String> serviceTypes;
    public List<String> serviceTypeCodes;
    public List<PokitMessage> messages;

    public String procedureId;
    public String procedureIdQualifier;
  }

  public static class Deductible extends CoverageItem {
    public PokitMoney benefitAmount;

    public String timePeriod;
    public String insuranceType;
    public LocalDate eligibilityDate;

  }

  public static class Copay extends CoverageItem {
    public PokitMoney copayment;
    public String authorizationRequired;
  }

  public static class Limitation extends CoverageItem {
    public PokitMoney benefitAmount;
  }

  public static class OutOfPocket extends CoverageItem {
    public PokitMoney benefitAmount;
    public String timePeriod;
  }

  public static class Coinsurance extends CoverageItem {
    public String benefitPercent;
    public String authorizationRequired;
  }

  public static class NonCovered extends CoverageItem {
    public PokitMoney benefitAmount;
    public String timePeriod;
  }

  public static class OtherPayer extends PokitPayer {
    public String planNumber;
    public String planDescription;
    public PokitSubscriber subscriber;

    public String coordinationOfBenefits;
    public String coordinationOfBenefitsDate;

    public String coverageLevel;

    public List<String> serviceTypes;
    public List<String> serviceTypeCodes;

    public String procedureId;
    public String procedureIdQualifier;
  }

  public static class Contact {
    public String id;
    public String name;
    public String phone;
    public String email;
    public String url;
    public String contactType;
    public PokitAddress address;
    // Do we need serviceTypes. maybe not.
    public List<String> serviceTypes;
    public List<String> serviceTypeCodes;

    public String procedureId;
    public String procedureIdQualifier;
  }

  public static class HealthCareFacility {
    public String name;
    public String phone;
    public String email;
    public String url;
    public String fax;
    public String facilityType;
    public PokitAddress address;
    // Do we need serviceTypes. maybe not.
    public List<String> serviceTypes;
    public List<String> serviceTypeCodes;
  }

  /**
   * Represents Pharmacy specific coverage in PokitDok Eligibility data.
   */
  public static class PharmacyCoverage {
    public boolean isEligible;
    public String planNumber;

    public LocalDate benefitDate;

    public List<PharmacyCopay> copay;

    public BenefitsManager benefitsManager;
  }

  public static class PharmacyCopay {
    public PokitMoney copayment;
    public String tier;
    public String type;
    public List<String> notes;
    public String supply;
  }

  public static class BenefitsManager extends PokitPayer {
    public String coverageDescription;
  }

}
