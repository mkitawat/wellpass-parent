package com.wellpass.core.validation.validators;

import static com.wellpass.core.validation.validators.ValidationException.ERR_CODE_INVALID_VALUE;
import static com.wellpass.core.validation.validators.ValidationException.ERR_CODE_MISSING;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class SubscriberRelationshipValidators {

  public static final String REL_MEMBER = "M";
  public static final String REL_DEPENDENT = "D";
  public static final String REL_SPOUSE = "S";

  public static Set<String> WP_SUPPORTED_RELATIONSHIPS = ImmutableSet.of(
      REL_MEMBER, REL_DEPENDENT, REL_SPOUSE);

  public static final ConvertingValidator<String, String> SUBSCRIBER_RELATIONSHIP_NORMALIZER =
      normalizer(WP_SUPPORTED_RELATIONSHIPS);

  public static ConvertingValidator<String, String> normalizer(
      Set<String> supportedLanguages) {
    return language -> {
      try {
        language = language.toUpperCase();
      } catch (NullPointerException npe) {
        throw new ValidationException(ERR_CODE_MISSING);
      }
      if (supportedLanguages.contains(language)) return language;
      throw new ValidationException(ERR_CODE_INVALID_VALUE);
    };
  }

}
