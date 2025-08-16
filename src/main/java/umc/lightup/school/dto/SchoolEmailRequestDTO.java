package umc.lightup.school.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SchoolEmailRequestDTO {
  @Builder
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SchoolEmailDTO {
    String userName;
    String code;
  }
}
