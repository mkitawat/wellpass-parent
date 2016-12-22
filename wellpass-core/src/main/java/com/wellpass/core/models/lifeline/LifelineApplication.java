package com.wellpass.core.models.lifeline;

import com.wellpass.core.models.auth.Person;
import com.wellpass.core.utils.SecurityUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import java.util.Date;

@Entity(value = "lifelineapplication", noClassnameStored = true)
public class LifelineApplication {
  @Id
  public ObjectId id;

  public String token = SecurityUtils.nextToken();

  @Reference(idOnly = true)
  public Person person;
  public Date tokenExpirationDate;
}
