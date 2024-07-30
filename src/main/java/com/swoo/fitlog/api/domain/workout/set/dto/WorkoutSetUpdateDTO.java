package com.swoo.fitlog.api.domain.workout.set.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSetUpdateDTO {
    private Integer numberOfPerform;
    private Integer weight;
}
