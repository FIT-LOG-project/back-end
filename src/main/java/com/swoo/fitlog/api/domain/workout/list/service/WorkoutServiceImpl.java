package com.swoo.fitlog.api.domain.workout.list.service;

import com.swoo.fitlog.api.domain.workout.list.dto.WorkoutDTO;
import com.swoo.fitlog.api.domain.workout.list.dto.WorkoutSearchDTO;
import com.swoo.fitlog.api.domain.workout.list.dto.WorkoutBasicDTO;
import com.swoo.fitlog.api.domain.workout.list.dto.WorkoutBasicWithBookmarkDTO;
import com.swoo.fitlog.api.domain.workout.list.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkoutServiceImpl implements WorkoutService {

    private final WorkoutRepository workoutRepository;

    @Override
    public List<WorkoutBasicDTO> findAll() {
        return workoutRepository.findAll();
    }

    @Override
    public WorkoutBasicDTO findById(WorkoutSearchDTO workoutSearch) {
        return workoutRepository.findById(workoutSearch);
    }

    @Override
    public List<WorkoutBasicDTO> findByBodyPart(WorkoutSearchDTO workoutSearch) {
        return workoutRepository.findByBodyPart(workoutSearch);
    }

    @Override
    public List<WorkoutBasicWithBookmarkDTO> findAllByMember(WorkoutSearchDTO workoutSearch) {
        return workoutRepository.findAllByMember(workoutSearch);
    }

    @Override
    public List<WorkoutBasicWithBookmarkDTO> findByBodyPartAndMember(WorkoutSearchDTO workoutSearch) {
        return workoutRepository.findByBodyPartAndMember(workoutSearch);
    }

    @Override
    public List<WorkoutBasicWithBookmarkDTO> findAllForBookmark(WorkoutSearchDTO workoutSearch) {
        return workoutRepository.findAllForBookmark(workoutSearch);
    }

    @Override
    public void save(WorkoutDTO workout) {
        workoutRepository.save(workout);
    }

    @Override
    public void updateName(int workoutId, WorkoutDTO workout) {
        workoutRepository.updateName(workoutId, workout);
    }

    @Override
    public void updateBodyPart(int workoutId, WorkoutDTO workout) {
        workoutRepository.updateBodyPart(workoutId, workout);
    }

    @Override
    public void delete(int workoutId) {
        workoutRepository.deleteById(workoutId);
    }
}
