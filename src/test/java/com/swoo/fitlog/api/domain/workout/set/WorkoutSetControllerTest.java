package com.swoo.fitlog.api.domain.workout.set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swoo.fitlog.api.domain.jwt.dto.tokenDto;
import com.swoo.fitlog.api.domain.jwt.service.TokenService;
import com.swoo.fitlog.api.domain.user.MemberStatus;
import com.swoo.fitlog.api.domain.user.dto.MemberDto;
import com.swoo.fitlog.api.domain.user.entity.Member;
import com.swoo.fitlog.api.domain.user.repository.MemberRepository;
import com.swoo.fitlog.api.domain.workout.log.dto.WorkoutLogSaveDTO;
import com.swoo.fitlog.api.domain.workout.log.repository.WorkoutLogRepository;
import com.swoo.fitlog.api.domain.workout.set.dto.WorkoutSetResDTO;
import com.swoo.fitlog.api.domain.workout.set.dto.WorkoutSetSaveDTO;
import com.swoo.fitlog.api.domain.workout.set.dto.WorkoutSetUpdateDTO;
import com.swoo.fitlog.api.domain.workout.set.repsitory.WorkoutSetRepository;
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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WorkoutSetControllerTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    WorkoutLogRepository workoutLogRepository;

    @Autowired
    WorkoutSetRepository workoutSetRepository;

    @Autowired
    TokenService tokenService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    String accessToken;

    Member member;

    @BeforeEach
    void beforeEach() {
        Member createMember = Member.builder()
                .email("test@test.com")
                .password("123456")
                .status(MemberStatus.NEW)
                .build();
        member = memberRepository.save(createMember);

        MemberDto memberDto = MemberDto.builder()
                .email(createMember.getEmail())
                .password(createMember.getPassword())
                .build();
        tokenDto tokenDto = tokenService.authenticateUserAndGenerateToken(memberDto);
        accessToken = tokenDto.getAccessToken();
    }

    @AfterEach
    void afterEach() {
        memberRepository.deleteById(member.getId());
    }

    @Test
    @DisplayName("세트 추가")
    void sets() throws Exception {
        WorkoutLogSaveDTO workoutLogSaveDTO = WorkoutLogSaveDTO.builder()
                .createdDate(LocalDate.now())
                .memberEmail("test@test.com")
                .workoutIds(List.of(1))
                .build();
        List<Long> keys = workoutLogRepository.save(workoutLogSaveDTO);
        long key = keys.get(0);

        WorkoutSetSaveDTO workoutSetSaveDTO = WorkoutSetSaveDTO.builder()
                .workoutLogId(key)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/workouts/logs/sets")
                        .header("Authorization", "Bearer ".concat(accessToken))
                        .contentType("application/json;charset=utf-8")
                        .content(objectMapper.writeValueAsString(workoutSetSaveDTO))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numberOfPerform", is(0)))
                .andExpect(jsonPath("$.data.weight", is(0)))
                .andDo(print());
    }

    @Test
    @DisplayName("세트 수정: 무게, 횟수, 무게 & 횟수")
    void update() throws Exception {
        WorkoutLogSaveDTO workoutLogSaveDTO = WorkoutLogSaveDTO.builder()
                .createdDate(LocalDate.now())
                .memberEmail("test@test.com")
                .workoutIds(List.of(1))
                .build();
        List<Long> keys = workoutLogRepository.save(workoutLogSaveDTO);
        long key = keys.get(0);

        WorkoutSetSaveDTO workoutSetSaveDTO = WorkoutSetSaveDTO.builder()
                .workoutLogId(key)
                .build();
        long savedSetKey = workoutSetRepository.save(workoutSetSaveDTO);


        String URI = "/api/v1/workouts/logs/sets/" + savedSetKey;

        // 무게만 수정
        WorkoutSetUpdateDTO workoutSetUpdate = WorkoutSetUpdateDTO.builder().weight(50).build();
        mockMvc.perform(MockMvcRequestBuilders.patch(URI)
                        .header("Authorization", "Bearer ".concat(accessToken))
                        .contentType("application/json;charset=utf-8")
                        .content(objectMapper.writeValueAsString(workoutSetUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.weight", is(50)))
                .andDo(print());

        // 횟수만 수정
        workoutSetUpdate = WorkoutSetUpdateDTO.builder().numberOfPerform(10).build();
        mockMvc.perform(MockMvcRequestBuilders.patch(URI)
                        .header("Authorization", "Bearer ".concat(accessToken))
                        .contentType("application/json;charset=utf-8")
                        .content(objectMapper.writeValueAsString(workoutSetUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numberOfPerform", is(10)))
                .andDo(print());

        // 무게 & 횟수 수정
        workoutSetUpdate = WorkoutSetUpdateDTO.builder().numberOfPerform(20).weight(40).build();
        mockMvc.perform(MockMvcRequestBuilders.patch(URI)
                        .header("Authorization", "Bearer ".concat(accessToken))
                        .contentType("application/json;charset=utf-8")
                        .content(objectMapper.writeValueAsString(workoutSetUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numberOfPerform", is(20)))
                .andExpect(jsonPath("$.data.weight", is(40)))
                .andDo(print());
    }

    @Test
    @DisplayName("세트 하나 삭제")
    void deleteToOnlyOne() throws Exception {
        // given

        // 운동 기록을 저장한다.
        WorkoutLogSaveDTO workoutLogSaveDTO = WorkoutLogSaveDTO.builder()
                .memberEmail("test@test.com")
                .createdDate(LocalDate.now())
                .workoutIds(List.of(1))
                .build();
        List<Long> keys = workoutLogRepository.save(workoutLogSaveDTO);
        long key = keys.get(0);

        // 삭제에 필요한 세트를 저장한다.
        WorkoutSetSaveDTO workoutSetSaveDTO = WorkoutSetSaveDTO.builder()
                .workoutLogId(key)
                .build();
        long savedWorkoutSetKey = workoutSetRepository.save(workoutSetSaveDTO);

        // when
        String URI = "/api/v1/workouts/logs/sets/" + savedWorkoutSetKey;
        mockMvc.perform(MockMvcRequestBuilders.delete(URI)
                .header("Authorization", "Bearer ".concat(accessToken)))
                .andExpect(status().isOk());

        // then
        assertThatThrownBy(() -> workoutSetRepository.findById(savedWorkoutSetKey))
                .isInstanceOf(EmptyResultDataAccessException.class);

    }

    @Test
    @DisplayName("특정 운동 기록의 모든 세트 삭제")
    void deleteAllByWorkoutLog() throws Exception {
        // given

        // 운동 기록을 생성한다.
        WorkoutLogSaveDTO workoutLogSaveDTO = WorkoutLogSaveDTO.builder()
                .createdDate(LocalDate.now())
                .memberEmail("test@test.com")
                .workoutIds(List.of(1))
                .build();
        List<Long> keys = workoutLogRepository.save(workoutLogSaveDTO);
        long key = keys.get(0);

        // 삭제에 필요한 세트를 저장한다.
        WorkoutSetSaveDTO workoutSetSaveDTO = WorkoutSetSaveDTO.builder()
                .workoutLogId(key)
                .build();
        workoutSetRepository.save(workoutSetSaveDTO);

        workoutSetSaveDTO = WorkoutSetSaveDTO.builder()
                .workoutLogId(key)
                .build();
        workoutSetRepository.save(workoutSetSaveDTO);

        // when
        String URI = String.format("/api/v1/workouts/logs/%s/sets", key);
        mockMvc.perform(MockMvcRequestBuilders.delete(URI)
                .header("Authorization", "Bearer ".concat(accessToken)))
                .andExpect(status().isOk())
                .andDo(print());

        // then
        List<WorkoutSetResDTO> workoutSetResDTOS = workoutSetRepository.findAllByWorkoutLogId(key);
        assertThat(workoutSetResDTOS).isEmpty();
    }
}