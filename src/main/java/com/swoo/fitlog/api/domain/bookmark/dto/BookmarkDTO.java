package com.swoo.fitlog.api.domain.bookmark.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkDTO {
    private String email;
    private long itemId;
}
