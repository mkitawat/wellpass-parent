package com.wellpass.core.validation.validators;

@FunctionalInterface
public interface ConvertingValidator<S, T> {

  T convert(S s) throws ValidationException;

  default T convertSafely(S s, T defaultT) {
    try {
      return this.convert(s);
    } catch (Exception ex) {
      return defaultT;
    }
  }

  default <K> ConvertingValidator<S, K> andThen(ConvertingValidator<T, K> other) {
    return s -> other.convert(this.convert(s));
  }

  default ConvertingValidator<S, T> andThen(Validator<T> other) {
    return s -> { T t = this.convert(s); other.validate(t); return t;};
  }

  default ConvertingValidator<S, T> nullSafe(T defaultT) {
    return s -> { if (s == null) return defaultT; return this.convert(s); };
  }

  default ConvertingValidator<S, T> nullSafe() {
    return nullSafe(null);
  }

  default ConvertingValidator<S, T> errorSafe(T defaultT) {
    return s -> { return this.convertSafely(s, defaultT); };
  }

  default ConvertingValidator<S, T> errorSafe() {
    return errorSafe(null);
  }

  default ConvertingValidator<S, T> nullAndErrorSafe(T defaultT) {
    return s -> {
      if (s == null) return defaultT;
      try {
        return this.convert(s);
      } catch (Exception ex) {
        return defaultT;
      }
    };
  }

  default ConvertingValidator<S, T> nullAndErrorSafe() {
    return nullAndErrorSafe(null);
  }

}
