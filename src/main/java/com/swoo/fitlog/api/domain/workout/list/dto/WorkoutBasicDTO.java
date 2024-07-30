package com.swoo.fitlog.api.domain.workout.list.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutBasicDTO {
    private int id;
    private String name;
    private String bodyPartName;
}

