package com.wellpass.core.models.coverage;

import com.wellpass.core.models.auth.Address;

import java.time.LocalDate;
import java.util.List;

public class MemberInfo {
  public String firstName;

  public String lastName;

  public String middleName;

  public String gender;

  public LocalDate dateOfBirth;

  public String ssn;

  public String language;

  public String employerName;

  public List<Address> addresses;

  public List<PhoneNumber> phoneNumbers;

  public String email;
}
