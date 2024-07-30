package com.swoo.fitlog.api.domain.workout.set.repsitory;

import com.swoo.fitlog.api.domain.workout.set.dto.WorkoutSetResDTO;
import com.swoo.fitlog.api.domain.workout.set.dto.WorkoutSetSaveDTO;
import com.swoo.fitlog.api.domain.workout.set.dto.WorkoutSetUpdateDTO;

import java.util.List;

public interface WorkoutSetRepository {

    long save(WorkoutSetSaveDTO workoutSet);

    void update(long id, WorkoutSetUpdateDTO workoutSet);

    WorkoutSetResDTO findById(long id);

    List<WorkoutSetResDTO> findAllByWorkoutLogId(long workoutLogId);

    void deleteById(long id);

    void deleteAllByWorkoutLogId(long workoutLogId);
}
