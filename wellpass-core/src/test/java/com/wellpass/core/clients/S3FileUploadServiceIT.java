package com.wellpass.core.clients;

import com.wellpass.core.annotations.ExternalIntegrationTests;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(ExternalIntegrationTests.class)
public class S3FileUploadServiceIT {
  private S3FileUploadClient fileUploadService = new S3FileUploadClient();
  private HttpClient httpService = new HttpClient();

  @Test
  @Ignore
  public void signUrlTest() {
    String bucketName = "wellpass-images-test";
    String imageData = "imageDataTest";
    String objectKey = "objectKeyTest";
    String s3Url = fileUploadService.putImage(bucketName, objectKey, imageData);
    String signedUrl = fileUploadService.signUrl(bucketName, objectKey, s3Url);

    try {
      CloseableHttpResponse signedResponse = httpService.get(signedUrl);
      CloseableHttpResponse nonSignedResponse = httpService.get(s3Url);
      int signedStatusCode = signedResponse.getStatusLine().getStatusCode();
      int nonSignedStatusCode = nonSignedResponse.getStatusLine().getStatusCode();
      Assert.assertTrue(signedStatusCode == 200);
      Assert.assertTrue(nonSignedStatusCode == 403);
    } catch (IOException exception) {
      System.out.println(exception.getMessage());
    }
  }

}
