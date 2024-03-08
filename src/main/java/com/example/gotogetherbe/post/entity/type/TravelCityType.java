package com.example.gotogetherbe.post.entity.type;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TravelCityType {
  SEOUL(TravelCountryType.KOREA),
  BUSAN(TravelCountryType.KOREA),
  TOKYO(TravelCountryType.JAPAN),
  OSAKA(TravelCountryType.JAPAN),
  NEWYORK(TravelCountryType.USA),
  LOSANGELES(TravelCountryType.USA);

  private final TravelCountryType country;

}
