package com.wellpass.core.validation.validators;

import static com.wellpass.core.validation.validators.IntegerValidators.INTEGER_CONVERTER;
import static com.wellpass.core.validation.validators.IntegerValidators.NULLSAFE_INTEGER_CONVERTER;

import com.wellpass.core.annotations.UnitTests;
import org.junit.experimental.categories.Category;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;

@Category(UnitTests.class)
public class IntegerConverterTest extends
  AbstractConvertingValidatorTest<String, Integer> {

  @Parameters
  public static Iterable<Object[]> data() {
      return Arrays.asList(new Object[][] {
        { "0", 0, null, INTEGER_CONVERTER },
        { "1", 1, null, INTEGER_CONVERTER },
        { "-1", -1, null, INTEGER_CONVERTER },
        { Integer.MIN_VALUE + "", Integer.MIN_VALUE, null, INTEGER_CONVERTER },
        { Integer.MAX_VALUE + "", Integer.MAX_VALUE, null, INTEGER_CONVERTER },
        { null, null, null, NULLSAFE_INTEGER_CONVERTER },

        { null, null, ValidationException.class, INTEGER_CONVERTER },
        { "1.3", null, ValidationException.class, INTEGER_CONVERTER },
        { "1.e", null, ValidationException.class, INTEGER_CONVERTER },
        { "12ab", null, ValidationException.class, INTEGER_CONVERTER }
      });
  }

  public IntegerConverterTest(
      String input,
      Integer output,
      Class<?> exceptionClz,
      ConvertingValidator<String, Integer> converter) {
    super(input, output, exceptionClz, converter);
  }
}
