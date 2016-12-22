package com.wellpass.core.daos;

import com.wellpass.core.models.auth.Device;
import com.wellpass.core.models.auth.User;
import org.mongodb.morphia.Datastore;

import java.util.List;

//@Singleton
public class DeviceDAO extends DAOImpl<Device> {
  //  @Inject
  public DeviceDAO(Datastore ds) {
    super(ds, Device.class);
  }

  public List<Device> findUserDevices(User user) {
    return createQuery().field("userId").equalIgnoreCase(user.id).asList();
  }
}
