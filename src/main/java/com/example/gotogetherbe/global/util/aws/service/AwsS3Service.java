package com.example.gotogetherbe.global.util.aws.service;



import static com.example.gotogetherbe.global.exception.type.ErrorCode.*;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;


import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.global.util.aws.dto.S3ImageDto;
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

  /**
   * 게시물 이미지를 AWS S3에 업로드하고 그에 대한 정보를 반환.
   *
   * @param multipartFile 업로드할 이미지 파일
   * @return 업로드된 이미지의 S3 URL, 파일 이름, 파일 크기를 포함한 S3ImageDto 객체
   */
  public S3ImageDto uploadPostImage(MultipartFile multipartFile) {
    log.info("[uploadPostImage] start");

    String fileName = generateFileName(multipartFile);
    uploadToS3(multipartFile, fileName);

    return new S3ImageDto(getUrl(fileName), fileName, multipartFile.getSize());
  }

  /**
   * 프로필 이미지를 AWS S3에 업로드하고 그에 대한 URL 을 반환.
   *
   * @param multipartFile 업로드할 이미지 파일
   * @return 업로드된 이미지의 S3 URL
   */
  public String uploadProfileImage(MultipartFile multipartFile) {
    log.info("[uploadProfileImage] start");

    String fileName = generateFileName(multipartFile);
    uploadToS3(multipartFile, fileName);

    return getUrl(fileName);
  }


  /**
   * AWS S3에 파일을 업로드.
   *
   * @param multipartFile 업로드할 파일
   * @param fileName 파일 이름
   */
  private void uploadToS3(MultipartFile multipartFile, String fileName) {
    try (InputStream inputStream = multipartFile.getInputStream()) {
      amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream,
          getObjectMetadata(multipartFile))
          .withCannedAcl(CannedAccessControlList.PublicRead));
    } catch (IOException e) {
      log.error("IOException is occurred", e);
      throw new GlobalException(INTERNAL_SERVER_ERROR);
    }
  }
  public void deleteFile(String fileName) {
    try {
      amazonS3.deleteObject(bucketName, fileName);
    } catch (AmazonS3Exception e) {
      log.error("AmazonS3Exception is occurred", e);
      throw new GlobalException(INTERNAL_SERVER_ERROR);
    }
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
