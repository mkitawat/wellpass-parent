package com.wellpass.core.utils;

import java.util.Collection;
import java.util.List;

public interface CollectionUtils {

  public static boolean isEmpty(Collection<?> coll) {
    return coll == null || coll.isEmpty();
  }

  public static boolean isNotEmpty(Collection<?> coll) {
    return coll != null && !coll.isEmpty();
  }

  public static <T> T first(Collection<T> coll) {
    return isNotEmpty(coll) ? coll.iterator().next() : null;
  }

  public static <T> T first(List<T> list) {
    return isNotEmpty(list) ? list.get(0) : null;
  }
}
