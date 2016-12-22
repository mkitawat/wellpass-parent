package com.wellpass.core.enums;

import com.google.common.collect.ImmutableMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface CodedEnum<T extends Enum<T> & CodedEnum<T>> {
  /**
   * Returns the enum constant of the specified coded enum type with the specified code.
   * @param codedEnumType the Class object of the coded enum type from which to return a constant
   * @param code the code of the constant to return
   * @return the enum constant of the specified coded enum type with the specified name
   */
  public static <T extends Enum<T> & CodedEnum<T>> T fromCode(Class<T> codedEnumType, String code) {
    return codeMapOf(codedEnumType).get(code);
  }

  /**
   * Creates an immutable map containing the specified elements keyed by their code.
   * @param first a mandatory element required to create the map
   * @param rest the remaining elements that the map will contain
   * @return an immutable map containing the specified elements
   */
  @SuppressWarnings("unchecked")
  public static <T extends Enum<T> & CodedEnum<T>> Map<String, T> codeMapOf(T first, T... rest) {
    ImmutableMap.Builder<String, T> builder = new ImmutableMap.Builder<>();
    builder.put(first.code(), first);
    for (T other : rest) {
      builder.put(other.code(), other);
    }
    return builder.build();
  }

  /**
   * Returns an immutable map containing the association of codes to their respective
   * coded enum constants specified by @param clz
   * @param codedEnumType the Class object of the coded enum type from which to return a constant
   * @return an unmodifiable Map containing the association of codes to coded enum constants
   */
  @SuppressWarnings("unchecked")
  public static <T extends Enum<T> & CodedEnum<T>> Map<String, T> codeMapOf(
      Class<T> codedEnumType) {
    Map<String, ? extends CodedEnum<?>> codeMap =
      Internal.CODE_CACHE
        .computeIfAbsent(codedEnumType, clzKey -> Internal.instantiateMap(codedEnumType));

    return (Map<String, T>) codeMap;
  }

  /**
   * Returns the code of this enum constant.
   * Typically will be overridden to provide the code as defined in the coded enum declaration.
   * If not overridden, by default it will return enum element name i.e. name()
   * @return code value
   */
  public default String code() {
    return name();
  }

  /**
   * Must be implemented by Enum.name()
   * @return Enum element name
   */
  public String name();

  /**
   * An Internal class to hold private cache of the CodeEnum interface.
   */
  public static class Internal {
    private static final Map<Class<?>, Map<String, ? extends CodedEnum<?>>> CODE_CACHE =
      new ConcurrentHashMap<>();

    private static <T extends Enum<T> & CodedEnum<T>> Map<String, T> instantiateMap(
      Class<T> clz) {

      Map<String, T> map = new HashMap<>();
      for (T enumElement : clz.getEnumConstants()) {
        map.put(enumElement.code(), enumElement);
      }
      return Collections.unmodifiableMap(map);
    }
  }

}
