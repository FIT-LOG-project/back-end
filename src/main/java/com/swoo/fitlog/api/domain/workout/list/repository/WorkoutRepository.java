package com.swoo.fitlog.api.domain.workout.list.repository;

import com.swoo.fitlog.api.domain.workout.list.dto.WorkoutBasicDTO;
import com.swoo.fitlog.api.domain.workout.list.dto.WorkoutDTO;
import com.swoo.fitlog.api.domain.workout.list.dto.WorkoutSearchDTO;
import com.swoo.fitlog.api.domain.workout.list.dto.WorkoutBasicWithBookmarkDTO;

import java.util.List;

public interface WorkoutRepository {

    List<WorkoutBasicDTO> findAll();

    List<WorkoutBasicDTO> findByBodyPart(WorkoutSearchDTO workoutSearch);

    List<WorkoutBasicWithBookmarkDTO> findAllByMember(WorkoutSearchDTO workoutSearch);

    List<WorkoutBasicWithBookmarkDTO> findByBodyPartAndMember(WorkoutSearchDTO workoutSearch);

    List<WorkoutBasicWithBookmarkDTO> findAllForBookmark(WorkoutSearchDTO workoutSearch);

    WorkoutBasicDTO findById(WorkoutSearchDTO workoutSearch);

    int save(WorkoutDTO workout);

    void updateName(int workoutId, WorkoutDTO workout);

    void updateBodyPart(int workoutId, WorkoutDTO workout);

    void deleteById(int workoutId);
}
