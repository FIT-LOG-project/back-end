package com.swoo.fitlog.api.domain.workout.list.controller;

import com.swoo.fitlog.api.domain.workout.list.dto.WorkoutBasicWithBookmarkDTO;
import com.swoo.fitlog.api.domain.workout.list.dto.WorkoutSearchDTO;
import com.swoo.fitlog.api.domain.workout.list.service.WorkoutServiceImpl;
import com.swoo.fitlog.http.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutServiceImpl workoutService;

    @GetMapping("/api/v1/workouts")
    public ResponseEntity<RestResponse<List<WorkoutBasicWithBookmarkDTO>>> getWorkouts(
            @RequestParam(required = false) String email,
            @RequestParam(value = "type", required = false) Byte bodyPart,
            @RequestParam(value = "bookmarked", required = false) boolean isBookmark) {

        List<WorkoutBasicWithBookmarkDTO> findWorkout;
        WorkoutSearchDTO workoutSearch = WorkoutSearchDTO.builder()
                .email(email)
                .bodyPartId(bodyPart)
                .isBookmark(isBookmark)
                .build();

        if(isBookmark) {
            findWorkout = workoutService.findAllForBookmark(workoutSearch);
        } else if (bodyPart == null) {
            findWorkout = workoutService.findAllByMember(workoutSearch);
        }  else {
            findWorkout = workoutService.findByBodyPartAndMember(workoutSearch);
        }

        RestResponse<List<WorkoutBasicWithBookmarkDTO>> restResponse =
                RestResponse.ok("운동 조회 성공", findWorkout);

        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }
}
