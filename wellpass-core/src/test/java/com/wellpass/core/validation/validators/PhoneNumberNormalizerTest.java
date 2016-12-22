package com.wellpass.core.validation.validators;

import static com.wellpass.core.validation.validators.PhoneNumberValidators.NULLSAFE_US_E164_NORMALIZER;
import static com.wellpass.core.validation.validators.PhoneNumberValidators.US_E164_NORMALIZER;

import com.wellpass.core.annotations.UnitTests;
import org.junit.experimental.categories.Category;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;

@Category(UnitTests.class)
public class PhoneNumberNormalizerTest extends
    AbstractConvertingValidatorTest<String, String> {

  @Parameters
  public static Iterable<Object[]> data() {
      return Arrays.asList(new Object[][] {
        { "2024190130", "+12024190130", null, US_E164_NORMALIZER },
        { "202-419-0130", "+12024190130", null, US_E164_NORMALIZER },
        { "202 419 0130", "+12024190130", null, US_E164_NORMALIZER },
        { "(202) 419 0130", "+12024190130", null, US_E164_NORMALIZER },
        { "12024190130", "+12024190130", null, US_E164_NORMALIZER },
        { "+1 202-419-0130", "+12024190130", null, US_E164_NORMALIZER },
        { "+1 202 419 0130", "+12024190130", null, US_E164_NORMALIZER },
        { "+1 (202)-419-0130", "+12024190130", null, US_E164_NORMALIZER },
        { "(+1) (202)-419-0130", "+12024190130", null, US_E164_NORMALIZER },
        { "+1 (202) 419 0130", "+12024190130", null, US_E164_NORMALIZER },
        { null, null, null, NULLSAFE_US_E164_NORMALIZER },

        { null, null, ValidationException.class, US_E164_NORMALIZER },
        { "1", null, ValidationException.class, US_E164_NORMALIZER },
        { "foo@dot", null, ValidationException.class, US_E164_NORMALIZER },
        { "1+1+1+1", null, ValidationException.class, US_E164_NORMALIZER }
      });
  }

  public PhoneNumberNormalizerTest(
      String input,
      String output,
      Class<?> exceptionClz,
      ConvertingValidator<String, String> converter) {
    super(input, output, exceptionClz, converter);
  }

}
