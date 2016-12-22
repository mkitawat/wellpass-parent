package com.wellpass.core.validation.validators;

import static com.wellpass.core.validation.validators.ValidationException.ERR_CODE_INVALID_VALUE;
import static com.wellpass.core.validation.validators.ValidationException.ERR_CODE_MISSING;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class LanguageValidators {

  public static final String LANG_ENGLISH = "ENGLISH";
  public static final String LANG_SPANISH = "SPANISH";

  public static Set<String> WP_SUPPORTED_LANGUAGES = ImmutableSet.of(
      LANG_ENGLISH, LANG_SPANISH);

  public static final ConvertingValidator<String, String> LANGUAGE_NORMALIZER =
      normalizer(WP_SUPPORTED_LANGUAGES);

  public static final ConvertingValidator<String, String> LANGUAGE_NORMALIZER_EN_DEFAULT =
      LANGUAGE_NORMALIZER.nullAndErrorSafe(LANG_ENGLISH);

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
