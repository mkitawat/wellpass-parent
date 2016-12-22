package com.wellpass.core.daos;

import com.mongodb.WriteResult;
import com.wellpass.core.exceptions.WPNotFoundException;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.mapping.MappedClass;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DAOImpl<T> implements DAO<T> {
  private static final Logger LOG = LoggerFactory.getLogger(DAOImpl.class);
  private static final String ID_KEY = "id";
  private org.mongodb.morphia.dao.DAO<T, ObjectId> morphiaDAO;
  private Morphia morphia;

  public DAOImpl(Datastore ds, Class<T> resourceClass) {
    morphiaDAO = new BasicDAO<>(resourceClass, ds);
    morphia = new Morphia();

    LOG.info("ensuring mongodb indexes for {}", resourceClass);
    getDatastore().ensureIndexes(resourceClass);
  }

  public Query<T> createQuery() {
    return morphiaDAO.createQuery();
  }

  public T findOneById(String id) throws WPNotFoundException {
    final T result = findOne(ID_KEY, new ObjectId(id));
    if (result == null) {
      throw new WPNotFoundException("object not found");
    }
    return result;
  }

  @Override
  public T findOne(String key, Object val) throws WPNotFoundException {
    T result = morphiaDAO.findOne(key, val);
    if (result == null) {
      throw new WPNotFoundException("object not found");
    }
    return result;
  }

  @Override
  public List<T> find() {
    return morphiaDAO.createQuery().asList();
  }

  @Override
  public T save(T resource) {
    morphiaDAO.save(resource);
    return resource;
  }

  @Override
  public boolean deleteById(String id) {
    final WriteResult result = morphiaDAO.deleteById(new ObjectId(id));
    if (result.getN() < 1) {
      return false;
    } else {
      return result.wasAcknowledged();
    }
  }

  @Override
  public Datastore getDatastore() {
    return morphiaDAO.getDatastore();
  }

  @Override
  public <T> void update(T model, String field1, String... restFields) {
    @SuppressWarnings("unchecked")
    Class<T> modelClazz = (Class<T>)model.getClass();
    MappedClass mc = this.morphia.getMapper().getMappedClass(modelClazz);
    UpdateOperations<T> updateOps = this.getDatastore().createUpdateOperations(modelClazz);

    updateModel(model, field1, updateOps, mc);
    for (String otherField : restFields) {
      updateModel(model, otherField, updateOps, mc);
    }

    this.getDatastore().update(model, updateOps);
  }

  private <T> UpdateOperations<T> updateModel(
    T model, String field, UpdateOperations<T> updateOps, MappedClass mc) {
    MappedField mf = mc.getMappedFieldByJavaField(field);
    if (mf == null) {
      throw new IllegalArgumentException(String.format("No such field %s",  field));
    }
    Object value = mf.getFieldValue(model);
    if (value != null) {
      updateOps.set(field, value);
    } else {
      updateOps.unset(field);
    }
    return updateOps;
  }
}
