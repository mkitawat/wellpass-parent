package com.wellpass.core.validation.validators;

import static com.wellpass.core.validation.validators.ZipcodeValidators.DEFAULT_ZIPCODE_NORMALIZER;
import static com.wellpass.core.validation.validators.ZipcodeValidators.US_ZIPCODE_NORMALIZER;

import com.wellpass.core.annotations.UnitTests;
import org.junit.experimental.categories.Category;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;

@Category(UnitTests.class)
public class ZipcodeNormalizerTest extends
    AbstractConvertingValidatorTest<String, String> {

  @Parameters
  public static Iterable<Object[]> data() {
      return Arrays.asList(new Object[][] {
        { "07503", "07503", null, US_ZIPCODE_NORMALIZER },
        { "075031234", "075031234", null, US_ZIPCODE_NORMALIZER },
        { "07503-1234", "075031234", null, US_ZIPCODE_NORMALIZER },
        { "07503 1234", "075031234", null, US_ZIPCODE_NORMALIZER },
        { null, null, null, US_ZIPCODE_NORMALIZER.nullSafe(null) },

        { "400010", "400010", null, DEFAULT_ZIPCODE_NORMALIZER },

        { null, null, ValidationException.class, US_ZIPCODE_NORMALIZER },
        { "invalidzipcode", null, ValidationException.class, US_ZIPCODE_NORMALIZER },
        { "0750", null, ValidationException.class, US_ZIPCODE_NORMALIZER },
        { "07503123", null, ValidationException.class, US_ZIPCODE_NORMALIZER },
        { "0750-123", null, ValidationException.class, US_ZIPCODE_NORMALIZER },

        { "invalidzipcode", null, ValidationException.class, DEFAULT_ZIPCODE_NORMALIZER },
        { "70ab80", null, ValidationException.class, DEFAULT_ZIPCODE_NORMALIZER },
        { "0123456789123456789", null, ValidationException.class, DEFAULT_ZIPCODE_NORMALIZER },
      });
  }

  public ZipcodeNormalizerTest(
      String input,
      String output,
      Class<?> exceptionClz,
      ConvertingValidator<String, String> converter) {
    super(input, output, exceptionClz, converter);
  }

}
