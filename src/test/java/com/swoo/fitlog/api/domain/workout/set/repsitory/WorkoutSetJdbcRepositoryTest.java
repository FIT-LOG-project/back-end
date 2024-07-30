package com.swoo.fitlog.api.domain.workout.set.repsitory;

import com.swoo.fitlog.api.domain.user.MemberStatus;
import com.swoo.fitlog.api.domain.user.entity.Member;
import com.swoo.fitlog.api.domain.user.repository.MemberRepository;
import com.swoo.fitlog.api.domain.workout.log.dto.WorkoutLogSaveDTO;
import com.swoo.fitlog.api.domain.workout.log.repository.WorkoutLogRepository;
import com.swoo.fitlog.api.domain.workout.set.dto.WorkoutSetResDTO;
import com.swoo.fitlog.api.domain.workout.set.dto.WorkoutSetSaveDTO;
import com.swoo.fitlog.api.domain.workout.set.dto.WorkoutSetUpdateDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class WorkoutSetJdbcRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    WorkoutLogRepository workoutLogRepository;

    @Autowired
    WorkoutSetRepository workoutSetRepository;

    Member savedMember;
    long savedWorkoutLogKey;

    @BeforeEach
    void beforeEach() {
        Member member = Member.builder()
                .email("test@test.com")
                .password("123456")
                .status(MemberStatus.NEW)
                .build();
        savedMember = memberRepository.save(member);

        WorkoutLogSaveDTO workoutLogSave = WorkoutLogSaveDTO.builder()
                .memberEmail(member.getEmail())
                .workoutIds(List.of(1))
                .createdDate(LocalDate.now())
                .build();

        List<Long> keys = workoutLogRepository.save(workoutLogSave);
        savedWorkoutLogKey = keys.get(0);

    }

    @AfterEach
    void afterEach() {
        memberRepository.deleteById(savedMember.getId());
        workoutLogRepository.deleteById(savedWorkoutLogKey);
        workoutSetRepository.deleteAllByWorkoutLogId(savedWorkoutLogKey);
    }

    @Test
    @DisplayName("세트 추가 성공 & 무게와 횟수 디폴트 값 입력 성공")
    void save() {
        // given

        // when
        WorkoutSetSaveDTO workoutSetSave = WorkoutSetSaveDTO.builder()
                .workoutLogId(savedWorkoutLogKey)
                .build();
        long savedWorkoutSetKey = workoutSetRepository.save(workoutSetSave);

        WorkoutSetResDTO findWorkoutSetRes = workoutSetRepository.findById(savedWorkoutSetKey);

        // then
        assertThat(findWorkoutSetRes.getId()).isEqualTo(savedWorkoutSetKey);
        assertThat(findWorkoutSetRes.getWeight()).isEqualTo(0);
        assertThat(findWorkoutSetRes.getNumberOfPerform()).isEqualTo(0);
    }

    @Test
    @DisplayName("세트 수정: 1.횟수, 2.무게, 3.횟수 & 무게")
    void update() {
        // given
        WorkoutSetSaveDTO workoutSetSave = WorkoutSetSaveDTO.builder()
                .workoutLogId(savedWorkoutLogKey)
                .build();
        long savedWorkoutSetKey = workoutSetRepository.save(workoutSetSave);

        // when
        // then

        // 1. 횟수만 수정
        WorkoutSetUpdateDTO workoutSetUpdateOnlyNumberOfPerform = WorkoutSetUpdateDTO.builder()
                .numberOfPerform(10)
                .build();
        workoutSetRepository.update(savedWorkoutSetKey, workoutSetUpdateOnlyNumberOfPerform);
        WorkoutSetResDTO findWorkoutSetRes = workoutSetRepository.findById(savedWorkoutSetKey);
        assertThat(findWorkoutSetRes.getNumberOfPerform()).isEqualTo(10);
        assertThat(findWorkoutSetRes.getWeight()).isEqualTo(0);

        // 2. 무게만 수정
        WorkoutSetUpdateDTO workoutSetUpdateOnlyWeight = WorkoutSetUpdateDTO.builder().weight(100).build();
        workoutSetRepository.update(savedWorkoutSetKey, workoutSetUpdateOnlyWeight);
        findWorkoutSetRes = workoutSetRepository.findById(savedWorkoutSetKey);
        assertThat(findWorkoutSetRes.getNumberOfPerform()).isEqualTo(10);
        assertThat(findWorkoutSetRes.getWeight()).isEqualTo(100);

        // 3. 횟수 & 무게 동시 수정
        WorkoutSetUpdateDTO workoutSetUpdateBothPerformAndWeight = WorkoutSetUpdateDTO.builder()
                .numberOfPerform(20)
                .weight(80)
                .build();
        workoutSetRepository.update(savedWorkoutSetKey, workoutSetUpdateBothPerformAndWeight);
        findWorkoutSetRes = workoutSetRepository.findById(savedWorkoutSetKey);
        assertThat(findWorkoutSetRes.getNumberOfPerform()).isEqualTo(20);
        assertThat(findWorkoutSetRes.getWeight()).isEqualTo(80);
    }

    @Test
    @DisplayName("단건 세트 삭제")
    void deleteById() {
        // given
        WorkoutSetSaveDTO workoutSetSave = WorkoutSetSaveDTO.builder()
                .workoutLogId(savedWorkoutLogKey)
                .build();
        long savedWorkoutSetKey = workoutSetRepository.save(workoutSetSave);

        // when
        workoutSetRepository.deleteById(savedWorkoutSetKey);

        // then
        assertThatThrownBy(() -> workoutSetRepository.findById(savedWorkoutSetKey))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    @DisplayName("특정 운동 기록에 저장된 세트를 모두 삭제")
    void deleteAllByWorkoutLogId() {
        // given
        WorkoutSetSaveDTO workoutSetSave = WorkoutSetSaveDTO.builder()
                .workoutLogId(savedWorkoutLogKey)
                .build();
        long savedWorkoutSetKey1 = workoutSetRepository.save(workoutSetSave);

        WorkoutSetSaveDTO workoutSetSave2 = WorkoutSetSaveDTO.builder()
                .workoutLogId(savedWorkoutLogKey)
                .build();
        long savedWorkoutSetKey2 = workoutSetRepository.save(workoutSetSave2);

        // when
        workoutSetRepository.deleteAllByWorkoutLogId(savedWorkoutLogKey);

        // then
        assertThatThrownBy(() -> workoutSetRepository.findById(savedWorkoutSetKey1))
                .isInstanceOf(EmptyResultDataAccessException.class);
        assertThatThrownBy(() -> workoutSetRepository.findById(savedWorkoutSetKey2))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    @DisplayName("primary key 로 설정한 오름차순 정렬 테스트")
    void sameSetNumber() {
        // given
        WorkoutSetSaveDTO workoutSetSave1 = WorkoutSetSaveDTO.builder()
                .workoutLogId(savedWorkoutLogKey)
                .build();
        workoutSetRepository.save(workoutSetSave1);

        // when
        WorkoutSetSaveDTO workoutSetSave2 = WorkoutSetSaveDTO.builder()
                .workoutLogId(savedWorkoutLogKey)
                .build();
        workoutSetRepository.save(workoutSetSave2);

        // then
        List<WorkoutSetResDTO> findWorkoutSets = workoutSetRepository.findAllByWorkoutLogId(savedWorkoutLogKey);
        assertThat(findWorkoutSets).extracting(WorkoutSetResDTO::getId).isSorted();
    }
}