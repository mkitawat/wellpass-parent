package com.wellpass.core.validation.validators;

import static com.wellpass.core.validation.validators.NotNullValidators.STRING_NOTNULL;

import com.wellpass.core.annotations.UnitTests;
import org.junit.experimental.categories.Category;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;

@Category(UnitTests.class)
public class NotNullValidatorTest extends AbstractValidatorTest<String> {

  @Parameters
  public static Iterable<Object[]> data() {
      return Arrays.asList(new Object[][] {
        { "", null, STRING_NOTNULL },
        { " ", null, STRING_NOTNULL },
        { "foo", null, STRING_NOTNULL },

        { null, ValidationException.class, STRING_NOTNULL },
      });
  }

  public NotNullValidatorTest(
      String input,
      Class<?> exceptionClz,
      Validator<String> validator) {
    super(input, exceptionClz, validator);
  }

}
