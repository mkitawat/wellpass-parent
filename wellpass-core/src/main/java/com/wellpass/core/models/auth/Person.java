package com.wellpass.core.models.auth;

import com.wellpass.core.mongo.converters.LocalDateConverter;
import com.wellpass.core.utils.gson.Exclude;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Converters;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.NotSaved;
import org.mongodb.morphia.utils.IndexType;

import java.time.LocalDate;

/**
 * A Wellpass person.
 */
@Entity(value = "people", noClassnameStored = true)
@Converters(LocalDateConverter.class)
@Indexes({
  @Index(fields = {@Field(value = "email", type = IndexType.ASC)}),
  @Index(fields = {@Field(value = "mobile", type = IndexType.ASC)}),
  @Index(fields = {
    @Field(value = "firstName", type = IndexType.TEXT),
    @Field(value = "lastName", type = IndexType.TEXT),
    @Field(value = "dateOfBirth", type = IndexType.ASC)},
    options = @IndexOptions(language = "none", name = "person_text_index")
  )
})
public class Person {
  @Id
  public ObjectId id;

  public String firstName;

  public String lastName;

  public String middleInitial;

  public String email;

  public LocalDate dateOfBirth;

  public String last4Ssn;

  public String mobile;

  public Boolean tracFone;

  public Address address;

  public String gender;

  public String language;

  public ObjectId familyId;

  @Embedded
  public CustomAttributes customAttributes;

  public String employerName;

  public boolean
    existsInSenseHealth;
    // Flag to determine if we have already created this person in Sensehealth system

  public transient boolean isNew;

  @Exclude
  @NotSaved
  public double searchScore;

  /**
   * Creates new User object with new id
   */
  public static Person newPerson() {
    Person person = new Person();
    person.id = new ObjectId();
    person.isNew = true;
    person.existsInSenseHealth = false;
    person.tracFone = false;
    return person;
  }

  public String name() {
    return firstName + " " + lastName;
  }

  public boolean isOlderThan(Person person) {
    return this.dateOfBirth.isBefore(person.dateOfBirth);
  }
}
