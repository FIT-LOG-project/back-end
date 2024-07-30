package com.swoo.fitlog.api.domain.bookmark.service;

import com.swoo.fitlog.api.domain.bookmark.dto.BookmarkDTO;

public interface BookmarkService {

    void save(BookmarkDTO bookmark);

    void delete(BookmarkDTO bookmark);
}
