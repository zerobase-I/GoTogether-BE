package com.example.gotogetherbe.global.util.aws.service;



import static com.example.gotogetherbe.global.exception.type.ErrorCode.*;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;


import com.example.gotogetherbe.global.exception.GlobalException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsS3Service {

  private final AmazonS3 amazonS3;

  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;



  public String uploadProfileImage(MultipartFile multipartFile) {
    log.info("[uploadProfileImage] start");

    String fileName = generateFileName(multipartFile);

    try (InputStream inputStream = multipartFile.getInputStream()) {
      amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream,
          getObjectMetadata(multipartFile))
          .withCannedAcl(CannedAccessControlList.PublicRead));
    } catch (IOException e) {
      log.error("IOException is occurred", e);
      throw new GlobalException(INTERNAL_SERVER_ERROR);
    }

    return getUrl(fileName);
  }


  private String getUrl(String fileName) {
    return amazonS3.getUrl(bucketName, fileName).toString();
  }


  private ObjectMetadata getObjectMetadata(MultipartFile multipartFile) {
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentType(multipartFile.getContentType());
    objectMetadata.setContentLength(multipartFile.getSize());
    return objectMetadata;
  }

  private String generateFileName(MultipartFile multipartFile) {
    return UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();
  }
}
