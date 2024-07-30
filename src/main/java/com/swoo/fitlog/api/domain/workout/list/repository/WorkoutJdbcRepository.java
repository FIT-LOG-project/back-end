package com.swoo.fitlog.api.domain.workout.list.repository;

import com.swoo.fitlog.api.domain.workout.list.dto.WorkoutBasicDTO;
import com.swoo.fitlog.api.domain.workout.list.dto.WorkoutBasicWithBookmarkDTO;
import com.swoo.fitlog.api.domain.workout.list.dto.WorkoutDTO;
import com.swoo.fitlog.api.domain.workout.list.dto.WorkoutSearchDTO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Repository
public class WorkoutJdbcRepository implements WorkoutRepository{

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public WorkoutJdbcRepository(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<WorkoutBasicDTO> findAll() {
        String sql = "SELECT workouts.id, workouts.name, body_parts.name AS bodyPartName FROM workouts " +
                "INNER JOIN body_parts ON workouts.body_parts_id = body_parts.id";
        return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(WorkoutBasicDTO.class));
    }

    @Override
    public List<WorkoutBasicDTO> findByBodyPart(WorkoutSearchDTO workoutSearch) {
        String sql = "SELECT workouts.id, workouts.name, body_parts.name AS bodyPartName FROM workouts " +
                "INNER JOIN body_parts ON workouts.body_parts_id = body_parts.id " +
                "WHERE workouts.body_parts_id = :bodyPartId";

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("bodyPartId", workoutSearch.getBodyPartId());

        return jdbcTemplate.query(sql, param, BeanPropertyRowMapper.newInstance(WorkoutBasicDTO.class));
    }

    @Override
    public List<WorkoutBasicWithBookmarkDTO> findAllByMember(WorkoutSearchDTO workoutSearch) {
        String sql = """
                SELECT workouts.id, workouts.name, body_parts.name AS bodyPartName,
                CASE
                WHEN workout_bookmarks.id IS NOT NULL THEN TRUE
                ELSE FALSE END AS bookmarked, workout_bookmarks.members_email FROM workouts
                JOIN body_parts ON workouts.body_parts_id = body_parts.id
                LEFT JOIN workout_bookmarks ON workouts.id = workout_bookmarks.workouts_id
                AND workout_bookmarks.members_email = :email;
                """;

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("email", workoutSearch.getEmail());

        return jdbcTemplate.query(sql, param, BeanPropertyRowMapper.newInstance(WorkoutBasicWithBookmarkDTO.class));
    }

    @Override
    public List<WorkoutBasicWithBookmarkDTO> findByBodyPartAndMember(WorkoutSearchDTO workoutSearch) {
        String sql = """
                SELECT workouts.id, workouts.name, body_parts.name AS bodyPartName,
                CASE 
                WHEN workout_bookmarks.id IS NOT NULL THEN TRUE 
                ELSE FALSE END AS bookmarked FROM workouts 
                JOIN body_parts ON workouts.body_parts_id = body_parts.id 
                LEFT JOIN workout_bookmarks ON workouts.id = workout_bookmarks.workouts_id 
                AND workout_bookmarks.members_email = :email 
                WHERE workouts.body_parts_id = :bodyPartId;
                """;

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("email", workoutSearch.getEmail())
                .addValue("bodyPartId", workoutSearch.getBodyPartId());

        return jdbcTemplate.query(sql, param, BeanPropertyRowMapper.newInstance(WorkoutBasicWithBookmarkDTO.class));
    }

    @Override
    public WorkoutBasicDTO findById(WorkoutSearchDTO workoutSearch) {
        String sql = """
                SELECT workouts.id, workouts.name, body_parts.name AS bodyPartName FROM workouts
                JOIN body_parts ON workouts.body_parts_id = body_parts.id WHERE workouts.id = :workoutId
                """;

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("workoutId", workoutSearch.getId());
        return jdbcTemplate.queryForObject(sql, param, BeanPropertyRowMapper.newInstance(WorkoutBasicDTO.class));
    }

    @Override
    public List<WorkoutBasicWithBookmarkDTO> findAllForBookmark(WorkoutSearchDTO workoutSearch) {
        String sql = """
                SELECT workouts.id, workouts.name, body_parts.name AS bodyPartName,
                CASE 
                WHEN workout_bookmarks.id IS NOT NULL THEN TRUE 
                ELSE FALSE END AS bookmarked FROM workouts 
                JOIN body_parts ON workouts.body_parts_id = body_parts.id 
                LEFT JOIN workout_bookmarks ON workouts.id = workout_bookmarks.workouts_id 
                AND workout_bookmarks.members_email = :email 
                WHERE workout_bookmarks.id IS NOT NULL;
                """;

        MapSqlParameterSource param =
                new MapSqlParameterSource().addValue("email", workoutSearch.getEmail());

        return jdbcTemplate.query(sql, param, BeanPropertyRowMapper.newInstance(WorkoutBasicWithBookmarkDTO.class));
    }

    @Override
    public int save(WorkoutDTO saveWorkout) {
        String sql = "INSERT INTO workouts(name, body_parts_id) VALUES(:name, :bodyPartId)";

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("name", saveWorkout.getName())
                .addValue("bodyPartId", saveWorkout.getBodyPartId());

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(sql, param, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }

    @Override
    public void updateName(int workoutId, WorkoutDTO workout) {
        String sql = "UPDATE workouts SET name = :name WHERE id = :id";

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("id", workoutId)
                .addValue("name", workout.getName());

        jdbcTemplate.update(sql, param);
    }

    @Override
    public void updateBodyPart(int workoutId, WorkoutDTO workout) {
        String sql = "UPDATE workouts SET body_parts_id = :bodyPartId WHERE id = :id";

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("id", workoutId)
                .addValue("bodyPartId", workout.getBodyPartId());

        jdbcTemplate.update(sql, param);
    }

    @Override
    public void deleteById(int workoutId) {
        String sql = "DELETE FROM workouts WHERE id = :workoutId";

        MapSqlParameterSource param = new MapSqlParameterSource().addValue("workoutId", workoutId);

        jdbcTemplate.update(sql, param);
    }
}
