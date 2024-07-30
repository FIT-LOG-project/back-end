package com.swoo.fitlog.api.domain.workout.log.controller;

import com.swoo.fitlog.api.domain.workout.log.dto.WorkoutLogResDTO;
import com.swoo.fitlog.api.domain.workout.log.dto.WorkoutLogSaveDTO;
import com.swoo.fitlog.api.domain.workout.log.dto.WorkoutLogUpdateDTO;
import com.swoo.fitlog.api.domain.workout.log.service.WorkoutLogService;
import com.swoo.fitlog.http.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class WorkoutLogController {

    private final WorkoutLogService workoutLogService;

    /*
    * 특정 사용자가 특정 날짜에 특정 운동 추가
    * */
    @PostMapping("/api/v1/workouts/logs")
    public ResponseEntity<RestResponse<List<WorkoutLogResDTO>>> createWorkoutLog(
            @RequestBody WorkoutLogSaveDTO workoutLogs) {

        List<WorkoutLogResDTO> savedWorkoutLogs = workoutLogService.save(workoutLogs);

        RestResponse<List<WorkoutLogResDTO>> restResponse = RestResponse.ok("운동 추가 성공", savedWorkoutLogs);
        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }

    /*
    * 특정 사용자의 특정 날짜에 저장된 운동을 조회한다.
    * */
    @GetMapping("/api/v1/workouts/logs")
    public ResponseEntity<RestResponse<List<WorkoutLogResDTO>>> getWorkoutLogs(@RequestParam String email,
                                                                               @RequestParam LocalDate date) {

        List<WorkoutLogResDTO> findWorkoutLogs = workoutLogService.findAll(email, date);

        RestResponse<List<WorkoutLogResDTO>> restResponse =
                RestResponse.ok("운동 기록 조회 성공", findWorkoutLogs);

        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }

    /*
    * 메모 수정
    * */
    @PatchMapping("/api/v1/workouts/logs/{id}")
    public ResponseEntity<RestResponse<WorkoutLogUpdateDTO>> updateWorkoutLog(@PathVariable("id") Long id,
                                                                              @RequestBody
                                                                              WorkoutLogUpdateDTO workoutLogUpdate) {

        WorkoutLogUpdateDTO updatedWorkoutLog = workoutLogService.update(id, workoutLogUpdate);

        RestResponse<WorkoutLogUpdateDTO> restResponse = RestResponse.ok("메모 수정 성공", updatedWorkoutLog);
        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }

    /*
    * 운동 기록의 정보를 조회한다. - 필요 없음 삭제 필요? 이전 내역들을 불러올떄 사용할 수도?
    * */
    @GetMapping("/api/v1/workouts/logs/{id}")
    public ResponseEntity<RestResponse<WorkoutLogResDTO>> getWorkoutLog(@PathVariable Long id) {

        WorkoutLogResDTO findWorkoutLog = workoutLogService.findById(id);

        RestResponse<WorkoutLogResDTO> restResponse = RestResponse.ok("운동 기록 조회 성공", findWorkoutLog);
        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }

    @DeleteMapping("/api/v1/workouts/logs/{id}")
    public ResponseEntity<RestResponse<Object>> deleteWorkoutLog(@PathVariable Long id) {

        workoutLogService.delete(id);

        RestResponse<Object> restResponse = RestResponse.ok("운동 기록 삭제 성공", null);
        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }

    @DeleteMapping("/api/v1/workouts/logs")
    public ResponseEntity<RestResponse<Object>> deleteWorkoutLogs(@RequestParam String email,
                                                                @RequestParam LocalDate date) {

        workoutLogService.deleteAllByMemberAndDate(email, date);

        RestResponse<Object> restResponse = RestResponse.ok("해당 날짜의 운동 기록 모두 삭제 성공", null);
        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }
}
