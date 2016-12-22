package com.wellpass.core.models.coverage;

public class PhoneNumber {

  public String number;
  // TODO String carrier field to capture any type of carrier?
  public Boolean tracFone;

  public PhoneNumber() {
  }

  public PhoneNumber(String number) {
    this.number = number;
  }

  public PhoneNumber(String number, Boolean tracFone) {
    this(number);
    this.tracFone = tracFone;
  }
}
