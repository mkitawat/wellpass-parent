package com.wellpass.core.models.coverage;

import com.wellpass.core.enums.CodedEnum;
import com.wellpass.core.models.PokitInPlanNetwork;

import java.util.EnumSet;
import java.util.Set;

public enum Network {
  IN_NETWORK,
  OUT_NETWORK;

  public static Set<Network> fromPokitInPlanNetwork(String inPlanNetwork) {
    if (inPlanNetwork == null) {
      return EnumSet.of(IN_NETWORK, OUT_NETWORK);
    }
    return fromPokitInPlanNetwork(CodedEnum.fromCode(PokitInPlanNetwork.class, inPlanNetwork));
  }

  public static Set<Network> fromPokitInPlanNetwork(PokitInPlanNetwork inPlanNetwork) {
    if (inPlanNetwork == null) {
      return EnumSet.of(IN_NETWORK, OUT_NETWORK);
    }
    switch (inPlanNetwork) {
      case yes:
        return EnumSet.of(IN_NETWORK);
      case no:
        return EnumSet.of(OUT_NETWORK);
      case not_applicable:
        return EnumSet.of(IN_NETWORK, OUT_NETWORK);
      default:
        return EnumSet.noneOf(Network.class);
    }
  }
}
