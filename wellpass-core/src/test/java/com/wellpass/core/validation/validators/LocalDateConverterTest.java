package com.wellpass.core.validation.validators;

import com.wellpass.core.annotations.UnitTests;
import org.junit.experimental.categories.Category;
import org.junit.runners.Parameterized.Parameters;

import java.time.LocalDate;
import java.util.Arrays;

@Category(UnitTests.class)
public class LocalDateConverterTest extends
  AbstractConvertingValidatorTest<String, LocalDate> {

  @Parameters
  public static Iterable<Object[]> data() {
    ConvertingValidator<String, LocalDate> localDateConverter =
        LocalDateValidators.converterWithPattern("M/d/yyyy");
      return Arrays.asList(new Object[][] {
        { "2/28/2016", LocalDate.parse("2016-02-28"), null, localDateConverter },
        { "2/9/2016", LocalDate.parse("2016-02-09"), null, localDateConverter },
        { "02/29/2016", LocalDate.parse("2016-02-29"), null, localDateConverter },

        { null, null, null, localDateConverter.nullSafe(null) },
        { null, null, null, localDateConverter.errorSafe(null) },
        { "2016-02-29", null, null, localDateConverter.errorSafe(null) },
        { "2016-02-29", LocalDate.now(), null, localDateConverter.errorSafe(LocalDate.now()) },

        { null, null, ValidationException.class, localDateConverter },
        { null, null, ValidationException.class, localDateConverter },
        { "2016-02-29", null, ValidationException.class, localDateConverter },
        { "2016-02-29", null, ValidationException.class, localDateConverter }
      });
  }

  public LocalDateConverterTest(
      String input,
      LocalDate output,
      Class<?> exceptionClz,
      ConvertingValidator<String, LocalDate> converter) {
    super(input, output, exceptionClz, converter);
  }

}
