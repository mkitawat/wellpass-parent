package com.wellpass.core.validation.validators;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Parameterized.class)
public abstract class AbstractConvertingValidatorTest<S, T> {
  private Logger logger = LoggerFactory.getLogger(this.getClass());
  S input;
  T output;
  Class<?> exceptionClz;
  ConvertingValidator<S, T> converter;

  public AbstractConvertingValidatorTest(
      S input,
      T output,
      Class<?> exceptionClz,
      ConvertingValidator<S, T> converter) {
    this.input = input;
    this.output = output;
    this.exceptionClz = exceptionClz;
    this.converter = converter;
  }

  @Test
  public void test() throws Exception {
    if (exceptionClz == null) {
      T actualOutput = converter.convert(input);
      logger.info(
          "input={}, expectedoutput={}, actualoutput={}", input, output, actualOutput);
      Assert.assertEquals("Output should match", output, actualOutput);
    } else {
      Exception ex = null;
      try {
        converter.convert(input);
      } catch (Exception e) {
        ex = e;
      }
      Assert.assertNotNull("Exception expected", ex);
      Assert.assertEquals("Exception class should match", exceptionClz, ex.getClass());
    }
  }
}
