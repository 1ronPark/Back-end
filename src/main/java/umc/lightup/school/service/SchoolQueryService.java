package umc.lightup.school.service;

import org.springframework.data.domain.Page;
import umc.lightup.school.domain.School;

public interface SchoolQueryService {
  Page<School> getSchoolList(String name, Integer page, Integer size);
}
