package com.swoo.fitlog.api.domain.workout.log.repository;


import com.swoo.fitlog.api.domain.workout.log.dto.WorkoutLogResDTO;
import com.swoo.fitlog.api.domain.workout.log.dto.WorkoutLogSaveDTO;
import com.swoo.fitlog.api.domain.workout.log.dto.WorkoutLogUpdateDTO;

import java.time.LocalDate;
import java.util.List;

public interface WorkoutLogRepository {

    List<Long> save(WorkoutLogSaveDTO workoutLogs);

    void update(long id, WorkoutLogUpdateDTO workoutLog);

    WorkoutLogResDTO findById(long id);

    List<WorkoutLogResDTO> findByMemberAndDate(String memberEmail, LocalDate createdDate);

    void deleteById(long id);

    void deleteByMemberAndDate(String memberEmail, LocalDate createdDate);
}
