package com.wellpass.core.utils.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;

public class LocalDateTypeAdapter extends TypeAdapter<LocalDate> {
  public static TypeAdapter<LocalDate> INSTANCE = new LocalDateTypeAdapter();
  public static TypeAdapter<LocalDate> NULLSAFE_INSTANCE = INSTANCE.nullSafe();

  @Override
  public void write(JsonWriter out, LocalDate value) throws IOException {
    out.value(value.toString());
  }

  @Override
  public LocalDate read(JsonReader in) throws IOException {
    return LocalDate.parse(in.nextString());
  }

}
