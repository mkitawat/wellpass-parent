package com.wellpass.core.validation.validators;

import static com.wellpass.core.validation.validators.BooleanValidators.NULLSAFE_FALSE_YESNO_CONVERTER;
import static com.wellpass.core.validation.validators.BooleanValidators.NULLSAFE_TRUE_YESNO_CONVERTER;
import static com.wellpass.core.validation.validators.BooleanValidators.NULLSAFE_YESNO_CONVERTER;
import static com.wellpass.core.validation.validators.BooleanValidators.YESNO_CONVERTER;

import com.wellpass.core.annotations.UnitTests;
import org.junit.experimental.categories.Category;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;

@Category(UnitTests.class)
public class YesNoConverterTest extends
    AbstractConvertingValidatorTest<String, Boolean> {

  @Parameters
  public static Iterable<Object[]> data() {
      return Arrays.asList(new Object[][] {
        { "y", Boolean.TRUE, null, YESNO_CONVERTER },
        { "Y", Boolean.TRUE, null, YESNO_CONVERTER },
        { "Yes", Boolean.TRUE, null, YESNO_CONVERTER },
        { "YES", Boolean.TRUE, null, YESNO_CONVERTER },

        { "n", Boolean.FALSE, null, YESNO_CONVERTER },
        { "N", Boolean.FALSE, null, YESNO_CONVERTER },
        { "No", Boolean.FALSE, null, YESNO_CONVERTER },
        { "NO", Boolean.FALSE, null, YESNO_CONVERTER },

        { null, null, null, NULLSAFE_YESNO_CONVERTER },
        { "Y", Boolean.TRUE, null, NULLSAFE_YESNO_CONVERTER },
        { "N", Boolean.FALSE, null, NULLSAFE_YESNO_CONVERTER },

        { null, Boolean.TRUE, null, NULLSAFE_TRUE_YESNO_CONVERTER },
        { "Y", Boolean.TRUE, null, NULLSAFE_TRUE_YESNO_CONVERTER },
        { "N", Boolean.FALSE, null, NULLSAFE_TRUE_YESNO_CONVERTER },

        { null, Boolean.FALSE, null, NULLSAFE_FALSE_YESNO_CONVERTER },
        { "Y", Boolean.TRUE, null, NULLSAFE_FALSE_YESNO_CONVERTER },
        { "N", Boolean.FALSE, null, NULLSAFE_FALSE_YESNO_CONVERTER },

        { null, null, ValidationException.class, YESNO_CONVERTER },
        { "foo", null, ValidationException.class, YESNO_CONVERTER },

        { "foo", null, ValidationException.class, NULLSAFE_TRUE_YESNO_CONVERTER },
        { "foo", null, ValidationException.class, NULLSAFE_FALSE_YESNO_CONVERTER }
      });
  }

  public YesNoConverterTest(
      String input,
      Boolean output,
      Class<?> exceptionClz,
      ConvertingValidator<String, Boolean> converter) {
    super(input, output, exceptionClz, converter);
  }

}
