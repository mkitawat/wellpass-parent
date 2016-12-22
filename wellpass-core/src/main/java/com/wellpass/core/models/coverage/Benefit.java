package com.wellpass.core.models.coverage;

import java.math.BigDecimal;

public class Benefit {

  public Network network;

  public String serviceCode;

  public String serviceDesc;

  public Deductible deductible;

  public Money copay;

  public BigDecimal coinsurance;

  public OutOfPocket outOfPocket;

  public Money limitation;

}
