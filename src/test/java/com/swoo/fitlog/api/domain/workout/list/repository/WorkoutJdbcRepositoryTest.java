package com.swoo.fitlog.api.domain.workout.list.repository;

import com.swoo.fitlog.api.domain.bookmark.dto.BookmarkDTO;
import com.swoo.fitlog.api.domain.bookmark.repository.WorkoutBookmarkRepository;
import com.swoo.fitlog.api.domain.user.entity.Member;
import com.swoo.fitlog.api.domain.user.repository.MemberRepository;
import com.swoo.fitlog.api.domain.workout.list.dto.WorkoutBasicDTO;
import com.swoo.fitlog.api.domain.workout.list.dto.WorkoutBasicWithBookmarkDTO;
import com.swoo.fitlog.api.domain.workout.list.dto.WorkoutDTO;
import com.swoo.fitlog.api.domain.workout.list.dto.WorkoutSearchDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class WorkoutJdbcRepositoryTest {

    @Autowired
    WorkoutRepository workoutRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    WorkoutBookmarkRepository workoutBookmarkRepository;

    @Test
    void findAll() {
        // given
        // when
        List<WorkoutBasicDTO> findWorkouts = workoutRepository.findAll();

        // then
        assertThat(findWorkouts.size()).isGreaterThan(0);
    }

    @Test
    void findByBodyPart() {
        // given
        WorkoutSearchDTO workoutSearch = WorkoutSearchDTO.builder()
                .bodyPartId((byte) 1).build();

        List<WorkoutBasicDTO> findWorkouts = workoutRepository.findByBodyPart(workoutSearch);
        for (WorkoutBasicDTO workoutDTO : findWorkouts) {
            assertThat(workoutDTO.getBodyPartName()).isEqualTo("가슴");
        }
    }

    @Test
    void findAllByMember() {
        // given
        Member member = Member.builder()
                .email("test1@test.com")
                .password("123456")
                .build();
        memberRepository.save(member);

        WorkoutSearchDTO workoutSearch = WorkoutSearchDTO.builder()
                .email(member.getEmail())
                .build();

        // when
        List<WorkoutBasicWithBookmarkDTO> findWorkouts = workoutRepository.findAllByMember(workoutSearch);

        // then
        assertThat(findWorkouts.size()).isGreaterThan(0);
    }

    @Test
    void findById() {
        // given
        WorkoutSearchDTO workoutSearch = WorkoutSearchDTO.builder().id((byte) 1).build();

        // when
        WorkoutBasicDTO findWorkouts = workoutRepository.findById(workoutSearch);

        // then
        assertThat(findWorkouts.getName()).isEqualTo("스미스머신 벤치프레스");
    }

    @Test
    void findByBodyPartAndMember() {
        // given
        Member member = Member.builder()
                .email("test2@test.com")
                .password("123456")
                .build();

        memberRepository.save(member);

        WorkoutSearchDTO workoutSearch = WorkoutSearchDTO.builder()
                .email(member.getEmail())
                .bodyPartId((byte) 1).build();

        // when
        List<WorkoutBasicWithBookmarkDTO> findWorkouts =
                workoutRepository.findByBodyPartAndMember(workoutSearch);

        // then
        assertThat(findWorkouts.size()).isGreaterThan(0);
    }

    @Test
    void findByOnlyBookmark() {
        // given
        Member member = Member.builder()
                .email("test3@test.com")
                .password("123456")
                .build();

        memberRepository.save(member);

        WorkoutSearchDTO workoutSearch = WorkoutSearchDTO.builder()
                .email(member.getEmail())
                .build();

        BookmarkDTO bookmark = BookmarkDTO.builder()
                .email(member.getEmail())
                .itemId(1)
                .build();
        workoutBookmarkRepository.save(bookmark);

        // when
        List<WorkoutBasicWithBookmarkDTO> findWorkout = workoutRepository.findAllForBookmark(workoutSearch);

        // then
        assertThat(findWorkout.size()).isEqualTo(1);
    }

    @Test
    void save() {
        // given
        WorkoutDTO workout = WorkoutDTO.builder().name("새로운 운동").bodyPartId((byte) 1).build();

        // when
        int workoutId = workoutRepository.save(workout);

        // then
        WorkoutSearchDTO workoutSearch = WorkoutSearchDTO.builder().id(workoutId).build();

        WorkoutBasicDTO findWorkout = workoutRepository.findById(workoutSearch);
        assertThat(workout.getName()).isEqualTo(findWorkout.getName());
    }

    @Test
    void updateName() {
        // given
        WorkoutDTO updateWorkout = WorkoutDTO.builder().name("수정된 운동").build();

        // when
        workoutRepository.updateName(2, updateWorkout);

        // then
        WorkoutSearchDTO workoutSearch = WorkoutSearchDTO.builder().id((byte) 2).build();
        WorkoutBasicDTO findWorkout = workoutRepository.findById(workoutSearch);

        assertThat(findWorkout.getName()).isEqualTo(updateWorkout.getName());
    }

    @Test
    void updateBodyPart() {
        // given
        WorkoutDTO updateWorkout = WorkoutDTO.builder().bodyPartId((byte) 2).build();

        // when
        workoutRepository.updateBodyPart(3, updateWorkout);

        // then
        WorkoutSearchDTO workoutSearch = WorkoutSearchDTO.builder().id((byte) 3).build();
        WorkoutBasicDTO findWorkout = workoutRepository.findById(workoutSearch);
        assertThat(findWorkout.getBodyPartName()).isEqualTo("등");
    }
}