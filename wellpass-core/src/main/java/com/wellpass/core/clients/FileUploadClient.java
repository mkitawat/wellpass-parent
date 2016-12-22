package com.wellpass.core.clients;

import java.io.File;

public interface FileUploadClient {
    void putFile(String path, String fileName, File file);
    String putImage(String bucketName, String objectKey, String imageData);
    void archiveFile(String fromBucket, String fileName, String toBucket, String newFileName);
}
