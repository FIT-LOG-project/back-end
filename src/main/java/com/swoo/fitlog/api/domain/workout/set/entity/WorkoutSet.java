package com.swoo.fitlog.api.domain.workout.set.entity;

import lombok.*;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSet {
    private long id;
    private int numberOfSet;
    private int numberOfPerform;
    private int weight;
    private long workoutLogId;
}
