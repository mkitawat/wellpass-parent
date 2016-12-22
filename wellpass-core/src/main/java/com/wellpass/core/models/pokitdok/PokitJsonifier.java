package com.wellpass.core.models.pokitdok;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.wellpass.core.utils.gson.LocalDateTypeAdapter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PokitDok GSON holder
 */
public class PokitJsonifier {
  private static PokitJsonifier INSTANCE = null;
  public final Gson gson;
  private final Map<Class<? extends PokitBaseResponse>, ParameterizedType> typeCache;

  private PokitJsonifier() {
    gson = new GsonBuilder()
      .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
      .registerTypeAdapter(LocalDate.class, LocalDateTypeAdapter.NULLSAFE_INSTANCE)
      .create();
    typeCache = new ConcurrentHashMap<>();
  }

  public static PokitJsonifier getInstance() {

    if (INSTANCE != null) {
      return INSTANCE;
    }

    synchronized (PokitJsonifier.class) {
      if (INSTANCE != null) {
        return INSTANCE;
      }
      return INSTANCE = new PokitJsonifier();
    }
  }

  public String toJson(Object obj) {
    return gson.toJson(obj);
  }

  public <T> T fromJson(String json, Class<T> clz) {
    return gson.fromJson(json, clz);
  }

  public <V extends PokitBaseResponse> V fromRootJson(String json, Class<V> clz) {
    // restrict use of artificial Type with GSON library only. Tested with only GSON
    PokitRootResponse<V> root = gson.fromJson(json, getArtificialRootType(clz));
    return root.data;
  }

  public String toRootJson(PokitBaseResponse response) {
    PokitRootResponse<PokitBaseResponse> root = new PokitRootResponse<PokitBaseResponse>();
    root.data = response;
    return toJson(root);
  }

  private ParameterizedType getArtificialRootType(Class<? extends PokitBaseResponse> clz) {
    return typeCache.computeIfAbsent(clz, ParameterizedTypeImpl::new);
  }


  /**
   * All the PokitDok endpoints return a generic response at the root level
   * which consists of a meta key and a data key.
   * <pre>
   * {
   *  "meta": { ... },
   *  "data": { actual data }
   * }
   * </pre>
   *
   * Endpoint specific response data comes under data key.
   * This class tries to extract the actual data under "data" key by using
   * java.lang.reflect.ParameterizedType private subclass.
   */
  private static class PokitRootResponse<T extends PokitBaseResponse> {
    public T data;
  }

  /**
   * ParameterizedType subclass only for PokitBaseResponse<T extends PokitBaseResponseData>
   * GSON itself canonicalize {@link ParameterizedType} object internally.
   * Restrict its use with GSON library only. Tested only with GSON.
   */
  private static class ParameterizedTypeImpl implements ParameterizedType {

    private Class<?> clz;

    private ParameterizedTypeImpl(Class<? extends PokitBaseResponse> clz) {
      this.clz = clz;
    }

    @Override
    public Type getRawType() {
      return PokitRootResponse.class;
    }

    @Override
    public Type getOwnerType() {
      return null;
    }

    @Override
    public Type[] getActualTypeArguments() {
      return new Type[]{clz};
    }

    @Override
    public boolean equals(Object other) {
      if (this == other) {
        return true;
      }
      if (!(other instanceof ParameterizedTypeImpl)) {
        return false;
      }
      ParameterizedTypeImpl pOther = (ParameterizedTypeImpl) other;
      return Objects.equals(this.clz, pOther.clz);
    }

    @Override
    public int hashCode() {
      return this.clz.hashCode();
    }
  }
}
