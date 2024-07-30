package com.swoo.fitlog.api.domain.workout.log.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swoo.fitlog.api.domain.jwt.service.TokenService;
import com.swoo.fitlog.api.domain.user.MemberStatus;
import com.swoo.fitlog.api.domain.user.dto.MemberDto;
import com.swoo.fitlog.api.domain.user.entity.Member;
import com.swoo.fitlog.api.domain.user.repository.MemberRepository;
import com.swoo.fitlog.api.domain.workout.log.dto.WorkoutLogResDTO;
import com.swoo.fitlog.api.domain.workout.log.dto.WorkoutLogSaveDTO;
import com.swoo.fitlog.api.domain.workout.log.dto.WorkoutLogUpdateDTO;
import com.swoo.fitlog.api.domain.workout.log.repository.WorkoutLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class WorkoutLogControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    WorkoutLogRepository workoutLogRepository;

    @Autowired
    TokenService tokenService;

    @Autowired
    ObjectMapper objectMapper;

    Member member;

    String accessToken;

    @BeforeEach
    void beforeEach() {
        Member createdMember = Member.builder()
                .email("test@test.com")
                .password("123456")
                .status(MemberStatus.NEW)
                .build();
        member = memberRepository.save(createdMember);

        MemberDto memberDto = MemberDto.builder()
                .email(member.getEmail())
                .password(member.getPassword())
                .build();
        accessToken = tokenService.authenticateUserAndGenerateToken(memberDto).getAccessToken();
    }

    @AfterEach
    void afterEach() {
        memberRepository.deleteById(member.getId());
    }

    @Test
    @DisplayName("특정 사용자가 특정 날짜에 운동을 저장하고 조회 성공")
    void createAndFindWorkoutLog() throws Exception {
        // given
        // when
        WorkoutLogSaveDTO workoutLog = WorkoutLogSaveDTO.builder()
                .memberEmail("test@test.com")
                .workoutIds(List.of(1, 2))
                .createdDate(LocalDate.now())
                .build();
        String convertWorkoutLogs = objectMapper.writeValueAsString(workoutLog);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/workouts/logs")
                        .header("Authorization", "Bearer ".concat(accessToken))
                        .contentType("Application/JSON; charset=utf-8")
                        .content(convertWorkoutLogs))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.data",
                        Matchers.hasSize(2)
                ))
                .andDo(print());
    }

    @Test
    @DisplayName("메모 수정 성공")
    void getWorkoutLogs() throws Exception {
        // given
        // when
        WorkoutLogSaveDTO workoutLogForSave = WorkoutLogSaveDTO.builder()
                .memberEmail("test@test.com")
                .workoutIds(List.of(1, 2))
                .createdDate(LocalDate.now())
                .build();
        List<Long> keys = workoutLogRepository.save(workoutLogForSave);
        Long firstKey = keys.get(0);

        WorkoutLogUpdateDTO workoutLogUpdate = WorkoutLogUpdateDTO.builder().note("테스트 메모를 수정합니다.").build();

        // then
        String URI = "/api/v1/workouts/logs/".concat(firstKey.toString());
        mockMvc.perform(MockMvcRequestBuilders.patch(URI)
                        .header("Authorization", "Bearer ".concat(accessToken))
                        .contentType("application/json;charset=utf-8")
                        .content(objectMapper.writeValueAsString(workoutLogUpdate)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.data.note",
                        Matchers.is("테스트 메모를 수정합니다."))
                )
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("운동 기록 단건 삭제")
    void deleteWorkoutLog() throws Exception {
        // given
        WorkoutLogSaveDTO workoutLogSaveDTO = WorkoutLogSaveDTO.builder()
                .createdDate(LocalDate.now())
                .memberEmail("test@test.com")
                .workoutIds(List.of(1, 2))
                .build();
        List<Long> keys = workoutLogRepository.save(workoutLogSaveDTO);
        Long firstKey = keys.get(0);

        // when
        String URI = "/api/v1/workouts/logs/" + firstKey;
        mockMvc.perform(MockMvcRequestBuilders.delete(URI)
                        .header("Authorization", "Bearer ".concat(accessToken)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());

        // then
        assertThatThrownBy(() -> workoutLogRepository.findById(firstKey))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    @DisplayName("특정 회원의 특정 날짜 운동 기록 모두 삭제")
    void deleteWorkoutLogs() throws Exception {
        // given
        WorkoutLogSaveDTO workoutLogSaveDTO = WorkoutLogSaveDTO.builder()
                .createdDate(LocalDate.now())
                .memberEmail("test@test.com")
                .workoutIds(List.of(1, 2))
                .build();
        List<Long> keys = workoutLogRepository.save(workoutLogSaveDTO);

        // when
        String URI = "/api/v1/workouts/logs";
        mockMvc.perform(MockMvcRequestBuilders.delete(URI)
                        .header("Authorization", "Bearer ".concat(accessToken))
                        .param("email", workoutLogSaveDTO.getMemberEmail())
                        .param("date", workoutLogSaveDTO.getCreatedDate().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());

        // then
        List<WorkoutLogResDTO> workoutLogResDTOS = workoutLogRepository.findByMemberAndDate(
                workoutLogSaveDTO.getMemberEmail(),
                workoutLogSaveDTO.getCreatedDate()
        );

        assertThat(workoutLogResDTOS).isEmpty();
    }
}