package com.wellpass.core.validation.validators;

import static com.wellpass.core.validation.validators.GenderValidators.GENDER_F;
import static com.wellpass.core.validation.validators.GenderValidators.GENDER_M;
import static com.wellpass.core.validation.validators.GenderValidators.GENDER_NORMALIZER;
import static com.wellpass.core.validation.validators.GenderValidators.NULLSAFE_GENDER_NORMALIZER;

import com.wellpass.core.annotations.UnitTests;
import org.junit.experimental.categories.Category;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;

@Category(UnitTests.class)
public class GenderNormalizerTest extends
    AbstractConvertingValidatorTest<String, String> {

  @Parameters
  public static Iterable<Object[]> data() {
      return Arrays.asList(new Object[][] {
        { "m", GENDER_M, null, GENDER_NORMALIZER },
        { "M", GENDER_M, null, GENDER_NORMALIZER },
        { "male", GENDER_M, null, GENDER_NORMALIZER },
        { "Male", GENDER_M, null, GENDER_NORMALIZER },
        { "MALE", GENDER_M, null, GENDER_NORMALIZER },

        { "f", GENDER_F, null, GENDER_NORMALIZER },
        { "F", GENDER_F, null, GENDER_NORMALIZER },
        { "female", GENDER_F, null, GENDER_NORMALIZER },
        { "Female", GENDER_F, null, GENDER_NORMALIZER },
        { "Female", GENDER_F, null, GENDER_NORMALIZER },

        { null, null, null, NULLSAFE_GENDER_NORMALIZER },

        { null, null, ValidationException.class, GENDER_NORMALIZER },
        { "mle", null, ValidationException.class, GENDER_NORMALIZER },
        { "ale", null, ValidationException.class, GENDER_NORMALIZER },
        { "true", null, ValidationException.class, GENDER_NORMALIZER }
      });
  }

  public GenderNormalizerTest(
      String input,
      String output,
      Class<?> exceptionClz,
      ConvertingValidator<String, String> converter) {
    super(input, output, exceptionClz, converter);
  }

}
