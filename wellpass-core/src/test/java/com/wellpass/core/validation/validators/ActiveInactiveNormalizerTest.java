package com.wellpass.core.validation.validators;

import static com.wellpass.core.validation.validators.StatusValidators.STATUS_ACTIVE;
import static com.wellpass.core.validation.validators.StatusValidators.STATUS_INACTIVE;

import com.wellpass.core.annotations.UnitTests;
import org.junit.experimental.categories.Category;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;

@Category(UnitTests.class)
public class ActiveInactiveNormalizerTest extends
    AbstractConvertingValidatorTest<String, String> {

  @Parameters
  public static Iterable<Object[]> data() {
      return Arrays.asList(new Object[][] {
        { "active", STATUS_ACTIVE, null, StatusValidators.ACTIVE_INACTIVE_NORMALIZER },
        { "Active", STATUS_ACTIVE, null, StatusValidators.ACTIVE_INACTIVE_NORMALIZER },
        { "ACTIVE", STATUS_ACTIVE, null, StatusValidators.ACTIVE_INACTIVE_NORMALIZER },

        { "inactive", STATUS_INACTIVE, null, StatusValidators.ACTIVE_INACTIVE_NORMALIZER },
        { "Inactive", STATUS_INACTIVE, null, StatusValidators.ACTIVE_INACTIVE_NORMALIZER },
        { "INACTIVE", STATUS_INACTIVE, null, StatusValidators.ACTIVE_INACTIVE_NORMALIZER },

        { null, null, ValidationException.class, StatusValidators.ACTIVE_INACTIVE_NORMALIZER },
        { "A", null, ValidationException.class, StatusValidators.ACTIVE_INACTIVE_NORMALIZER },
        { "I", null, ValidationException.class, StatusValidators.ACTIVE_INACTIVE_NORMALIZER }
      });
  }

  public ActiveInactiveNormalizerTest(
      String input,
      String output,
      Class<?> exceptionClz,
      ConvertingValidator<String, String> converter) {
    super(input, output, exceptionClz, converter);
  }

}
