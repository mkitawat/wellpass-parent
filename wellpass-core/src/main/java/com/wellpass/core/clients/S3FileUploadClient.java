package com.wellpass.core.clients;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.wellpass.core.exceptions.FileUploadException;
import com.wellpass.core.exceptions.UrlSigningException;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class S3FileUploadClient implements FileUploadClient {
  public static final String S3_ARCHIVE_SUFFIX = "-archive";
  private static final AmazonS3Client client = new AmazonS3Client();
  private static final String S3_HTTPS_PREFIX = "https://";
  private static final String S3_DOMAIN_SUFFIX = ".s3.amazonaws.com/";
  private static final String S3_ACCESS_KEY_SUFFIX = "?AWSAccessKeyId=";

  @Override
  public void putFile(String path, String fileName, File file) {
    try {
      client.putObject(new PutObjectRequest(path, fileName, file));
    } catch (AmazonClientException e) {
      throw new FileUploadException(e.getMessage());
    }
  }

  @Override
  public String putImage(String bucketName, String objectKey, String imageRaw) {
    try {
      byte[] binaryImage = Base64.decodeBase64((imageRaw.substring(imageRaw.indexOf(",") + 1)).getBytes());
      InputStream imageStream = new ByteArrayInputStream(binaryImage);
      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentLength(binaryImage.length);
      metadata.setContentType("image/png");
      metadata.setCacheControl("public, max-age=31536000");
      metadata.setSSEAlgorithm(metadata.AES_256_SERVER_SIDE_ENCRYPTION);
      client.putObject(new PutObjectRequest(bucketName, objectKey, imageStream, metadata));
      client.setObjectAcl(bucketName, objectKey, CannedAccessControlList.AuthenticatedRead);
      String url = client.getResourceUrl(bucketName, objectKey);
      return url;
    } catch (AmazonClientException e) {
      throw new FileUploadException(e.getMessage());
    }
  }

  @Override
  public void archiveFile(String fromBucket, String fileName, String toBucket, String newFileName) {
    this.copyFile(fromBucket, fileName, toBucket, newFileName);
    this.removeFile(fromBucket, fileName);
  }

  public void copyFile(String fromBucket, String fileName, String toBucket, String newFileName) {
    try {
      client.copyObject(new CopyObjectRequest(fromBucket, fileName, toBucket, newFileName));
    } catch (AmazonClientException ex) {
      throw new FileUploadException(ex.getMessage(), ex);
    }
  }

  public void removeFile(String bucket, String fileName) {
      try {
          client.deleteObject(new DeleteObjectRequest(bucket, fileName));
      } catch (AmazonClientException ex) {
          throw new FileUploadException(ex.getMessage(), ex);
      }
  }

  public String signUrl(String bucketName, String objectKey, String s3Url) {
    String signedUrl = null;

    try {
      Date expiration = new Date();
      long milliSeconds = expiration.getTime();
      milliSeconds += 1000 * 60 * 60; // Add 1 hour.
      expiration.setTime(milliSeconds);

      GeneratePresignedUrlRequest generatePresignedUrlRequest =
          new GeneratePresignedUrlRequest(bucketName, objectKey);
      generatePresignedUrlRequest.setMethod(HttpMethod.GET);
      generatePresignedUrlRequest.setExpiration(expiration);

      URL url = client.generatePresignedUrl(generatePresignedUrlRequest);
      signedUrl = url.toString();
    } catch (AmazonServiceException exception) {
      throw new UrlSigningException(exception.getMessage());
    } catch (AmazonClientException ace) {
      throw new UrlSigningException(ace.getMessage());
    }

    if (signedUrl != null) {
      return signedUrl;
    }

    return s3Url;
  }

  public static String s3BucketFromUri(String uri) {
      return findFirstPattern(Pattern.compile(Pattern.quote(S3_HTTPS_PREFIX) + "(.*?)" + Pattern.quote(S3_DOMAIN_SUFFIX)), uri);
  }

  public static String s3PathFromUri(String uri) {
      return findFirstPattern(Pattern.compile(Pattern.quote(S3_DOMAIN_SUFFIX) + "(.*?)" + Pattern.quote(S3_ACCESS_KEY_SUFFIX)), uri);
  }

  public static String s3KeyFromUri(String uri) {
    return findFirstPattern(Pattern.compile(Pattern.quote(S3_DOMAIN_SUFFIX) + "(.*?$)"), uri);
  }

  private static String findFirstPattern(Pattern pattern, String text) {
      Matcher matcher = pattern.matcher(text);
      while (matcher.find()) {
          return matcher.group(1);
      }
      return null;
  }
}
