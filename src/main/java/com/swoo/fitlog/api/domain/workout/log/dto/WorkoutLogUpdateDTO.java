package com.swoo.fitlog.api.domain.workout.log.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutLogUpdateDTO {
    private String note;
}
