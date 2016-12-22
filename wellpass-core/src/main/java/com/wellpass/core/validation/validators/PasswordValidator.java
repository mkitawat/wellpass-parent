package com.wellpass.core.validation.validators;

import java.util.regex.Pattern;

public class PasswordValidator {
  public static final String ERR_CODE_INVALID_PASSWORD = "INVALID_PASSWORD";
  public static final String ERR_MESSAGE_INVALID_PASSWORD =
          "Must be at least 8 characters long, contain at least one letter and at least one digit.";
  private static Pattern PASSWORD_PATTERN = Pattern.compile("((?=.*\\d)(?=.*[A-Za-z]).{8,30})");

  public static void regexValidate(String password) throws ValidationException {
    try {
      if (!PASSWORD_PATTERN.matcher(password).matches()) {
        throwPasswordError();
      }
    } catch (NullPointerException ex) {
      throwPasswordError();
    }
  }

  private static void throwPasswordError() throws ValidationException {
    throw new ValidationException(ERR_CODE_INVALID_PASSWORD, ERR_MESSAGE_INVALID_PASSWORD);
  }
}
