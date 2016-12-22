package com.wellpass.core.validation.validators;

@FunctionalInterface
public interface Validator<S> {

  void validate(S s) throws ValidationException;

  default Validator<S> andThen(Validator<S> other) {
    return s -> { this.validate(s); other.validate(s); };
  }

  default <T> ConvertingValidator<S, T> andThen(ConvertingValidator<S, T> other) {
    return s -> { this.validate(s); return other.convert(s); };
  }

  default Validator<S> nullSafe() {
    return s -> {
      if (s == null) return;
      this.validate(s);
    };
  }

  default ConvertingValidator<S, S> toConvertingValidator() {
    return s -> { this.validate(s); return s; };
  }
}
