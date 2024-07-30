package com.swoo.fitlog.api.domain.workout.log.service;

import com.swoo.fitlog.api.domain.workout.log.dto.WorkoutLogResDTO;
import com.swoo.fitlog.api.domain.workout.log.dto.WorkoutLogSaveDTO;
import com.swoo.fitlog.api.domain.workout.log.dto.WorkoutLogUpdateDTO;

import java.time.LocalDate;
import java.util.List;

public interface WorkoutLogService {

    List<WorkoutLogResDTO> save(WorkoutLogSaveDTO workoutLogSaveDTO);

    WorkoutLogResDTO findById(long id);

    List<WorkoutLogResDTO> findAll(String email, LocalDate createdDate);

    WorkoutLogUpdateDTO update(long id, WorkoutLogUpdateDTO workoutLogUpdateDTO);

    void delete(long id);

    void deleteAllByMemberAndDate(String email, LocalDate createdDate);
}
