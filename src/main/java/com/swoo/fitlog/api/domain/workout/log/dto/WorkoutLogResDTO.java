package com.swoo.fitlog.api.domain.workout.log.dto;

import com.swoo.fitlog.api.domain.workout.set.dto.WorkoutSetResDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutLogResDTO {
    private long id;
    private String name;
    private String note;
    private List<WorkoutSetResDTO> workoutSets;
    private int maxWeight;
    private int oneRepMax;
}