package com.swoo.fitlog.api.domain.workout.log.repository;

import com.swoo.fitlog.api.domain.user.MemberStatus;
import com.swoo.fitlog.api.domain.user.entity.Member;
import com.swoo.fitlog.api.domain.user.repository.MemberRepository;
import com.swoo.fitlog.api.domain.workout.log.dto.WorkoutLogResDTO;
import com.swoo.fitlog.api.domain.workout.log.dto.WorkoutLogSaveDTO;
import com.swoo.fitlog.api.domain.workout.log.dto.WorkoutLogUpdateDTO;
import com.swoo.fitlog.api.domain.workout.set.dto.WorkoutSetResDTO;
import com.swoo.fitlog.api.domain.workout.set.dto.WorkoutSetSaveDTO;
import com.swoo.fitlog.api.domain.workout.set.repsitory.WorkoutSetRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
class WorkoutLogJdbcRepositoryTest {

    @Autowired
    WorkoutLogRepository workoutLogRepository;

    @Autowired
    WorkoutSetRepository workoutSetRepository;

    @Autowired
    MemberRepository memberRepository;

    Member saveMember;

    @BeforeEach
    void beforeEach() {
        Member member = Member.builder()
                .email("test@test.com")
                .password("123456")
                .status(MemberStatus.NEW)
                .build();

        saveMember = memberRepository.save(member);
    }

    @AfterEach
    void afterEach() {
        memberRepository.deleteById(saveMember.getId());
        workoutLogRepository.deleteByMemberAndDate(saveMember.getEmail(), LocalDate.now());
    }

    @Test
    @DisplayName("이전에 기록한 적이 없는 운동 추가")
    void saveFirstTime() {
        // given
        WorkoutLogSaveDTO workoutLogForSave = WorkoutLogSaveDTO.builder()
                .memberEmail("test@test.com")
                .createdDate(LocalDate.now())
                .workoutIds(List.of(1))
                .build();

        // when
        List<Long> keys = workoutLogRepository.save(workoutLogForSave);

        // then
        assertThat(keys.get(0)).isEqualTo(1);
    }

    @Test
    @DisplayName("이전에 기록한 데이터가 있는 운동을 추가할 때 메모가 불러와지는지 체크")
    void saveSecondTime() {
        // given
        WorkoutLogSaveDTO workoutLogForSave = WorkoutLogSaveDTO.builder()
                .memberEmail("test@test.com")
                .createdDate(LocalDate.of(2024, 6, 3))
                .workoutIds(List.of(1))
                .build();

        List<Long> keys = workoutLogRepository.save(workoutLogForSave);
        long key = keys.get(0);

        WorkoutLogUpdateDTO workoutLogUpdate = WorkoutLogUpdateDTO.builder()
                .note("2024년 6월 3월에 작성한 테스트 메모입니다.")
                .build();
        workoutLogRepository.update(key, workoutLogUpdate);

        // when

        // 이전에 추가한 메모가 올바르게 불러와지는지 추가한 날짜 이후에 운동 기록을 다시 추가한다.
        workoutLogForSave = WorkoutLogSaveDTO.builder()
                .memberEmail("test@test.com")
                .createdDate(LocalDate.now())
                .workoutIds(List.of(1))
                .build();
        keys = workoutLogRepository.save(workoutLogForSave);
        key = keys.get(0);

        // then
        WorkoutLogResDTO workoutLog = workoutLogRepository.findById(key);
        assertThat(workoutLog.getNote()).isEqualTo("2024년 6월 3월에 작성한 테스트 메모입니다.");
    }

    @Test
    @DisplayName("메모 수정")
    void update() {
        // given
        WorkoutLogSaveDTO workoutLogForSave = WorkoutLogSaveDTO.builder()
                .memberEmail("test@test.com")
                .createdDate(LocalDate.now())
                .workoutIds(List.of(1))
                .build();
        List<Long> keys = workoutLogRepository.save(workoutLogForSave);
        long key = keys.get(0);

        // when
        WorkoutLogUpdateDTO updatedWorkoutLog = WorkoutLogUpdateDTO.builder().note("수정된 메모입니다.").build();
        workoutLogRepository.update(key, updatedWorkoutLog);

        // then
        WorkoutLogResDTO findWorkoutLog = workoutLogRepository.findById(key);
        assertThat(findWorkoutLog.getNote()).isEqualTo("수정된 메모입니다.");
    }

