package com.wellpass.core.validation.validators;

import static com.wellpass.core.validation.validators.ValidationException.ERR_CODE_INVALID_FORMAT;
import static com.wellpass.core.validation.validators.ValidationException.ERR_CODE_MISSING;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public class PhoneNumberValidators {
  public static final String DEFAULT_REGION = "US";
  private static final PhoneNumberUtil PHONE_NUMBER_UTIL = PhoneNumberUtil.getInstance();

  public static final ConvertingValidator<String, String> US_E164_NORMALIZER =
      e164Normalizer();
  public static final ConvertingValidator<String, String> NULLSAFE_US_E164_NORMALIZER =
      US_E164_NORMALIZER.nullSafe(null);

  public static ConvertingValidator<String, String> e164Normalizer() {
    return normalizer(DEFAULT_REGION, PhoneNumberFormat.E164);
  }

  public static ConvertingValidator<String, String> e164Normalizer(String region) {
    return normalizer(region, PhoneNumberFormat.E164);
  }

  public static ConvertingValidator<String, String> normalizer(
      String region, PhoneNumberFormat phoneNumberFormat) {
    return phoneNumber -> {
      try {
        PhoneNumber parsedNumber = PHONE_NUMBER_UTIL.parse(phoneNumber, region);
        return PHONE_NUMBER_UTIL.format(parsedNumber, phoneNumberFormat);
      } catch (NullPointerException npe) {
        throw new ValidationException(ERR_CODE_MISSING, "Please enter a valid phone number.");
      } catch (NumberParseException ex) {
        String errorCode =
            ex.getErrorType() != null ? ex.getErrorType().name() : ERR_CODE_INVALID_FORMAT;
        throw new ValidationException(errorCode, "Please enter a valid phone number.", ex);
      }
    };
  }

}
