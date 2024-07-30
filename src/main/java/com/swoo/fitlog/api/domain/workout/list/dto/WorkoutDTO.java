package com.swoo.fitlog.api.domain.workout.list.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutDTO {
    private String name;
    private Byte bodyPartId;
}