package com.swoo.fitlog.api.domain.workout.list.entity;

import lombok.*;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Workout {
    private int id;
    private String name;
    private byte bodyPartId;
}
