package com.swoo.fitlog.api.domain.workout.log.dto;

import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutLogSaveDTO {

    private LocalDate createdDate;

    @Email
    private String memberEmail;

    private List<Integer> workoutIds;
}