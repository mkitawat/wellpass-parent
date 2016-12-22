package com.wellpass.core.utils.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.json.JsonSanitizer;

import org.bson.types.ObjectId;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Date;

/**
 * Convert an object to JSON using GSON with our custom modifications.
 *
 * Since we use this class a lot, we cut ourselves some slack and use a cute name.
 */
public class Jsonifier {
  private static Gson gson = new GsonBuilder()
    .registerTypeAdapter(ObjectId.class, new ObjectIdSerializer())
    .registerTypeAdapter(Date.class, new DateSerializer())
    .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
    .addSerializationExclusionStrategy(new SerializationAnnotationExclusionStrategy())
    .addDeserializationExclusionStrategy(new DeserializationAnnotationExclusionStrategy())
    .create();

  public static String toJson(Object o) {
    return gson.toJson(o);
  }

  public static <T> T fromJson(String json, Class<T> object) {
    return gson.fromJson(JsonSanitizer.sanitize(json), object);
  }

  private static class ObjectIdSerializer
    implements JsonSerializer<ObjectId>, JsonDeserializer<ObjectId> {
    public JsonElement serialize(ObjectId src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.toString());
    }

    @Override
    public ObjectId deserialize(JsonElement jsonElement, Type type,
                                JsonDeserializationContext context) throws JsonParseException {
      return new ObjectId(jsonElement.getAsString());
    }
  }

  private static class DateSerializer implements JsonSerializer<Date>, JsonDeserializer<Date> {
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.getTime() / 1000);
    }

    public Date deserialize(JsonElement jsonElement, Type type,
                            JsonDeserializationContext context) {
      try {
        long seconds = jsonElement.getAsJsonPrimitive().getAsLong();
        return new Date(seconds * 1000);
      } catch (NumberFormatException e) {
        // Json value wasn't a number
        throw new JsonParseException("[" + jsonElement.getAsJsonPrimitive() + "] is not a number.");
      }
    }
  }

  private static class LocalDateSerializer implements JsonSerializer<LocalDate> {
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.toString());
    }
  }

  private static class DeserializationAnnotationExclusionStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes f) {
      return f.getAnnotation(Exclude.class) != null && f.getAnnotation(Exclude.class).deserialize();
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
      return false;
    }
  }

  private static class SerializationAnnotationExclusionStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes f) {
      return f.getAnnotation(Exclude.class) != null && f.getAnnotation(Exclude.class).serialize();
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
      return false;
    }
  }
}
