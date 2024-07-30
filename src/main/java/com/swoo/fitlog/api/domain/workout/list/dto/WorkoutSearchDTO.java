package com.swoo.fitlog.api.domain.workout.list.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSearchDTO {
    private int id;
    private String email;
    private Byte bodyPartId;
    private Boolean isBookmark;
}
