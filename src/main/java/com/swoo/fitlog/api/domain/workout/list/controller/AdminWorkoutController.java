package com.swoo.fitlog.api.domain.workout.list.controller;

import com.swoo.fitlog.api.domain.workout.list.dto.WorkoutBasicDTO;
import com.swoo.fitlog.api.domain.workout.list.dto.WorkoutDTO;
import com.swoo.fitlog.api.domain.workout.list.dto.WorkoutSearchDTO;
import com.swoo.fitlog.api.domain.workout.list.service.WorkoutService;
import com.swoo.fitlog.http.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminWorkoutController {

    private final WorkoutService workoutService;

    @GetMapping("/api/v1/admin/workouts")
    public ResponseEntity<RestResponse<List<WorkoutBasicDTO>>> getWorkoutForAdmin(
            @RequestParam(value = "type", required = false) Byte bodyPart) {

        List<WorkoutBasicDTO> findWorkouts;
        if (bodyPart == null) {
            findWorkouts = workoutService.findAll();
        } else {
            WorkoutSearchDTO workoutSearch = WorkoutSearchDTO.builder().bodyPartId(bodyPart).build();
            findWorkouts = workoutService.findByBodyPart(workoutSearch);
        }

        RestResponse<List<WorkoutBasicDTO>> restResponse = RestResponse.ok("운동 조회 성공", findWorkouts);

        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }

    @GetMapping("/api/v1/admin/workouts/{workoutId}")
    public ResponseEntity<RestResponse<WorkoutBasicDTO>> getWorkoutForAdmin(@PathVariable int workoutId) {
        WorkoutSearchDTO workoutSearch = WorkoutSearchDTO.builder().id(workoutId).build();

        WorkoutBasicDTO findWorkout = workoutService.findById(workoutSearch);

        RestResponse<WorkoutBasicDTO> restResponse = RestResponse.ok("단일 운동 조회 성공", findWorkout);
        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }

    @PostMapping("/api/v1/admin/workouts")
    public ResponseEntity<RestResponse<Object>> createWorkout(@RequestBody WorkoutDTO createWorkout) {
        workoutService.save(createWorkout);

        RestResponse<Object> restResponse = RestResponse.ok("운동 종목 추가 완료", null);
        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }

    @PatchMapping("/api/v1/admin/workouts/{workoutId}")
    public ResponseEntity<RestResponse<Object>> updateWorkout(@PathVariable int workoutId,
                                                              @RequestBody WorkoutDTO updateWorkout) {
        if (updateWorkout.getBodyPartId() == null) {
            workoutService.updateName(workoutId, updateWorkout);
        } else {
            workoutService.updateBodyPart(workoutId, updateWorkout);
        }

        RestResponse<Object> restResponse = RestResponse.ok("운동 이름 수정 완료", null);
        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }

    @DeleteMapping("/api/v1/admin/workouts/{workoutId}")
    public ResponseEntity<RestResponse<Object>> deleteWorkout(@PathVariable int workoutId) {
        workoutService.delete(workoutId);

        RestResponse<Object> restResponse = RestResponse.ok("운동 종목 삭제 완료", null);
        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }
}
