package com.example.gotogetherbe.global.service;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

/**
 * Redis DB 데이터 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

  private final RedisTemplate<String, Object> redisTemplate;

  /**
   * 주어진 key 에 대응하는 데이터를 Redis DB 에서 조회
   *
   * @param key 조회하고자 하는 데이터의 key
   * @return key 에 대응하는 데이터
   */
  public String getData(String key) {
    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
    return (String) valueOperations.get(key);
  }


  /**
   * 주어진 key 와 value 를 Redis DB에 저장. 저장된 데이터는 주어진 시간이 지나면 자동으로 삭제.
   *
   * @param key         저장하고자 하는 데이터의 key
   * @param value       저장하고자 하는 데이터의 value
   * @param expiredTime 데이터의 만료 시간 (밀리초 단위)
   */
  public void setDataExpire(String key, String value, Long expiredTime) {
    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
    valueOperations.set(key, value, Duration.ofMillis(expiredTime));
  }

  /**
   * 주어진 key 에 대응하는 데이터가 Redis DB에 존재하는지 확인.
   *
   * @param key 확인하고자 하는 데이터의 key
   * @return 데이터 존재 여부
   */
  public boolean existData(String key) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }


  /**
   * 주어진 key 에 대응하는 데이터를 Redis DB 에서 삭제.
   *
   * @param key 삭제하고자 하는 데이터의 key
   */
  public void deleteData(String key) {
    try {
      Boolean result = redisTemplate.delete(key);
      if (Boolean.TRUE.equals(result)) {
        log.info("Successfully deleted key : {}", key);
      } else {
        log.warn("Failed to delete key : {}", key);
      }
    } catch (Exception e) {
      log.error("Error occurred while deleting key : {}", key, e);
    }
  }


}
