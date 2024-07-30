package com.swoo.fitlog.api.domain.bookmark.repository;

import com.swoo.fitlog.api.domain.bookmark.dto.BookmarkDTO;

public interface BookmarkRepository {

    void save(BookmarkDTO bookmark);

    void delete(BookmarkDTO bookmark);
}
