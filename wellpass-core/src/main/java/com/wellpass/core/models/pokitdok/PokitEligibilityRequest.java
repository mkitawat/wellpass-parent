package com.wellpass.core.models.pokitdok;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PokitEligibilityRequest extends PokitBaseRequest {

  public PokitMember member;
  public PokitProvider provider;
  public String tradingPartnerId;
  public List<String> serviceTypes;
  public PokitPayer payer;
  public String procedureId;
  public String procedureIdQualifier;
  public String cptCode;
  public String transactionCode;

  public static PokitEligibilityRequest createFrom(
    String memberId,
    String memberFirstName,
    String memberLastName,
    LocalDate memberBirthDate,
    String providerNpi,
    String tradingPartnerId,
    Collection<String> serviceTypes) {

    PokitEligibilityRequest request = new PokitEligibilityRequest();
    request.member = new PokitMember();
    request.member.id = memberId;
    request.member.firstName = memberFirstName;
    request.member.lastName = memberLastName;
    request.member.birthDate = memberBirthDate;

    if (providerNpi != null) {
      request.provider = new PokitProvider();
      request.provider.npi = providerNpi;
    }

    if (serviceTypes != null) {
      request.serviceTypes = new ArrayList<>(serviceTypes);
    }

    request.tradingPartnerId = tradingPartnerId;

    return request;
  }

  @Override
  public String getTargetPath() {
    return "eligibility/";
  }

  /**
   * Per the X12 Specification, requested member data needs to meet one of these search options:
   * [memberFirstName, memberLastName, memberDob]
   * [memberLastName, memberId, memberDob]
   * [memberFirstName, memberLastName, memberId]
   * [memberId, memberDob]
   * [memberFirstName, memberLastName, memberId, memberDob]
   */
  @Override
  public void validate() throws IllegalArgumentException {
    if (this.member == null) {
      throw new IllegalArgumentException("PokitDok Eligiblity request - member info required");
    }
    if (this.member.id == null && this.member.birthDate == null) {
      throw new IllegalArgumentException(
        "PokitDoc Eligiblity Request - member.id or member.birthDate required");
    }
    if (this.member.id == null || this.member.birthDate == null) {
      if (this.member.firstName == null || this.member.lastName == null) {
        throw new IllegalArgumentException(
          "PokitDoc Eligiblity Request - member.firstName and member.lastName both required when either of id or birthDate is missing");
      }
    }
  }

}
