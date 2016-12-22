package com.wellpass.core.services;

import com.wellpass.core.daos.PersonDAO;
import com.wellpass.core.daos.UserChangeSetDAO;
import com.wellpass.core.models.auth.Person;
import com.wellpass.core.models.auth.UserChangeSet;
import com.wellpass.core.utils.gson.Jsonifier;

public class SyncUserService {

  private final UserChangeSetDAO userChangeSetDAO;
  private final PersonDAO personDAO;

  public SyncUserService(UserChangeSetDAO userChangeSetDAO, PersonDAO personDAO) {
    this.userChangeSetDAO = userChangeSetDAO;
    this.personDAO = personDAO;
  }

  public void sync(UserChangeSet changeset) {
    userChangeSetDAO.save(changeset);
    personDAO.merge(this.buildPerson(changeset));
  }

  private Person buildPerson(UserChangeSet changeset) {
    changeset.id = changeset.userId;
    String changesetJson = Jsonifier.toJson(changeset);
    Person person = Jsonifier.fromJson(changesetJson, Person.class);
    return person;
  }
}
