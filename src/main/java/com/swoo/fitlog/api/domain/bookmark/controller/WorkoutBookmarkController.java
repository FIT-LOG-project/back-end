package com.swoo.fitlog.api.domain.bookmark.controller;

import com.swoo.fitlog.api.domain.bookmark.dto.BookmarkDTO;
import com.swoo.fitlog.api.domain.bookmark.service.WorkoutBookmarkService;
import com.swoo.fitlog.http.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WorkoutBookmarkController {

    private final WorkoutBookmarkService workoutBookmarkService;

    @PostMapping("/api/v1/workouts/{email}/bookmarks/{workoutId}")
    public ResponseEntity<RestResponse<Object>> addBookmark(@PathVariable String email,
                                                            @PathVariable long workoutId) {

        BookmarkDTO bookmark = BookmarkDTO.builder().email(email).itemId(workoutId).build();
        workoutBookmarkService.save(bookmark);

        RestResponse<Object> restResponse = RestResponse.ok("운동 북마크 추가 성공", null);
        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }

    @DeleteMapping("/api/v1/workouts/{email}/bookmarks/{workoutId}")
    public ResponseEntity<RestResponse<Object>> deleteBookmark(@PathVariable String email,
                                                               @PathVariable long workoutId) {

        BookmarkDTO bookmark = BookmarkDTO.builder().email(email).itemId(workoutId).build();
        workoutBookmarkService.delete(bookmark);

        RestResponse<Object> restResponse = RestResponse.ok("운동 북마크 해제 성공", null);
        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }
}