    @Test
    @DisplayName("저장한 세트가 없을 때 운동 기록을 찾는 경우")
    void findById() {
        // given
        WorkoutLogSaveDTO workoutLogSave = WorkoutLogSaveDTO.builder()
                .memberEmail("test@test.com")
                .createdDate(LocalDate.now())
                .workoutIds(List.of(1))
                .build();
        List<Long> keys = workoutLogRepository.save(workoutLogSave);
        long savedWorkoutLogKey = keys.get(0);

        // when
        WorkoutLogResDTO findWorkoutLogRes = workoutLogRepository.findById(savedWorkoutLogKey);

        // then
        assertThat(findWorkoutLogRes.getId()).isEqualTo(savedWorkoutLogKey);
        assertThat(findWorkoutLogRes.getWorkoutSets().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("저장한 세트가 있을 때 운동 기록을 찾는 경우")
    void findByIdExistSet() {
        // given
        WorkoutLogSaveDTO workoutLogSave = WorkoutLogSaveDTO.builder()
                .memberEmail("test@test.com")
                .createdDate(LocalDate.now())
                .workoutIds(List.of(1))
                .build();
        List<Long> keys = workoutLogRepository.save(workoutLogSave);
        long savedWorkoutLogKey = keys.get(0);

        // when
        workoutSetRepository.save(WorkoutSetSaveDTO.builder().workoutLogId(savedWorkoutLogKey).build());
        workoutSetRepository.save(WorkoutSetSaveDTO.builder().workoutLogId(savedWorkoutLogKey).build());

        WorkoutLogResDTO findWorkoutLog = workoutLogRepository.findById(savedWorkoutLogKey);
        log.info(findWorkoutLog.getWorkoutSets().toString());

        // then
        assertThat(findWorkoutLog.getId()).isEqualTo(savedWorkoutLogKey);
        assertThat(findWorkoutLog.getWorkoutSets().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("여러 운동 기록을 저장하고 각각의 운동 기록에 세트가 있는 경우")
    void findByMemberAndDate() {
        // given
        WorkoutLogSaveDTO workoutLogSave = WorkoutLogSaveDTO.builder()
                .memberEmail("test@test.com")
                .createdDate(LocalDate.now())
                .workoutIds(List.of(1, 2))
                .build();
        List<Long> keys = workoutLogRepository.save(workoutLogSave);

        // 세트 추가
        workoutSetRepository.save(WorkoutSetSaveDTO.builder().workoutLogId(keys.get(0)).build());
        workoutSetRepository.save(WorkoutSetSaveDTO.builder().workoutLogId(keys.get(0)).build());
        workoutSetRepository.save(WorkoutSetSaveDTO.builder().workoutLogId(keys.get(0)).build());

        workoutSetRepository.save(WorkoutSetSaveDTO.builder().workoutLogId(keys.get(1)).build());
        workoutSetRepository.save(WorkoutSetSaveDTO.builder().workoutLogId(keys.get(1)).build());

        // when
        List<WorkoutLogResDTO> findAllByMemberAndDate =
                workoutLogRepository.findByMemberAndDate("test@test.com", LocalDate.now());

        // then
        assertThat(findAllByMemberAndDate).extracting("id").containsOnly(keys.get(0), keys.get(1));

        findAllByMemberAndDate.forEach((workoutLogResDTO) -> {
            if (workoutLogResDTO.getId() == 1L) {
                assertThat(workoutLogResDTO.getWorkoutSets()).hasSize(3);
                assertThat(workoutLogResDTO.getWorkoutSets()).extracting(WorkoutSetResDTO::getId).isSorted();
            } else if (workoutLogResDTO.getId() == 2L) {
                assertThat(workoutLogResDTO.getWorkoutSets()).hasSize(2);
                assertThat(workoutLogResDTO.getWorkoutSets()).extracting(WorkoutSetResDTO::getId).isSorted();
            }
        });
    }

    @Test
    @DisplayName("단건 운동 기록 삭제")
    void deleteById() {
        // given
        WorkoutLogSaveDTO workoutLogSave = WorkoutLogSaveDTO.builder()
                .memberEmail("test@test.com")
                .createdDate(LocalDate.now())
                .workoutIds(List.of(1))
                .build();
        List<Long> keys = workoutLogRepository.save(workoutLogSave);
        long savedWorkoutLogKey = keys.get(0);

        // when
        workoutLogRepository.deleteById(savedWorkoutLogKey);

        // then
        assertThatThrownBy(() -> workoutLogRepository.findById(savedWorkoutLogKey))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    @DisplayName("특정 회원의 특정 날짜에 존재하는 모든 운동 기록 삭제")
    void deleteByMemberAndDate() {
        // given
        WorkoutLogSaveDTO workoutLogSave = WorkoutLogSaveDTO.builder()
                .memberEmail("test@test.com")
                .createdDate(LocalDate.now())
                .workoutIds(List.of(1, 2))
                .build();
        workoutLogRepository.save(workoutLogSave);

        // when
        workoutLogRepository.deleteByMemberAndDate("test@test.com", LocalDate.now());

        // then
        List<WorkoutLogResDTO> findAllByMemberAndDate =
                workoutLogRepository.findByMemberAndDate("test@test.com", LocalDate.now());
        assertThat(findAllByMemberAndDate).isEmpty();
    }

    @Test
    @DisplayName("이미 추가한 운동 재 추가시 예외 발생")
    void retryAddWorkout() {
        // given
        WorkoutLogSaveDTO workoutLogSave1 = WorkoutLogSaveDTO.builder()
                .memberEmail("test@test.com")
                .createdDate(LocalDate.now())
                .workoutIds(List.of(1))
                .build();
        workoutLogRepository.save(workoutLogSave1);

        // when
        WorkoutLogSaveDTO workoutLogSave2 = WorkoutLogSaveDTO.builder()
                .memberEmail("test@test.com")
                .createdDate(LocalDate.now())
                .workoutIds(List.of(1))
                .build();

        // then
        assertThatThrownBy(() -> workoutLogRepository.save(workoutLogSave2))
                .isInstanceOf(DuplicateKeyException.class);
    }
}