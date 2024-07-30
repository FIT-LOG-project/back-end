package com.swoo.fitlog.api.domain.bookmark.repository;

import com.swoo.fitlog.api.domain.bookmark.dto.BookmarkDTO;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class WorkoutBookmarkJdbcRepository implements WorkoutBookmarkRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public WorkoutBookmarkJdbcRepository(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void save(BookmarkDTO bookmark) {
        String sql = "INSERT INTO workout_bookmarks (members_email, workouts_id) VALUES (:email, :itemId)";

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("email", bookmark.getEmail())
                .addValue("itemId", bookmark.getItemId());

        jdbcTemplate.update(sql, param);
    }

    @Override
    public void delete(BookmarkDTO bookmark) {
        String sql = "DELETE FROM workout_bookmarks WHERE members_email = :email AND workouts_id = :itemId";

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("email", bookmark.getEmail())
                .addValue("itemId", bookmark.getItemId());

        jdbcTemplate.update(sql, param);
    }
}
