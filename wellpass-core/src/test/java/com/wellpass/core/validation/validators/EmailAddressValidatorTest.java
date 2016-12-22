package com.wellpass.core.validation.validators;

import static com.wellpass.core.validation.validators.EmailAddressValidators.EMAIL_VALIDATOR;
import static com.wellpass.core.validation.validators.EmailAddressValidators.NULLSAFE_EMAIL_VALIDATOR;

import com.wellpass.core.annotations.UnitTests;
import org.junit.experimental.categories.Category;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;

@Category(UnitTests.class)
public class EmailAddressValidatorTest extends AbstractValidatorTest<String> {

  @Parameters
  public static Iterable<Object[]> data() {
      return Arrays.asList(new Object[][] {
        { "foo@dot.com", null, EMAIL_VALIDATOR },
        { "foo.bar@dot.co.in", null, EMAIL_VALIDATOR },
        { "foo-bar@dot.org", null, EMAIL_VALIDATOR },
        { "foo_bar@dot.gov", null, EMAIL_VALIDATOR },
        { "foo_bar1963@dot.com", null, EMAIL_VALIDATOR },
        { "Foo_Bar_1963@dot.com", null, EMAIL_VALIDATOR },
        { "Foo_Bar_long_Long_more_more_more@dot.com", null, EMAIL_VALIDATOR },
        { null, null, NULLSAFE_EMAIL_VALIDATOR },

        { null, ValidationException.class, EMAIL_VALIDATOR },
        { "foo", ValidationException.class, EMAIL_VALIDATOR },
        { "foo@", ValidationException.class, EMAIL_VALIDATOR },
        { "foo@dot@", ValidationException.class, EMAIL_VALIDATOR },
        { "@foo", ValidationException.class, EMAIL_VALIDATOR },

        // Not sure if this is valid syntax.
        { "foo@dot", null, EMAIL_VALIDATOR },
      });
  }

  public EmailAddressValidatorTest(
      String input,
      Class<?> exceptionClz,
      Validator<String> validator) {
    super(input, exceptionClz, validator);
  }

}
