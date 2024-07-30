package com.swoo.fitlog.api.domain.workout.list.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutBasicWithBookmarkDTO {
    private int id;
    private String name;
    private String bodyPartName;
    private boolean bookmarked;
}