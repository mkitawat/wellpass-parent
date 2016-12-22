package com.wellpass.core.validation.validators;

import static com.wellpass.core.validation.validators.ValidationException.ERR_CODE_INVALID_NUMBER;
import static com.wellpass.core.validation.validators.ValidationException.ERR_CODE_MISSING;

public class IntegerValidators {

  public static final ConvertingValidator<String, Integer> INTEGER_CONVERTER =
      converter();
  public static final ConvertingValidator<String, Integer> NULLSAFE_INTEGER_CONVERTER =
      INTEGER_CONVERTER.nullSafe(null);

  public static ConvertingValidator<String, Integer> converter() {
    return IntegerValidators::convert;
  }

  public static Integer convert(String s) throws ValidationException {
    try {
      return Integer.parseInt(s);
    } catch (NullPointerException npe) {
      throw new ValidationException(ERR_CODE_MISSING);
    } catch (Exception ex) {
      throw new ValidationException(ERR_CODE_INVALID_NUMBER);
    }
  }

}
