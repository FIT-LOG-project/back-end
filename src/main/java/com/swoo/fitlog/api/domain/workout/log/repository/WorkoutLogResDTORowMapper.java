package com.swoo.fitlog.api.domain.workout.log.repository;

import com.swoo.fitlog.api.domain.workout.log.dto.WorkoutLogResDTO;
import com.swoo.fitlog.api.domain.workout.set.dto.WorkoutSetResDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class WorkoutLogResDTORowMapper implements RowMapper<WorkoutLogResDTO> {

    private final Map<Long, WorkoutLogResDTO> workoutLogResDTOMap;

    public WorkoutLogResDTORowMapper(Map<Long, WorkoutLogResDTO> workoutLogResDTOMap) {
        this.workoutLogResDTOMap = workoutLogResDTOMap;
    }

    @Override
    public WorkoutLogResDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long workoutLogId = rs.getLong("workout_logs.id");
        WorkoutLogResDTO findWorkoutLogResDTO = workoutLogResDTOMap.get(workoutLogId);

        if (findWorkoutLogResDTO == null) {
            findWorkoutLogResDTO = WorkoutLogResDTO.builder()
                    .id(workoutLogId)
                    .name(rs.getString("name"))
                    .note(rs.getString("note"))
                    .workoutSets(new ArrayList<>())
                    .build();

            workoutLogResDTOMap.put(workoutLogId, findWorkoutLogResDTO);
        }

        long workoutSetId = rs.getLong("workout_sets.id");
        if (workoutSetId != 0) {
            WorkoutSetResDTO workoutSetResDTO = WorkoutSetResDTO.builder()
                    .id(rs.getLong("workout_sets.id"))
                    .numberOfPerform(rs.getInt("number_of_perform"))
                    .weight(rs.getInt("weight"))
                    .build();

            findWorkoutLogResDTO.getWorkoutSets().add(workoutSetResDTO);
        }

        return findWorkoutLogResDTO;
    }
}
