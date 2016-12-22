package com.wellpass.core.daos;

import com.wellpass.core.models.wellbox.Image;
import org.mongodb.morphia.Datastore;

//@Singleton
public class ImageDAO extends DAOImpl<Image> {
  //  @Inject
  public ImageDAO(Datastore ds) {
    super(ds, Image.class);
  }
}
