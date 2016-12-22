package com.wellpass.core.validation.validators;

import static com.wellpass.core.validation.validators.ValidationException.ERR_CODE_INVALID_FORMAT;
import static com.wellpass.core.validation.validators.ValidationException.ERR_CODE_MISSING;
import static com.wellpass.core.validation.validators.ValidationException.ERR_CODE_PARSING;

import java.util.regex.Pattern;

public class SsnValidators {
  public static final Pattern DEFAULT_PATTERN = Pattern.compile("\\d{4}|\\d{9}");
  public static final Pattern FOUR_DIGIT_PATTERN = Pattern.compile("\\d{4}");
  public static final Pattern NINE_DIGIT_PATTERN = Pattern.compile("\\d{9}");

  public static final ConvertingValidator<String, String> DEFAULT_SSN_NORMALIZER =
      normalizerWithPattern(DEFAULT_PATTERN);

  public static ConvertingValidator<String, String> normalizerWithPattern(
      Pattern pattern) {
    return ssn -> {
      try {
        ssn = ssn.replace("-", "").replace(" ", "");
        if (pattern.matcher(ssn).matches()) {
          return ssn;
        }
        throw new ValidationException(ERR_CODE_INVALID_FORMAT);
      } catch (NullPointerException npe) {
        throw new ValidationException(ERR_CODE_MISSING);
      } catch(ValidationException ex) {
        throw ex;
      } catch (Exception ex) {
        throw new ValidationException(ERR_CODE_PARSING, ex);
      }
    };
  }

}
