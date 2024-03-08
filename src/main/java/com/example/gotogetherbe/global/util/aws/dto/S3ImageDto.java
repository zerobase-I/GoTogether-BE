package com.example.gotogetherbe.global.util.aws.dto;


import com.example.gotogetherbe.global.util.aws.entity.PostImage;
import lombok.Builder;

@Builder
public record S3ImageDto(

    Long id,
    String url,
    String fileName,
    Long size
) {
  public static S3ImageDto fromEntity(PostImage postImage) {
    return S3ImageDto.builder()
        .id(postImage.getId())
        .url(postImage.getUrl())
        .fileName(postImage.getFileName())
        .size(postImage.getSize())
        .build();
  }
  public PostImage toEntity() {
    return PostImage.builder()
        .url(url)
        .fileName(fileName)
        .size(size)
        .build();
  }
}