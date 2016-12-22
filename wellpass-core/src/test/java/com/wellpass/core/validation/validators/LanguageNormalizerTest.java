package com.wellpass.core.validation.validators;

import static com.wellpass.core.validation.validators.LanguageValidators.LANGUAGE_NORMALIZER;
import static com.wellpass.core.validation.validators.LanguageValidators.LANGUAGE_NORMALIZER_EN_DEFAULT;
import static com.wellpass.core.validation.validators.LanguageValidators.LANG_ENGLISH;
import static com.wellpass.core.validation.validators.LanguageValidators.LANG_SPANISH;

import com.wellpass.core.annotations.UnitTests;
import org.junit.experimental.categories.Category;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;

@Category(UnitTests.class)
public class LanguageNormalizerTest extends
  AbstractConvertingValidatorTest<String, String> {

  @Parameters
  public static Iterable<Object[]> data() {
      return Arrays.asList(new Object[][] {
        { "english", LANG_ENGLISH, null, LANGUAGE_NORMALIZER },
        { "English", LANG_ENGLISH, null, LANGUAGE_NORMALIZER },
        { "ENGLISH", LANG_ENGLISH, null, LANGUAGE_NORMALIZER },
        { "spanish", LANG_SPANISH, null, LANGUAGE_NORMALIZER },
        { "Spanish", LANG_SPANISH, null, LANGUAGE_NORMALIZER },
        { "SPANISH", LANG_SPANISH, null, LANGUAGE_NORMALIZER },

        { "englis", LANG_ENGLISH, null, LANGUAGE_NORMALIZER_EN_DEFAULT },
        { "spaniol", LANG_ENGLISH, null, LANGUAGE_NORMALIZER_EN_DEFAULT },
        { "FRENCH", LANG_ENGLISH, null, LANGUAGE_NORMALIZER_EN_DEFAULT },
        { null, LANG_ENGLISH, null, LANGUAGE_NORMALIZER_EN_DEFAULT },

        { null, null, ValidationException.class, LANGUAGE_NORMALIZER },
        { "englis", null, ValidationException.class, LANGUAGE_NORMALIZER },
        { "spaniol", null, ValidationException.class, LANGUAGE_NORMALIZER },
        { "FRENCH", null, ValidationException.class, LANGUAGE_NORMALIZER }
      });
  }

  public LanguageNormalizerTest(
      String input,
      String output,
      Class<?> exceptionClz,
      ConvertingValidator<String, String> converter) {
    super(input, output, exceptionClz, converter);
  }

}
