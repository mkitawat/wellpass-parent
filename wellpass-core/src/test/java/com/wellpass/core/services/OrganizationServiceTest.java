package com.wellpass.core.services;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.wellpass.core.annotations.UnitTests;
import com.wellpass.core.daos.OrganizationDAO;
import com.wellpass.core.models.coverage.Organization;
import junit.framework.Assert;
import org.bson.types.ObjectId;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(UnitTests.class)
public class OrganizationServiceTest {

  private static OrganizationService organizationService;
  private static OrganizationDAO organizationDAO;

  @BeforeClass
  public static void setUp() throws IOException {
    organizationDAO = mock(OrganizationDAO.class);
    organizationService = new OrganizationService(organizationDAO);
  }

  private static Organization makeOrganization(ObjectId id, String organizationId, String type,
                                               String fullName, String shortName, String state,
                                               String pokitdokPartnerId, String voxivaTenantId) {
    Organization organization = new Organization();
    organization.id = id;
    organization.organizationId = organizationId;
    organization.type = type;
    organization.fullName = fullName;
    organization.shortName = shortName;
    organization.state = state;
    organization.pokitdokPartnerId = pokitdokPartnerId;
    organization.voxivaTenantId = voxivaTenantId;
    return organization;
  }

  @Test
  public void testCreateOrganization() throws Exception {
    Organization
      organization =
      organizationService
        .createOrganization("id123", "type", "Organization full name", "Organization short", "NY",
          "1234", "2345", null);
    Assert.assertEquals(organization.organizationId, "id123");
    Assert.assertEquals(organization.type, "type");
    Assert.assertEquals(organization.fullName, "Organization full name");
    Assert.assertEquals(organization.shortName, "Organization short");
    Assert.assertEquals(organization.state, "NY");
    Assert.assertEquals(organization.pokitdokPartnerId, "1234");
    Assert.assertEquals(organization.voxivaTenantId, "2345");
    verify(organizationDAO).save(organization);
  }

  @Test
  public void testEditOrganization() throws Exception {
    Organization
      organization =
      makeOrganization(new ObjectId(), "id123", "type", "Organization full", "Organization short",
        "NY", "1111", "2222");
    Organization
      updatedOrganization =
      organizationService
        .editOrganization(organization, "type2", "Organization full 2", "Organization short 2",
          "NJ", "3333", "4444", null);
    Assert.assertEquals(organization.id, updatedOrganization.id);
    Assert.assertEquals(organization.organizationId, updatedOrganization.organizationId);
    Assert.assertEquals(updatedOrganization.type, "type2");
    Assert.assertEquals(updatedOrganization.fullName, "Organization full 2");
    Assert.assertEquals(updatedOrganization.shortName, "Organization short 2");
    Assert.assertEquals(updatedOrganization.state, "NJ");
    Assert.assertEquals(updatedOrganization.pokitdokPartnerId, "3333");
    Assert.assertEquals(updatedOrganization.voxivaTenantId, "4444");
    verify(organizationDAO).save(organization);
  }
}
