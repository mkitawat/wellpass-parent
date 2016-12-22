package com.wellpass.core.validation.validators;

import static com.wellpass.core.validation.validators.ValidationException.ERR_CODE_INVALID_FORMAT;
import static com.wellpass.core.validation.validators.ValidationException.ERR_CODE_MISSING;
import static com.wellpass.core.validation.validators.ValidationException.ERR_CODE_PARSING;

import java.util.regex.Pattern;

public class ZipcodeValidators {
  private static final Pattern DEFAULT_PATTERN = Pattern.compile("\\d{1,16}");
  private static final Pattern US_ZIPCODE_PATTERN = Pattern.compile("\\d{5}(\\d{4})?");

  public static final ConvertingValidator<String, String> DEFAULT_ZIPCODE_NORMALIZER =
      normalizerWithPattern(DEFAULT_PATTERN);

  public static final ConvertingValidator<String, String> US_ZIPCODE_NORMALIZER =
      normalizerWithPattern(US_ZIPCODE_PATTERN);

  public static ConvertingValidator<String, String> normalizerWithPattern(
      Pattern pattern) {
    return zipCode -> {
      try {
        zipCode = zipCode.replace("-", "").replace(" ", "");
        if (pattern.matcher(zipCode).matches()) {
          return zipCode;
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
