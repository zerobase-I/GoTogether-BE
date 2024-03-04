package com.example.gotogetherbe.global.util.aws.dto;


import com.example.gotogetherbe.global.util.aws.entity.PostImage;

public record S3ImageDto(
    String url,
    String fileName,
    Long size
) {
  public PostImage toEntity() {
    return PostImage.builder()
        .url(url)
        .fileName(fileName)
        .size(size)
        .build();
  }
}