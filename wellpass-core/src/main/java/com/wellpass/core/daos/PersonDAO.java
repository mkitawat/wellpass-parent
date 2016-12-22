package com.wellpass.core.daos;

import com.wellpass.core.models.auth.Person;
import org.mongodb.morphia.Datastore;

//@Singleton
public class PersonDAO extends DAOImpl<Person> {
  //  @Inject
  public PersonDAO(Datastore ds) {
    super(ds, Person.class);
  }

  public void merge(Person person) {
    getDatastore().merge(person);
  }
}
