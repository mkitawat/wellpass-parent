package com.wellpass.core.daos;

import com.wellpass.core.exceptions.WPNotFoundException;
import org.mongodb.morphia.Datastore;

import java.util.List;

public interface DAO<T> {
  T findOneById(String id) throws WPNotFoundException;

  T findOne(String key, Object val) throws WPNotFoundException;

  List<T> find();

  T save(T resource);

  boolean deleteById(String id);

  Datastore getDatastore();

  <T> void update(T model, String field1, String... restFields);
}
