package umc.lightup.school.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class SchoolResponseDTO {

  @Builder
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SchoolListDTO{
    List<SchoolDTO> schoolList;
    Integer listSize;
    Integer totalPage;
    Long totalElements;
    Boolean isFirst;
    Boolean isLast;
  }

  @Builder
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SchoolDTO{
    Long schoolId;
    String schoolName;
  }
}
