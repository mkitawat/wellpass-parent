package com.wellpass.core.validation.validators;

import static com.wellpass.core.validation.validators.ValidationException.ERR_CODE_INVALID_VALUE;
import static com.wellpass.core.validation.validators.ValidationException.ERR_CODE_MISSING;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Map;
import java.util.Set;

public class StatusValidators {
  public static final String STATUS_ACTIVE = "ACTIVE";
  public static final String STATUS_INACTIVE = "INACTIVE";

  public static final ConvertingValidator<String, String> ACTIVE_INACTIVE_NORMALIZER =
      activeInactiveNormalizer();

  public static final ConvertingValidator<String, Boolean> ACTIVE_INACTIVE_BOOLEAN_CONVERTER =
      activeInactiveBooleanConverter();

  public static ConvertingValidator<String, String> activeInactiveNormalizer() {
    return statusNormalizer(ImmutableSet.of(STATUS_ACTIVE, STATUS_INACTIVE));
  }

  public static ConvertingValidator<String, Boolean> activeInactiveBooleanConverter() {
    return booleanConverter(ImmutableMap.of(STATUS_ACTIVE, Boolean.TRUE, STATUS_INACTIVE, Boolean.FALSE));
  }

  public static ConvertingValidator<String, String> statusNormalizer(
      Set<String> validStatuses) {
    return status -> {
      try {
        status = status.toUpperCase();
      } catch (NullPointerException npe) {
        throw new ValidationException(ERR_CODE_MISSING);
      }
      if (validStatuses.contains(status)) return status;
      throw new ValidationException(ERR_CODE_INVALID_VALUE);
    };
  }

  public static ConvertingValidator<String, Boolean> booleanConverter(
      Map<String, Boolean> validStatuses) {
    return status -> {
      try {
        status = status.toUpperCase();
      } catch (NullPointerException npe) {
        throw new ValidationException(ERR_CODE_MISSING);
      }
      Boolean conversion = validStatuses.get(status);
      if (conversion != null) return conversion;
      throw new ValidationException(ERR_CODE_INVALID_VALUE);
    };
  }

}
