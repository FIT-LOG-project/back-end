package com.swoo.fitlog.api.domain.workout.set;

import com.swoo.fitlog.api.domain.workout.set.dto.WorkoutSetResDTO;
import com.swoo.fitlog.api.domain.workout.set.dto.WorkoutSetSaveDTO;
import com.swoo.fitlog.api.domain.workout.set.dto.WorkoutSetUpdateDTO;
import com.swoo.fitlog.api.domain.workout.set.repsitory.WorkoutSetRepository;
import com.swoo.fitlog.http.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class WorkoutSetController {

    private final WorkoutSetRepository workoutSetRepository;

    /*
    * 세트 추가
    * */
    @PostMapping("/api/v1/workouts/logs/sets")
    public ResponseEntity<RestResponse<WorkoutSetResDTO>> sets(@RequestBody WorkoutSetSaveDTO workoutSet) {

        long saveWorkoutSetId = workoutSetRepository.save(workoutSet);
        WorkoutSetResDTO findWorkoutSet = workoutSetRepository.findById(saveWorkoutSetId);

        RestResponse<WorkoutSetResDTO> restResponse = RestResponse.ok("세트 추가 성공", findWorkoutSet);
        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }

    /*
    * 무게 또는 수행 횟수 수정
    * */
    @PatchMapping("/api/v1/workouts/logs/sets/{id}")
    public ResponseEntity<RestResponse<WorkoutSetResDTO>> update(@PathVariable long id,
                                                                 @RequestBody WorkoutSetUpdateDTO updateWorkoutSet) {
        workoutSetRepository.update(id, updateWorkoutSet);
        WorkoutSetResDTO findWorkoutSet = workoutSetRepository.findById(id);

        RestResponse<WorkoutSetResDTO> restResponse = RestResponse.ok("세트 수정 성공", findWorkoutSet);
        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }

    /*
    * 특정 세트 하나를 삭제
    * */
    @DeleteMapping("/api/v1/workouts/logs/sets/{id}")
    public ResponseEntity<RestResponse<Object>> deleteToOnlyOne(@PathVariable long id) {
        workoutSetRepository.deleteById(id);

        RestResponse<Object> restResponse = RestResponse.ok("세트 삭제 성공", null);
        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }

    /*
    * 특정 운동 기록의 모든 세트를 삭제
    * */
    @DeleteMapping("/api/v1/workouts/logs/{workoutLogId}/sets")
    public ResponseEntity<RestResponse<Object>> deleteAllByWorkoutLog(@PathVariable long workoutLogId) {

        workoutSetRepository.deleteAllByWorkoutLogId(workoutLogId);

        RestResponse<Object> restResponse = RestResponse.ok("세트 모두 삭제 성공", null);
        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }
}
