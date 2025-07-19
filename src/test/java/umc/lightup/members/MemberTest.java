package umc.lightup.members;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import umc.lightup.api.ApiResponse;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.member.controller.MemberRestController;
import umc.lightup.member.domain.*;
import umc.lightup.member.dto.MemberRequestDTO;
import umc.lightup.member.dto.MemberResponseDTO;
import umc.lightup.member.enums.Mbti;
import umc.lightup.member.enums.Role;
import umc.lightup.member.repository.*;
import umc.lightup.member.service.MemberCommandService;
import umc.lightup.region.domain.Region;
import umc.lightup.region.repository.RegionRepository;
import umc.lightup.skill.domain.Skill;
import umc.lightup.skill.repository.SkillRepository;
import umc.lightup.strength.domain.Strength;
import umc.lightup.strength.repository.StrengthRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MemberTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberRegionRepository memberRegionRepository;
    @Autowired
    MemberSkillRepository memberSkillRepository;
    @Autowired
    MemberStrengthRepository memberStrengthRepository;
    @Autowired
    PortfolioRepository portfolioRepository;
    @Autowired
    SkillRepository skillRepository;
    @Autowired
    RegionRepository regionRepository;
    @Autowired
    StrengthRepository strengthRepository;
    @Autowired
    MemberCommandService memberCommandService;
    @Autowired
    MemberRestController memberRestController;
    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @BeforeAll
    void setup() {
        Member member1 = memberCommandService.joinMember(MemberRequestDTO.JoinDto.builder()
                .name("기본값1")
                .nickname("닉네임1")
                .gender(true)
                .birth(LocalDate.of(1980, Month.AUGUST, 1))
                .role(Role.LEADER)
                .mbti(Mbti.ENFJ)
                .email("someone@example.com")
                .phoneNumber("010-5555-6666")
                .password("password")
                .build());
        assertSame(1L, member1.getId());

        Member member2 = memberCommandService.joinMember(MemberRequestDTO.JoinDto.builder()
                .name("기본값2")
                .nickname("닉네임2")
                .gender(false)
                .birth(LocalDate.of(2000, Month.JANUARY, 1))
                .role(Role.TEAMMATE)
                .mbti(Mbti.ISTP)
                .email("someone2@example.com")
                .phoneNumber("010-2222-2222")
                .password("password")
                .build());
        assertSame(2L, member2.getId());

        Skill skill1 = Skill.builder()
                .name("스킬1")
                .build();
        skillRepository.save(skill1);
        assertSame(1L, skill1.getId());

        Skill skill2 = Skill.builder()
                .name("스킬2")
                .build();
        skillRepository.save(skill2);
        assertSame(2L, skill2.getId());

        Skill skill3 = Skill.builder()
                .name("스킬3")
                .build();
        skillRepository.save(skill3);
        assertSame(3L, skill3.getId());

        Strength strength1 = Strength.builder()
                .name("강점1")
                .build();
        strengthRepository.save(strength1);
        assertSame(1L, strength1.getId());

        Strength strength2 = Strength.builder()
                .name("강점2")
                .build();
        strengthRepository.save(strength2);
        assertSame(2L, strength2.getId());

        Strength strength3 = Strength.builder()
                .name("강점3")
                .build();
        strengthRepository.save(strength3);
        assertSame(3L, strength3.getId());

        //지역 데이터가 아직 없으므로 이것도 넣어 주어야 함
        //Region에 Builder 넣기 싫은데...

        Region region1 = Region.builder()
                .sido("시도1")
                .sigungu("시군구1")
                .build();
        regionRepository.save(region1);
        assertSame(1L, region1.getId());

        Region region2 = Region.builder()
                .sido("시도2")
                .sigungu("시군구2")
                .build();
        regionRepository.save(region2);
        assertSame(2L, region2.getId());

        Region region3 = Region.builder()
                .sido("시도3")
                .sigungu("시군구3")
                .build();
        regionRepository.save(region3);
        assertSame(3L, region3.getId());
    }

    /**
     * 닉네임 중복 회원가입 테스트에서 응답을 받기 위한 임시 클래스
     */
    static class NicknameCheck {
        private String nickname;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }

    @Test
    @DisplayName("닉네임 중복 회원가입 테스트")
    void joinDuplicateNicknameTest() throws Exception {
        //Given
        MemberRequestDTO.JoinDto joinDto = MemberRequestDTO.JoinDto.builder()
                .name("이름2")
                .nickname("닉네임2")
                .gender(true)
                .birth(LocalDate.of(2006, Month.DECEMBER, 31))
                .role(Role.LEADER)
                .mbti(Mbti.INTJ)
                .email("someone5@example.com")
                .phoneNumber("02-000-5555")
                .password("password")
                .build();

        //When
        String content = mockMvc.perform(post("/api/v1/members/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper.writeValueAsString(joinDto)))

                //Then (따지자면 여기서부터가 Then이 맞긴 함)
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        System.out.println(content);


        ApiResponse<NicknameCheck> response =
                jacksonObjectMapper.readValue(content,
                        new TypeReference<ApiResponse<NicknameCheck>>() {
                        });
        assertEquals(ErrorStatus.DUPLICATE_NICKNAME.toString(), response.getResult().getNickname());
    }


    /**
     * 휴대전화 형식 외 회원가입 테스트에서 응답을 받기 위한 임시 클래스
     */
    static class PhoneCheck {
        private String phoneNumber;

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }

    @Test
    @DisplayName("휴대전화 형식 외 회원가입 테스트")
    void joinPhonePatternTest() throws Exception {

        //Given
        MemberRequestDTO.JoinDto joinDto = MemberRequestDTO.JoinDto.builder()
                .name("이름2")
                .nickname("닉네임4")
                .gender(true)
                .birth(LocalDate.of(2006, Month.DECEMBER, 31))
                .role(Role.LEADER)
                .mbti(Mbti.INTJ)
                .email("someone5@example.com")
                .phoneNumber("jewnhfdwnshed")
                .password("password")
                .build();

        //When
        String content = mockMvc.perform(post("/api/v1/members/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper.writeValueAsString(joinDto)))

                //Then
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        System.out.println(content);

        ApiResponse<PhoneCheck> response =
                jacksonObjectMapper.readValue(content,
                        new TypeReference<ApiResponse<PhoneCheck>>() {
                        });
        assertNotNull(response.getResult().getPhoneNumber());
    }

    /**
     * 4개 테스트 때려박았는데, 원래 이게 좋은 방식은 아닌 걸로 알고 있음
     */
    @Test
    @DisplayName("전체적인 API 테스트")
    @Order(1)
    void joinLoginMyInfoAndEmptyMemberInfo() throws Exception {

        //Given
        String email3 = "someone3@example.com";
        String password3 = "password";
        String name3 = "이름3";
        String nickname3 = "닉네임3";
        LocalDate birth3 = LocalDate.of(2006, Month.DECEMBER, 31);
        Role role3 = Role.LEADER;
        Mbti mbti3 = Mbti.INTJ;
        String phoneNumber3 = "02-000-0000";
        boolean gender3 = true;
        MemberRequestDTO.JoinDto joinDto = MemberRequestDTO.JoinDto.builder()
                .name(name3)
                .nickname(nickname3)
                .gender(gender3)
                .birth(birth3)
                .role(role3)
                .mbti(mbti3)
                .email(email3)
                .phoneNumber(phoneNumber3)
                .password(password3)
                .build();

        //When
        LocalDateTime before = LocalDateTime.now();
        String joinResult = mockMvc.perform(post("/api/v1/members/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper.writeValueAsString(joinDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        LocalDateTime after = LocalDateTime.now();
        ApiResponse<MemberResponseDTO.JoinResultDTO> joinResponse =
                jacksonObjectMapper.readValue(joinResult,
                        new TypeReference<ApiResponse<MemberResponseDTO.JoinResultDTO>>() {
                        });

        //Then
        assertEquals(3L, joinResponse.getResult().getMemberId());
        assertTrue(before.isBefore(joinResponse.getResult().getCreatedAt()) ||
                before.isEqual(joinResponse.getResult().getCreatedAt()));
        assertTrue(after.isAfter(joinResponse.getResult().getCreatedAt()) ||
                after.isEqual(joinResponse.getResult().getCreatedAt()));

        //When
        String loginResult = mockMvc.perform(post("/api/v1/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper.writeValueAsString(MemberRequestDTO.PasswordLoginRequestDTO.builder()
                                .email(email3)
                                .password(password3)
                                .build())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ApiResponse<MemberResponseDTO.LoginResultDTO> loginResponse =
                jacksonObjectMapper.readValue(loginResult,
                        new TypeReference<ApiResponse<MemberResponseDTO.LoginResultDTO>>() {
                        });

        //Then
        assertEquals(3L, loginResponse.getResult().getMemberId());


        String accessToken = loginResponse.getResult().getAccessToken();

        //When
        String myInfoResult = mockMvc.perform(get("/api/v1/members/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ApiResponse<MemberResponseDTO.MyInfoDTO> myInfoResponse =
                jacksonObjectMapper.readValue(myInfoResult,
                        new TypeReference<ApiResponse<MemberResponseDTO.MyInfoDTO>>() {
                        });

        //Then
        int age3 = (int) birth3.until(before, ChronoUnit.YEARS);
        assertAll("MyInfo check",
                () -> assertEquals(3L, myInfoResponse.getResult().getId()),
                () -> assertEquals(name3, myInfoResponse.getResult().getName()),
                () -> assertEquals(nickname3, myInfoResponse.getResult().getNickname()),
                () -> assertEquals(gender3, myInfoResponse.getResult().isGender()),
                () -> assertEquals(age3, myInfoResponse.getResult().getAge()),
                () -> assertEquals(birth3, myInfoResponse.getResult().getBirth()),
                () -> assertEquals(role3, myInfoResponse.getResult().getRole()),
                () -> assertEquals(mbti3, myInfoResponse.getResult().getMbti()),
                () -> assertEquals(email3, myInfoResponse.getResult().getEmail()),
                () -> assertEquals(phoneNumber3, myInfoResponse.getResult().getPhoneNumber()),
                () -> assertNull(myInfoResponse.getResult().getSchool()),
                () -> assertNull(myInfoResponse.getResult().getCareer()),
                () -> assertNull(myInfoResponse.getResult().getProfileImageUrl())
        );

        //When
        String content = mockMvc.perform(get("/api/v1/members/3"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ApiResponse<MemberResponseDTO.MemberInfoDTO> response =
                jacksonObjectMapper.readValue(content,
                        new TypeReference<ApiResponse<MemberResponseDTO.MemberInfoDTO>>() {
                        });

        //Then
        MemberResponseDTO.MemberInfoDTO result = response.getResult();
        assertAll("member information by id approach",
                () -> assertEquals(name3, result.getName()),
                () -> assertEquals(nickname3, result.getNickname()),
                () -> assertEquals(age3, result.getAge()),
                () -> assertEquals(gender3, result.isGender()),
                () -> assertEquals(role3, result.getRole()),
                () -> assertEquals(mbti3, result.getMbti()),
                () -> assertNull(result.getCareer()),
                () -> assertNull(result.getSchool()),
                () -> assertIterableEquals(List.of(), result.getSkills()),
                () -> assertIterableEquals(List.of(), result.getStrengths()),
                () -> assertIterableEquals(List.of(), result.getRegions()),
                () -> assertIterableEquals(List.of(), result.getPortfolios()),
                () -> assertNull(result.getEmail()),
                () -> assertNull(result.getPhoneNumber()),
                () -> assertNull(result.getProfileImageUrl())
        );
    }

    @Test
    @DisplayName("회원 ID로 조회 테스트 1")
    void memberInfoWithMemberIdTest1() throws Exception {
        //Given
        Member member = memberRepository.findById(1L).get();
        List<Long> skillSet = List.of(1L, 2L);
        List<Long> strengthSet = List.of(1L, 3L);
        List<Long> regionSet = List.of(2L, 3L);
        List<Portfolio> portfolioSet = List.of(
                Portfolio.builder()
                        .member(member)
                        .name("포트폴리오1")
                        .fileUrl("url1")
                        .build(),
                Portfolio.builder()
                        .member(member)
                        .name("포트폴리오2")
                        .fileUrl("url2")
                        .build());

        skillSet.forEach(skillId -> memberSkillRepository.save(MemberSkill.builder()
                .member(member)
                .skill(skillRepository.findById(skillId).get())
                .build()));
        strengthSet.forEach(strengthId -> memberStrengthRepository.save(MemberStrength.builder()
                .member(member)
                .strength(strengthRepository.findById(strengthId).get())
                .build()));
        regionSet.forEach(regionId -> memberRegionRepository.save(MemberRegion.builder()
                .member(member)
                .region(regionRepository.findById(regionId).get())
                .build()));
        portfolioRepository.saveAll(portfolioSet);

        //When
        String content = mockMvc.perform(get("/api/v1/members/1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ApiResponse<MemberResponseDTO.MemberInfoDTO> response =
                jacksonObjectMapper.readValue(content,
                        new TypeReference<ApiResponse<MemberResponseDTO.MemberInfoDTO>>() {
                        });

        //Then
        MemberResponseDTO.MemberInfoDTO result = response.getResult();
        assertAll("basic member information",
                () -> assertEquals(member.getName(), result.getName()),
                () -> assertEquals(member.getNickname(), result.getNickname()),
                () -> assertEquals(member.getAge(), result.getAge()),
                () -> assertEquals(member.getGender(), result.isGender()),
                () -> assertEquals(member.getRole(), result.getRole()),
                () -> assertEquals(member.getMbti(), result.getMbti()),
                () -> assertEquals(member.getCareer(), result.getCareer()),
                () -> assertEquals(member.getSchool(), result.getSchool())
        );

        assertIterableEquals(
                skillSet.stream().map(skillId -> skillRepository.findById(skillId).get().getName()).toList(),
                result.getSkills(),
                "skills information"); //순서도 같도록 의도한 것

        assertIterableEquals(
                strengthSet.stream().map(strengthId -> strengthRepository.findById(strengthId).get().getName()).toList(),
                result.getStrengths(),
                "strengths information"); //순서도 같도록 의도한 것

        assertIterableEquals(
                regionSet.stream().map(regionId -> {
                    Region region = regionRepository.findById(regionId).get();
                    return region.getSido() + " " + region.getSigungu();
                }).toList(),
                result.getRegions(),
                "region information"); //순서도 같도록 의도한 것

        assertIterableEquals(
                portfolioSet.stream().map(portfolio -> MemberResponseDTO.PortfolioInfoDTO.builder()
                        .name(portfolio.getName())
                        .fileUrl(portfolio.getFileUrl())
                        .build()).toList(),
                result.getPortfolios(),
                "portfolio information"); //순서도 같도록 의도한 것

//        assertIterableEquals(
//                portfolioSet.stream().map(Portfolio::getName).toList(),
//                response.getResult().getPortfolios().stream().map(MemberResponseDTO.PortfolioInfoDTO::getName).toList(),
//                "portfolio information"); //순서도 같도록 의도한 것
//        assertIterableEquals(
//                portfolioSet.stream().map(Portfolio::getFileUrl).toList(),
//                response.getResult().getPortfolios().stream().map(MemberResponseDTO.PortfolioInfoDTO::getFileUrl).toList(),
//                "portfolio information"); //순서도 같도록 의도한 것

        assertAll("hidden information",
                () -> assertNull(result.getEmail()),
                () -> assertNull(result.getPhoneNumber()),
                () -> assertNull(result.getProfileImageUrl())
        );
    }

    @Test
    @DisplayName("회원 ID로 조회 테스트 2")
    void memberInfoWithMemberIdTest2() throws Exception {
        //Given
        Member member = memberRepository.findById(2L).get();
        List<Long> skillSet = List.of(2L, 3L);
        List<Long> strengthSet = List.of(2L);
        List<Long> regionSet = List.of(1L);
        List<Portfolio> portfolioSet = List.of(
                Portfolio.builder()
                        .member(member)
                        .name("포트폴리오2-1")
                        .fileUrl("url2-1")
                        .build());

        skillSet.forEach(skillId -> memberSkillRepository.save(MemberSkill.builder()
                .member(member)
                .skill(skillRepository.findById(skillId).get())
                .build()));
        strengthSet.forEach(strengthId -> memberStrengthRepository.save(MemberStrength.builder()
                .member(member)
                .strength(strengthRepository.findById(strengthId).get())
                .build()));
        regionSet.forEach(regionId -> memberRegionRepository.save(MemberRegion.builder()
                .member(member)
                .region(regionRepository.findById(regionId).get())
                .build()));
        portfolioRepository.saveAll(portfolioSet);

        //When
        String content = mockMvc.perform(get("/api/v1/members/2"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ApiResponse<MemberResponseDTO.MemberInfoDTO> response =
                jacksonObjectMapper.readValue(content,
                        new TypeReference<ApiResponse<MemberResponseDTO.MemberInfoDTO>>() {
                        });

        //Then
        MemberResponseDTO.MemberInfoDTO result = response.getResult();
        assertAll("basic member information",
                () -> assertEquals(member.getName(), result.getName()),
                () -> assertEquals(member.getNickname(), result.getNickname()),
                () -> assertEquals(member.getAge(), result.getAge()),
                () -> assertEquals(member.getGender(), result.isGender()),
                () -> assertEquals(member.getRole(), result.getRole()),
                () -> assertEquals(member.getMbti(), result.getMbti()),
                () -> assertEquals(member.getCareer(), result.getCareer()),
                () -> assertEquals(member.getSchool(), result.getSchool())
        );

        assertIterableEquals(
                skillSet.stream().map(skillId -> skillRepository.findById(skillId).get().getName()).toList(),
                result.getSkills(),
                "skills information"); //순서도 같도록 의도한 것

        assertIterableEquals(
                strengthSet.stream().map(strengthId -> strengthRepository.findById(strengthId).get().getName()).toList(),
                result.getStrengths(),
                "strengths information"); //순서도 같도록 의도한 것

        assertIterableEquals(
                regionSet.stream().map(regionId -> {
                    Region region = regionRepository.findById(regionId).get();
                    return region.getSido() + " " + region.getSigungu();
                }).toList(),
                result.getRegions(),
                "region information"); //순서도 같도록 의도한 것

        assertIterableEquals(
                portfolioSet.stream().map(portfolio -> MemberResponseDTO.PortfolioInfoDTO.builder()
                        .name(portfolio.getName())
                        .fileUrl(portfolio.getFileUrl())
                        .build()).toList(),
                result.getPortfolios(),
                "portfolio information"); //순서도 같도록 의도한 것

//        assertIterableEquals(
//                portfolioSet.stream().map(Portfolio::getName).toList(),
//                response.getResult().getPortfolios().stream().map(MemberResponseDTO.PortfolioInfoDTO::getName).toList(),
//                "portfolio information"); //순서도 같도록 의도한 것
//        assertIterableEquals(
//                portfolioSet.stream().map(Portfolio::getFileUrl).toList(),
//                response.getResult().getPortfolios().stream().map(MemberResponseDTO.PortfolioInfoDTO::getFileUrl).toList(),
//                "portfolio information"); //순서도 같도록 의도한 것

        assertAll("hidden information",
                () -> assertNull(result.getEmail()),
                () -> assertNull(result.getPhoneNumber()),
                () -> assertNull(result.getProfileImageUrl())
        );
    }

//    @Test
//    void myPageApi_ShouldReturnUserInfo_WhenValidJwt() throws Exception {
//        Authentication authentication = new UsernamePasswordAuthenticationToken(
//                "user@example.com",
//                null,
//                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
//        );
//
//        String token = jwtTokenProvider.generateToken(authentication);
//
//        mockMvc.perform(get("/api/mypage")
//                        .header("Authorization", "Bearer " + token))
//                .andExpect(status().isOk())
//                .andExpect((ResultMatcher) jsonPath("$.email").value("user@example.com"));
//    }


    @Test
    @DisplayName("회원정보 수정 테스트")
    void putMyInfoTest() throws Exception {

        //Given
        String email4 = "someone4@example.com";
        String password4 = "password";
        String name4 = "이름4";
        String nickname4 = "닉네임4";
        LocalDate birth4 = LocalDate.of(2006, Month.DECEMBER, 31);
        Role role4 = Role.LEADER;
        Mbti mbti4 = Mbti.INTJ;
        String phoneNumber4 = "02-000-0000";
        boolean gender4 = true;
        MemberRequestDTO.JoinDto joinDto = MemberRequestDTO.JoinDto.builder()
                .name(name4)
                .nickname(nickname4)
                .gender(gender4)
                .birth(birth4)
                .role(role4)
                .mbti(mbti4)
                .email(email4)
                .phoneNumber(phoneNumber4)
                .password(password4)
                .build();


        LocalDateTime before = LocalDateTime.now();
        String joinResult = mockMvc.perform(post("/api/v1/members/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper.writeValueAsString(joinDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        LocalDateTime after = LocalDateTime.now();
        ApiResponse<MemberResponseDTO.JoinResultDTO> joinResponse =
                jacksonObjectMapper.readValue(joinResult,
                        new TypeReference<ApiResponse<MemberResponseDTO.JoinResultDTO>>() {
                        });

        assertEquals(4L, joinResponse.getResult().getMemberId());
        assertTrue(before.isBefore(joinResponse.getResult().getCreatedAt()) ||
                before.isEqual(joinResponse.getResult().getCreatedAt()));
        assertTrue(after.isAfter(joinResponse.getResult().getCreatedAt()) ||
                after.isEqual(joinResponse.getResult().getCreatedAt()));

        String loginResult = mockMvc.perform(post("/api/v1/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper.writeValueAsString(MemberRequestDTO.PasswordLoginRequestDTO.builder()
                                .email(email4)
                                .password(password4)
                                .build())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ApiResponse<MemberResponseDTO.LoginResultDTO> loginResponse =
                jacksonObjectMapper.readValue(loginResult,
                        new TypeReference<ApiResponse<MemberResponseDTO.LoginResultDTO>>() {
                        });

        assertEquals(4L, loginResponse.getResult().getMemberId());


        String accessToken = loginResponse.getResult().getAccessToken();

        String myInfoResult = mockMvc.perform(get("/api/v1/members/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ApiResponse<MemberResponseDTO.MyInfoDTO> myInfoResponse =
                jacksonObjectMapper.readValue(myInfoResult,
                        new TypeReference<ApiResponse<MemberResponseDTO.MyInfoDTO>>() {
                        });

        int age4 = (int) birth4.until(before, ChronoUnit.YEARS);
        MemberResponseDTO.MyInfoDTO myInfoResponseResult = myInfoResponse.getResult();
        assertAll("MyInfo check",
                () -> assertEquals(4L, myInfoResponseResult.getId()),
                () -> assertEquals(name4, myInfoResponseResult.getName()),
                () -> assertEquals(nickname4, myInfoResponseResult.getNickname()),
                () -> assertEquals(gender4, myInfoResponseResult.isGender()),
                () -> assertEquals(age4, myInfoResponseResult.getAge()),
                () -> assertEquals(birth4, myInfoResponseResult.getBirth()),
                () -> assertEquals(role4, myInfoResponseResult.getRole()),
                () -> assertEquals(mbti4, myInfoResponseResult.getMbti()),
                () -> assertEquals(email4, myInfoResponseResult.getEmail()),
                () -> assertEquals(phoneNumber4, myInfoResponseResult.getPhoneNumber()),
                () -> assertNull(myInfoResponseResult.getSchool()),
                () -> assertNull(myInfoResponseResult.getCareer()),
                () -> assertNull(myInfoResponseResult.getProfileImageUrl())
        );

        //When
        //변경사항 없음
        MemberRequestDTO.ChangeDto change1 = MemberRequestDTO.ChangeDto.builder()
                .name(name4)
                .nickname(nickname4)
                .gender(gender4)
                .birth(birth4)
                .role(role4)
                .mbti(mbti4)
                .email(email4)
                .phoneNumber(phoneNumber4)
                .build();


        String change1Result = mockMvc.perform(put("/api/v1/members/me")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper.writeValueAsString(change1))
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ApiResponse<MemberResponseDTO.MyInfoDTO> change1Response =
                jacksonObjectMapper.readValue(change1Result,
                        new TypeReference<ApiResponse<MemberResponseDTO.MyInfoDTO>>() {
                        });

        //Then
        MemberResponseDTO.MyInfoDTO change1ResponseResult = change1Response.getResult();
        assertAll("MyInfo check without change",
                () -> assertEquals(4L, change1ResponseResult.getId()),
                () -> assertEquals(name4, change1ResponseResult.getName()),
                () -> assertEquals(nickname4, change1ResponseResult.getNickname()),
                () -> assertEquals(gender4, change1ResponseResult.isGender()),
                () -> assertEquals(age4, change1ResponseResult.getAge()),
                () -> assertEquals(birth4, change1ResponseResult.getBirth()),
                () -> assertEquals(role4, change1ResponseResult.getRole()),
                () -> assertEquals(mbti4, change1ResponseResult.getMbti()),
                () -> assertEquals(email4, change1ResponseResult.getEmail()),
                () -> assertEquals(phoneNumber4, change1ResponseResult.getPhoneNumber()),
                () -> assertNull(change1ResponseResult.getSchool()),
                () -> assertNull(change1ResponseResult.getCareer()),
                () -> assertNull(change1ResponseResult.getProfileImageUrl())
        );


        //When
        //중복 조건 없는 새로운 변경사항
        LocalDate changedBirth = LocalDate.of(1996, Month.JANUARY, 1);
        MemberRequestDTO.ChangeDto change2 = MemberRequestDTO.ChangeDto.builder()
                .name("name4")
                .nickname(nickname4)
                .gender(false)
                .birth(changedBirth)
                .role(Role.TEAMMATE)
                .mbti(Mbti.ENFJ)
                .email(email4)
                .phoneNumber(phoneNumber4)
                .build();


        String change2Result = mockMvc.perform(put("/api/v1/members/me")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper.writeValueAsString(change2))
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ApiResponse<MemberResponseDTO.MyInfoDTO> change2Response =
                jacksonObjectMapper.readValue(change2Result,
                        new TypeReference<ApiResponse<MemberResponseDTO.MyInfoDTO>>() {
                        });

        //Then
        MemberResponseDTO.MyInfoDTO change2ResponseResult = change2Response.getResult();
        int changedAge = (int) changedBirth.until(before, ChronoUnit.YEARS);
        assertAll("MyInfo check with non-unique changes",
                () -> assertEquals(4L, change2ResponseResult.getId()),
                () -> assertEquals("name4", change2ResponseResult.getName()),
                () -> assertEquals(nickname4, change2ResponseResult.getNickname()),
                () -> assertEquals(false, change2ResponseResult.isGender()),
                () -> assertEquals(changedAge, change2ResponseResult.getAge()),
                () -> assertEquals(changedBirth, change2ResponseResult.getBirth()),
                () -> assertEquals(Role.TEAMMATE, change2ResponseResult.getRole()),
                () -> assertEquals(Mbti.ENFJ, change2ResponseResult.getMbti()),
                () -> assertEquals(email4, change2ResponseResult.getEmail()),
                () -> assertEquals(phoneNumber4, change2ResponseResult.getPhoneNumber()),
                () -> assertNull(change2ResponseResult.getSchool()),
                () -> assertNull(change2ResponseResult.getCareer()),
                () -> assertNull(change2ResponseResult.getProfileImageUrl())
        );

        //When
        //중복 조건 변경사항
        MemberRequestDTO.ChangeDto change3 = MemberRequestDTO.ChangeDto.builder()
                .name(name4)
                .nickname(null)
                .gender(gender4)
                .birth(birth4)
                .role(role4)
                .mbti(mbti4)
                .email("newEmail@example.com")
                .phoneNumber("010-1111-2222")
                .build();


        String change3Result = mockMvc.perform(put("/api/v1/members/me")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper.writeValueAsString(change3))
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ApiResponse<MemberResponseDTO.MyInfoDTO> change3Response =
                jacksonObjectMapper.readValue(change3Result,
                        new TypeReference<ApiResponse<MemberResponseDTO.MyInfoDTO>>() {
                        });

        //Then
        MemberResponseDTO.MyInfoDTO change3ResponseResult = change3Response.getResult();
        assertAll("MyInfo check with unique changes",
                () -> assertEquals(4L, change3ResponseResult.getId()),
                () -> assertEquals(name4, change3ResponseResult.getName()),
                () -> assertNull(change3ResponseResult.getNickname()),
                () -> assertEquals(gender4, change3ResponseResult.isGender()),
                () -> assertEquals(age4, change3ResponseResult.getAge()),
                () -> assertEquals(birth4, change3ResponseResult.getBirth()),
                () -> assertEquals(role4, change3ResponseResult.getRole()),
                () -> assertEquals(mbti4, change3ResponseResult.getMbti()),
                () -> assertEquals("newEmail@example.com", change3ResponseResult.getEmail()),
                () -> assertEquals("010-1111-2222", change3ResponseResult.getPhoneNumber()),
                () -> assertNull(change3ResponseResult.getSchool()),
                () -> assertNull(change3ResponseResult.getCareer()),
                () -> assertNull(change3ResponseResult.getProfileImageUrl())
        );

        //이제 이메일이 바뀌었으니 에러가 나야 함
        mockMvc.perform(get("/api/v1/members/me")
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("중복 조건 회원정보수정 테스트")
    void putMyInfoDuplicatedTest() throws Exception {
        //Given
        //BeforeAll에 있던 거
        String loginResult = mockMvc.perform(post("/api/v1/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper.writeValueAsString(MemberRequestDTO.PasswordLoginRequestDTO.builder()
                                .email("someone2@example.com")
                                .password("password")
                                .build())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ApiResponse<MemberResponseDTO.LoginResultDTO> loginResponse =
                jacksonObjectMapper.readValue(loginResult,
                        new TypeReference<ApiResponse<MemberResponseDTO.LoginResultDTO>>() {
                        });

        assertEquals(2L, loginResponse.getResult().getMemberId());


        String accessToken = loginResponse.getResult().getAccessToken();

        String myInfoResult = mockMvc.perform(get("/api/v1/members/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ApiResponse<MemberResponseDTO.MyInfoDTO> myInfoResponse =
                jacksonObjectMapper.readValue(myInfoResult,
                        new TypeReference<ApiResponse<MemberResponseDTO.MyInfoDTO>>() {
                        });
        MemberResponseDTO.MyInfoDTO myInfoResponseResult = myInfoResponse.getResult();

        //When
        //닉네임
        MemberRequestDTO.ChangeDto change1 = MemberRequestDTO.ChangeDto.builder()
                .name(myInfoResponseResult.getName())
                .nickname("닉네임1")
                .gender(myInfoResponseResult.isGender())
                .birth(myInfoResponseResult.getBirth())
                .role(myInfoResponseResult.getRole())
                .mbti(myInfoResponseResult.getMbti())
                .email(myInfoResponseResult.getEmail())
                .phoneNumber(myInfoResponseResult.getPhoneNumber())
                .build();


        String change1Result = mockMvc.perform(put("/api/v1/members/me")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper.writeValueAsString(change1))
                )
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        ApiResponse<Void> change1Response =
                jacksonObjectMapper.readValue(change1Result,
                        new TypeReference<ApiResponse<Void>>() {
                        });

        //Then
        assertAll("MyInfo check with duplicated nickname",
                () -> assertEquals(ErrorStatus.DUPLICATE_NICKNAME.getCode(), change1Response.getCode()),
                () -> assertEquals(ErrorStatus.DUPLICATE_NICKNAME.getMessage(), change1Response.getMessage())
        );

        //When
        //이메일 중복
        MemberRequestDTO.ChangeDto change2 = MemberRequestDTO.ChangeDto.builder()
                .name(myInfoResponseResult.getName())
                .nickname(myInfoResponseResult.getNickname())
                .gender(myInfoResponseResult.isGender())
                .birth(myInfoResponseResult.getBirth())
                .role(myInfoResponseResult.getRole())
                .mbti(myInfoResponseResult.getMbti())
                .email("someone@example.com")
                .phoneNumber(myInfoResponseResult.getPhoneNumber())
                .build();


        String change2Result = mockMvc.perform(put("/api/v1/members/me")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper.writeValueAsString(change2))
                )
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        ApiResponse<Void> change2Response =
                jacksonObjectMapper.readValue(change2Result,
                        new TypeReference<ApiResponse<Void>>() {
                        });

        //Then
        assertAll("MyInfo check with duplicated email",
                () -> assertEquals(ErrorStatus.DUPLICATE_EMAIL.getCode(), change2Response.getCode()),
                () -> assertEquals(ErrorStatus.DUPLICATE_EMAIL.getMessage(), change2Response.getMessage())
        );

        //When
        //휴대전화 중복
        MemberRequestDTO.ChangeDto change3 = MemberRequestDTO.ChangeDto.builder()
                .name(myInfoResponseResult.getName())
                .nickname(myInfoResponseResult.getNickname())
                .gender(myInfoResponseResult.isGender())
                .birth(myInfoResponseResult.getBirth())
                .role(myInfoResponseResult.getRole())
                .mbti(myInfoResponseResult.getMbti())
                .email(myInfoResponseResult.getEmail())
                .phoneNumber("010-5555-6666")
                .build();


        String change3Result = mockMvc.perform(put("/api/v1/members/me")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper.writeValueAsString(change3))
                )
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        ApiResponse<Void> change3Response =
                jacksonObjectMapper.readValue(change3Result,
                        new TypeReference<ApiResponse<Void>>() {
                        });

        //Then
        assertAll("MyInfo check with duplicated phone number",
                () -> assertEquals(ErrorStatus.DUPLICATE_PHONE_NUMBER.getCode(), change3Response.getCode()),
                () -> assertEquals(ErrorStatus.DUPLICATE_PHONE_NUMBER.getMessage(), change3Response.getMessage())
        );
    }
}