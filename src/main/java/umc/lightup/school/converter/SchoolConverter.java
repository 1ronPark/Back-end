package umc.lightup.school.converter;

import org.springframework.data.domain.Page;
import umc.lightup.school.domain.School;
import umc.lightup.school.dto.SchoolResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class SchoolConverter {
  public static SchoolResponseDTO.SchoolDTO schoolDTO(School school){
    return SchoolResponseDTO.SchoolDTO.builder()
            .schoolId(school.getId())
            .schoolName(school.getName())
            .build();
  }

  public static SchoolResponseDTO.SchoolListDTO schoolListDTO(Page<School> schoolList){
    List<SchoolResponseDTO.SchoolDTO> schoolListDTO = schoolList.stream()
            .map(SchoolConverter::schoolDTO).collect(Collectors.toList());
    return SchoolResponseDTO.SchoolListDTO.builder()
            .isLast(schoolList.isLast())
            .isFirst(schoolList.isFirst())
            .totalPage(schoolList.getTotalPages())
            .totalElements(schoolList.getTotalElements())
            .listSize(schoolListDTO.size())
            .schoolList(schoolListDTO)
            .build();
  }
}
