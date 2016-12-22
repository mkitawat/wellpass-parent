package com.wellpass.core.daos;

import com.wellpass.core.models.coverage.Eligibility;
import org.mongodb.morphia.Datastore;

//@Singleton
public class EligibilityDAO extends DAOImpl<Eligibility> {
  //  @Inject
  public EligibilityDAO(Datastore ds) {
    super(ds, Eligibility.class);
  }

  public void merge(Eligibility person) {
    getDatastore().merge(person);
  }
}
