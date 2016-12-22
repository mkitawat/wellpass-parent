package com.wellpass.core.daos;

import com.wellpass.core.models.lifeline.LifelineApplication;
import org.mongodb.morphia.Datastore;

//@Singleton
public class LifelineApplicationDAO extends DAOImpl<LifelineApplication> {
  //  @Inject
  public LifelineApplicationDAO(Datastore ds) {
    super(ds, LifelineApplication.class);
  }
}
