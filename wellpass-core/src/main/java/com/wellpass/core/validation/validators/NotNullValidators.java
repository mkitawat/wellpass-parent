package com.wellpass.core.validation.validators;

import static com.wellpass.core.validation.validators.ValidationException.ERR_CODE_MISSING;

import org.apache.commons.lang3.StringUtils;

public class NotNullValidators {

  public static final Validator<Object> NOTNULL_VALIDATOR = notNull();

  public static final Validator<String> STRING_NOTNULL = notNullString();

  public static final Validator<String> STRING_NOTEMPTY = notEmptyString();

  public static Validator<Object> notNull() {
    return s -> {
      if (s == null) {
        throw new ValidationException(ERR_CODE_MISSING);
      }
    };
  }

  public static <S> Validator<S> notNull(Class<S> clz) {
    return s -> {
      if (s == null) {
        throw new ValidationException(ERR_CODE_MISSING);
      }
    };
  }

  public static Validator<String> notNullString() {
    return notNull(String.class);
  }

  public static Validator<String> notEmptyString() {
    return s -> {
      if (StringUtils.isEmpty(s)) {
        throw new ValidationException(ERR_CODE_MISSING);
      }
    };
  }

}
