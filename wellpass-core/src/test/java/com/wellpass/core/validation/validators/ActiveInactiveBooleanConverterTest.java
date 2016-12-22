package com.wellpass.core.validation.validators;

import com.wellpass.core.annotations.UnitTests;
import org.junit.experimental.categories.Category;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;

@Category(UnitTests.class)
public class ActiveInactiveBooleanConverterTest extends
    AbstractConvertingValidatorTest<String, Boolean> {

  @Parameters
  public static Iterable<Object[]> data() {
      return Arrays.asList(new Object[][] {
        { "active", Boolean.TRUE, null, StatusValidators.ACTIVE_INACTIVE_BOOLEAN_CONVERTER },
        { "Active", Boolean.TRUE, null, StatusValidators.ACTIVE_INACTIVE_BOOLEAN_CONVERTER },
        { "ACTIVE", Boolean.TRUE, null, StatusValidators.ACTIVE_INACTIVE_BOOLEAN_CONVERTER },

        { "inactive", Boolean.FALSE, null, StatusValidators.ACTIVE_INACTIVE_BOOLEAN_CONVERTER },
        { "Inactive", Boolean.FALSE, null, StatusValidators.ACTIVE_INACTIVE_BOOLEAN_CONVERTER },
        { "INACTIVE", Boolean.FALSE, null, StatusValidators.ACTIVE_INACTIVE_BOOLEAN_CONVERTER },

        { null, null, ValidationException.class, StatusValidators.ACTIVE_INACTIVE_BOOLEAN_CONVERTER },
        { "A", null, ValidationException.class, StatusValidators.ACTIVE_INACTIVE_BOOLEAN_CONVERTER },
        { "I", null, ValidationException.class, StatusValidators.ACTIVE_INACTIVE_BOOLEAN_CONVERTER }
      });
  }

  public ActiveInactiveBooleanConverterTest(
      String input,
      Boolean output,
      Class<?> exceptionClz,
      ConvertingValidator<String, Boolean> converter) {
    super(input, output, exceptionClz, converter);
  }

}
