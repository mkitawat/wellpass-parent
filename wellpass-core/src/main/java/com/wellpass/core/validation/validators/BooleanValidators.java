package com.wellpass.core.validation.validators;

import static com.wellpass.core.validation.validators.ValidationException.ERR_CODE_INVALID_VALUE;
import static com.wellpass.core.validation.validators.ValidationException.ERR_CODE_MISSING;

public class BooleanValidators {

  public static final ConvertingValidator<String, Boolean> YESNO_CONVERTER =
      yesNoConverter();

  public static final ConvertingValidator<String, Boolean> NULLSAFE_YESNO_CONVERTER =
      YESNO_CONVERTER.nullSafe(null);

  public static final ConvertingValidator<String, Boolean> NULLSAFE_TRUE_YESNO_CONVERTER =
      YESNO_CONVERTER.nullSafe(Boolean.TRUE);

  public static final ConvertingValidator<String, Boolean> NULLSAFE_FALSE_YESNO_CONVERTER =
      YESNO_CONVERTER.nullSafe(Boolean.FALSE);

  public static ConvertingValidator<String, Boolean> yesNoConverter() {
    return BooleanValidators::convertYesNo;
  }

  public static Boolean convertYesNo(String yesNo) throws ValidationException {
    try {
      yesNo = yesNo.toUpperCase();
    } catch (NullPointerException npe) {
      throw new ValidationException(ERR_CODE_MISSING);
    }

    switch(yesNo.length()) {
    case 1:
      if ("Y".equals(yesNo)) return Boolean.TRUE;
      if ("N".equals(yesNo)) return Boolean.FALSE;
      throw new ValidationException(ERR_CODE_INVALID_VALUE);
    default:
      if ("YES".equals(yesNo)) return Boolean.TRUE;
      if ("NO".equals(yesNo)) return Boolean.FALSE;
      throw new ValidationException(ERR_CODE_INVALID_VALUE);
    }
  }

}
