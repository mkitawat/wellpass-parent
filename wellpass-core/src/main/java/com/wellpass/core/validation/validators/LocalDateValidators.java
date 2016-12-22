package com.wellpass.core.validation.validators;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateValidators {
  public static final String ERR_CODE_INVALID_DATEFORMAT = "INVALID_DATEFORMAT";

  public static ConvertingValidator<String, LocalDate> converterWithPattern(
      String pattern) {
    return converter(DateTimeFormatter.ofPattern(pattern));
  }

  public static ConvertingValidator<String, LocalDate> converter(
      DateTimeFormatter formatter) {
    return date -> {
      try {
        return LocalDate.parse(date, formatter);
      } catch (Exception ex) {
        throw new ValidationException(ERR_CODE_INVALID_DATEFORMAT, ex.getMessage(), ex);
      }
    };
  }

}
