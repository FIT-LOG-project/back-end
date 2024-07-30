package com.swoo.fitlog.api.domain.workout.log.entity;

import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutLog {
    private long id;
    private String note;
    private LocalDate createdDate;
    private long memberId;
    private int workoutId;
}