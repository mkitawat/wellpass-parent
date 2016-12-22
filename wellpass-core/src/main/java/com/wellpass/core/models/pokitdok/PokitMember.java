package com.wellpass.core.models.pokitdok;

import java.time.LocalDate;

public class PokitMember {
  public String id;
  public String firstName;
  public String middleName;
  public String lastName;
  public String suffix;
  public String gender;
  public LocalDate birthDate;

  /*
   * This is are optional attributes specified in EligibilityRequest.member.
   * Create an inner subclass in EligibilityRequest if following attributes are required.
   */
  //public LocalDate planStartDate;
  //public String groupNumber;
}
