package com.wellpass.core.models.pokitdok;

import java.util.List;

public class PokitPrimaryCareProvider extends PokitProvider {
  public String phone;
  public String email;
  public String url;
  public String fax;
  public PokitAddress address;

  public List<PokitMessage> messages;
}
