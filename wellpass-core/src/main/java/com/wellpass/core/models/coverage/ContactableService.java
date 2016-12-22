package com.wellpass.core.models.coverage;

/**
 * Contactable service configured on the health plan configuration file
 */
public class ContactableService {
  /**
   * Displayable short text which can be used for grouping services
   */
  public String type;

  /**
   * Displayable short text which can be used as header or title
   */
  public String name;

  /**
   * Displayable text describing the service
   */
  public String description;

  public String phone;

  public String url;

  public String email;

  /**
   * For frameworks
   */
  public ContactableService() {

  }

  /**
   * For coding convenience
   */
  public ContactableService(String type, String name, String description, String phone, String url,
                            String email) {
    this.type = type;
    this.name = name;
    this.description = description;
    this.phone = phone;
    this.url = url;
    this.email = email;
  }
}
