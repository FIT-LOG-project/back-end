package com.swoo.fitlog.api.domain.bookmark.service;

import com.swoo.fitlog.api.domain.bookmark.dto.BookmarkDTO;
import com.swoo.fitlog.api.domain.bookmark.repository.WorkoutBookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkoutBookmarkServiceImpl implements WorkoutBookmarkService {

    private final WorkoutBookmarkRepository workoutBookmarkRepository;

    @Override
    public void save(BookmarkDTO bookmark) {
        workoutBookmarkRepository.save(bookmark);
    }

    @Override
    public void delete(BookmarkDTO bookmark) {
        workoutBookmarkRepository.delete(bookmark);
    }
}
