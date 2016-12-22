package com.wellpass.core.mongo.converters;

import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.time.LocalDate;

/**
 * Created by pmotipara on 3/30/16.
 * Converts java.time.LocalDate back/forth from its String Representation.
 */
public class LocalDateConverter extends TypeConverter implements SimpleValueConverter {

  public LocalDateConverter() {
    super(LocalDate.class);
  }

  @Override
  public LocalDate decode(Class<?> targetClass, Object fromDBObject,
                          MappedField optionalExtraInfo) {
    if (fromDBObject == null) {
      return null;
    }

    if (!(fromDBObject instanceof String)) {
      throw new RuntimeException(
        "Unexpected type received: " + fromDBObject.getClass().getName() + "; expected: "
          + LocalDate.class.getName());
    }

    return LocalDate.parse(((String) fromDBObject));
  }

  @Override
  public Object encode(Object value, MappedField optionalExtraInfo) {
    if (value == null) {
      return null;
    }

    if (!(value instanceof LocalDate)) {
      throw new RuntimeException(
        "Unexpected type received: " + value.getClass().getName() + "; expected: " + LocalDate.class
          .getName());
    }

    return value.toString();
  }

}
