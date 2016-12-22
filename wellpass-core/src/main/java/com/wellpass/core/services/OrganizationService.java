package com.wellpass.core.services;

import com.mongodb.DuplicateKeyException;
import com.wellpass.core.daos.OrganizationDAO;
import com.wellpass.core.exceptions.WPNotFoundException;
import com.wellpass.core.models.coverage.Organization;
import org.bson.types.ObjectId;

/**
 * Service to handle interaction with Organizations
 */
public class OrganizationService {
  private final OrganizationDAO organizationDAO;


  public OrganizationService(OrganizationDAO organizationDAO) {
    this.organizationDAO = organizationDAO;
  }

  public Organization organizationFromId(String id) {
    try {
      return organizationDAO.findOneById(id);
    } catch (WPNotFoundException ex) {
      return null;
    }
  }

  private Organization organizationFromSensehealthId(String shId) {
    try {
      return organizationDAO.findOne("shId", shId);
    } catch (WPNotFoundException ex) {
      return null;
    }
  }

  public Organization createOrganization(String organizationId, String type, String fullName,
                                         String shortName, String state, String pokitdokPartnerId,
                                         String voxivaTenantId, String tracfonePlanId)
    throws DuplicateKeyException {
    Organization o = new Organization();
    o.id = new ObjectId();
    o.organizationId = organizationId;
    o.type = type;
    o.fullName = fullName;
    o.shortName = shortName;
    o.state = state;
    o.pokitdokPartnerId = pokitdokPartnerId;
    o.voxivaTenantId = voxivaTenantId;
    o.tracfonePlanId = tracfonePlanId;
    organizationDAO.save(o);
    return o;
  }

  public Organization editOrganization(Organization o, String type, String fullName,
                                       String shortName, String state, String pokitdokPartnerId,
                                       String voxivaTenantId, String tracfonePlanId) {
    if (type != null) {
      o.type = type;
    }
    if (fullName != null) {
      o.fullName = fullName;
    }
    if (shortName != null) {
      o.shortName = shortName;
    }
    if (state != null) {
      o.state = state;
    }
    if (pokitdokPartnerId != null) {
      o.pokitdokPartnerId = pokitdokPartnerId;
    }
    if (voxivaTenantId != null) {
      o.voxivaTenantId = voxivaTenantId;
    }
    if (tracfonePlanId != null) {
      o.tracfonePlanId = tracfonePlanId;
    }
    organizationDAO.save(o);
    return o;
  }

  public Organization createUpdateShOrg(String shId, String organizationId, String tracfonePlanId,
                                        boolean lifelineonly) {
    Organization o = organizationFromSensehealthId(shId);
    if (o == null) {
      o = new Organization();
      o.id = new ObjectId();
    }

    o.organizationId = organizationId;
    o.shId = shId;
    o.tracfonePlanId = tracfonePlanId;
    o.lifelineOnly = lifelineonly;
    organizationDAO.save(o);
    return o;
  }

  public void deleteShOrg(String shId) {
    Organization o = organizationFromSensehealthId(shId);
    if (o == null) {
      throw new WPNotFoundException("no org found with id: " + shId);
    }
    organizationDAO.deleteById(o.id.toString());
  }
}
