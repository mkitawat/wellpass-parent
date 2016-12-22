package com.wellpass.core.validation.validators;

import static com.wellpass.core.validation.validators.ValidationException.ERR_CODE_INVALID_VALUE;
import static com.wellpass.core.validation.validators.ValidationException.ERR_CODE_MISSING;

public class GenderValidators {
  public static final String GENDER_M = "M";
  public static final String GENDER_F = "F";
  public static final String GENDER_UN = "UN";
  public static final String GENDER_UNK = "UNK";

  public static final ConvertingValidator<String, String> GENDER_NORMALIZER =
      singleLetterNormalizer();
  public static final ConvertingValidator<String, String> NULLSAFE_GENDER_NORMALIZER =
      GENDER_NORMALIZER.nullSafe(null);
  public static final ConvertingValidator<String, String> UNKNOWN_GENDER_NORMALIZER =
      unknownGenderNormalizer();
  public static final ConvertingValidator<String, String> NULLSAFE_UNKNOWN_GENDER_NORMALIZER =
      UNKNOWN_GENDER_NORMALIZER.nullSafe(null);

  public static ConvertingValidator<String, String> singleLetterNormalizer() {
    return GenderValidators::singleLetterNormalize;
  }

  public static ConvertingValidator<String, String> unknownGenderNormalizer() {
    return GenderValidators::normalizeUnknownGender;
  }

  public static String singleLetterNormalize(String gender) throws ValidationException {
    try {
      gender = gender.toUpperCase();
    } catch (NullPointerException ex) {
      throw new ValidationException(ERR_CODE_MISSING);
    }

    switch(gender.length()) {
    case 1:
      if (GENDER_M.equals(gender)) return GENDER_M;
      if (GENDER_F.equals(gender)) return GENDER_F;
      throw new ValidationException(ERR_CODE_INVALID_VALUE);
    default:
      if ("MALE".equals(gender)) return GENDER_M;
      if ("FEMALE".equals(gender)) return GENDER_F;
      throw new ValidationException(ERR_CODE_INVALID_VALUE);
    }
  }

  public static String normalizeUnknownGender(String gender) throws ValidationException {
    try {
      gender = gender.toUpperCase();
    } catch (NullPointerException ex) {
      throw new ValidationException(ERR_CODE_MISSING);
    }

    switch(gender.length()) {
    case 1:
      if (GENDER_M.equals(gender)) return GENDER_M;
      if (GENDER_F.equals(gender)) return GENDER_F;
      throw new ValidationException(ERR_CODE_INVALID_VALUE);
    case 2:
      if (GENDER_UN.equals(gender)) return GENDER_UN;
      throw new ValidationException(ERR_CODE_INVALID_VALUE);
    case 3:
      if (GENDER_UNK.equals(gender)) return GENDER_UNK;
      throw new ValidationException(ERR_CODE_INVALID_VALUE);
    default:
      if ("MALE".equals(gender)) return GENDER_M;
      if ("FEMALE".equals(gender)) return GENDER_F;
      if ("OTHER".equals(gender)) return GENDER_UN;
      if ("UNKNOWN".equals(gender)) return GENDER_UNK;
      throw new ValidationException(ERR_CODE_INVALID_VALUE);
    }
  }

}
