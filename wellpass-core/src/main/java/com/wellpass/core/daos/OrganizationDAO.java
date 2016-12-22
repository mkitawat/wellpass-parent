package com.wellpass.core.daos;

import com.wellpass.core.models.coverage.Organization;
import org.mongodb.morphia.Datastore;

//@Singleton
public class OrganizationDAO extends DAOImpl<Organization> {
  //  @Inject
  public OrganizationDAO(Datastore ds) {
    super(ds, Organization.class);
  }
}
