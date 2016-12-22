package com.wellpass.core.daos;

import com.wellpass.core.models.coverage.Family;
import org.mongodb.morphia.Datastore;

//@Singleton
public class FamilyDAO extends DAOImpl<Family> {
  //  @Inject
  public FamilyDAO(Datastore ds) {
    super(ds, Family.class);
  }
}
