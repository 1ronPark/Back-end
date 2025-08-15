package umc.lightup.school.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import umc.lightup.school.domain.School;
import umc.lightup.school.repository.SchoolRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchoolQueryServiceImpl implements SchoolQueryService {

  private final SchoolRepository schoolRepository;

  @Override
  public Page<School> getSchoolList(String keyword, Integer page, Integer size) {
    if(keyword == null || keyword.trim().isEmpty()){
      return  schoolRepository.findAll(PageRequest.of(page, size));
    }
    return schoolRepository.findAllByNameContaining(keyword, PageRequest.of(page, size));
  }
}
