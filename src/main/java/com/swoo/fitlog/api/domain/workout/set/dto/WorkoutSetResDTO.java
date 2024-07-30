package com.swoo.fitlog.api.domain.workout.set.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSetResDTO {
    private long id;
    private int numberOfPerform;
    private int weight;
}