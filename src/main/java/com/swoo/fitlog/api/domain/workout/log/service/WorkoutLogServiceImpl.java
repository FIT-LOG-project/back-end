package com.swoo.fitlog.api.domain.workout.log.service;

import com.swoo.fitlog.api.domain.workout.log.dto.WorkoutLogResDTO;
import com.swoo.fitlog.api.domain.workout.log.dto.WorkoutLogSaveDTO;
import com.swoo.fitlog.api.domain.workout.log.dto.WorkoutLogUpdateDTO;
import com.swoo.fitlog.api.domain.workout.log.repository.WorkoutLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkoutLogServiceImpl implements WorkoutLogService{

    private final WorkoutLogRepository workoutLogRepository;

    @Override
    public List<WorkoutLogResDTO> save(WorkoutLogSaveDTO workoutLogSaveDTO) {

        String memberEmail = workoutLogSaveDTO.getMemberEmail();
        LocalDate createdDate = workoutLogSaveDTO.getCreatedDate();

        workoutLogRepository.save(workoutLogSaveDTO);

        return workoutLogRepository.findByMemberAndDate(memberEmail, createdDate);
    }

    @Override
    public WorkoutLogResDTO findById(long id) {
        return workoutLogRepository.findById(id);
    }

    @Override
    public List<WorkoutLogResDTO> findAll(String email, LocalDate cratedDate) {
        return workoutLogRepository.findByMemberAndDate(email, cratedDate);
    }

    @Override
    public WorkoutLogUpdateDTO update(long id, WorkoutLogUpdateDTO workoutLogUpdateDTO) {

        workoutLogRepository.update(id, workoutLogUpdateDTO);

        WorkoutLogResDTO findWorkoutLog = workoutLogRepository.findById(id);
        workoutLogUpdateDTO.setNote(findWorkoutLog.getNote());

        return workoutLogUpdateDTO;
    }

    @Override
    public void delete(long id) {
        workoutLogRepository.deleteById(id);
    }

    @Override
    public void deleteAllByMemberAndDate(String email, LocalDate createdDate) {
        workoutLogRepository.deleteByMemberAndDate(email, createdDate);
    }
}
