package com.wellpass.core.services;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.wellpass.core.clients.HttpClient;
import org.junit.After;
import org.junit.BeforeClass;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

// todo justin - DO NOT LEAVE THIS HERE
// do this instead: http://stackoverflow.com/questions/36047637/how-can-i-include-test-classes-into-maven-jar-and-execute-them
public class BaseIT {
  private static final String DEV_TEST_MONGO_URI = "mongodb://localhost/wellpass_test";
  protected static HttpClient httpService;
  private static MongoClientURI mongoClientURI;
  private static MongoClient mongoClient;
  private static Morphia morphia;

  public static HttpClient getHttpService() {
    return httpService;
  }

  public static MongoClientURI getMongoClientURI() {
    return mongoClientURI;
  }

  public static MongoClient getMongoClient() {
    return mongoClient;
  }

  public static Morphia getMorphia() {
    return morphia;
  }

  public static Datastore getDatastore() {
    return datastore;
  }

  private static Datastore datastore;

  @BeforeClass
  public static void setUp() {
    mongoClientURI = new MongoClientURI(DEV_TEST_MONGO_URI,
      MongoClientOptions.builder().sslInvalidHostNameAllowed(true));
    mongoClient = new MongoClient(mongoClientURI);
    morphia = new Morphia();
    datastore = morphia.createDatastore(mongoClient, mongoClientURI.getDatabase());

    getDatastore().getDB().dropDatabase();

    httpService = new HttpClient();
  }

  @After
  public void tearDown() {
    getDatastore().getDB().dropDatabase();
  }
}
