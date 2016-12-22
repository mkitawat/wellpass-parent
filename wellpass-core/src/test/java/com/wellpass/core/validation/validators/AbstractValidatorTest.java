package com.wellpass.core.validation.validators;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public abstract class AbstractValidatorTest<S> {
  S input;
  Class<?> exceptionClz;
  Validator<S> validator;

  public AbstractValidatorTest(
      S input,
      Class<?> exceptionClz,
      Validator<S> validator) {
    this.input = input;
    this.exceptionClz = exceptionClz;
    this.validator = validator;
  }

  @Test
  public void test() throws Exception {
    if (exceptionClz == null) {
      validator.validate(input);
    } else {
      Exception ex = null;
      try {
        validator.validate(input);
      } catch (Exception e) {
        ex = e;
      }
      Assert.assertNotNull("Exception expected", ex);
      Assert.assertEquals("Exception class should match", exceptionClz, ex.getClass());
    }
  }

}
