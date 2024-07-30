package com.swoo.fitlog.api.domain.workout.set.repsitory;

import com.swoo.fitlog.api.domain.workout.set.dto.WorkoutSetResDTO;
import com.swoo.fitlog.api.domain.workout.set.dto.WorkoutSetSaveDTO;
import com.swoo.fitlog.api.domain.workout.set.dto.WorkoutSetUpdateDTO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;

@Repository
public class WorkoutSetJdbcRepository implements WorkoutSetRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public WorkoutSetJdbcRepository(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public long save(WorkoutSetSaveDTO workoutSet) {
        String sql = """
                INSERT INTO workout_sets(workout_logs_id)
                VALUES (:workoutLogId)
                """;

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("workoutLogId", workoutSet.getWorkoutLogId());

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, param, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public void update(long id, WorkoutSetUpdateDTO workoutSet) {
        StringBuilder sb = new StringBuilder("UPDATE workout_sets SET ");
        MapSqlParameterSource param = new MapSqlParameterSource("id", id);

        if (workoutSet.getNumberOfPerform() != null) {
            sb.append("number_of_perform = :numberOfPerform, ");
            param.addValue("numberOfPerform", workoutSet.getNumberOfPerform());
        }

        if (workoutSet.getWeight() != null) {
            sb.append("weight = :weight, ");
            param.addValue("weight", workoutSet.getWeight());
        }

        sb.replace(sb.length() - 2, sb.length(), " ");
        sb.append("WHERE id = :id");

        String sql = sb.toString();

        jdbcTemplate.update(sql, param);
    }

    /*
    * 특정 세트 조회 - 세트 추가 또는 수정 후 사용
    * */
    @Override
    public WorkoutSetResDTO findById(long id) {
        String sql = """
                SELECT id, number_of_perform, weight FROM workout_sets WHERE id=:id
                """;

        MapSqlParameterSource param = new MapSqlParameterSource().addValue("id", id);

        return jdbcTemplate.queryForObject(sql, param, BeanPropertyRowMapper.newInstance(WorkoutSetResDTO.class));
    }

    @Override
    public List<WorkoutSetResDTO> findAllByWorkoutLogId(long workoutLogId) {
        String sql = """
                SELECT id, number_of_perform, weight FROM workout_sets
                WHERE workout_logs_id = :workoutLogId ORDER BY id;
                """;

        MapSqlParameterSource param = new MapSqlParameterSource().addValue("workoutLogId", workoutLogId);
        return jdbcTemplate.query(sql, param, new BeanPropertyRowMapper<>(WorkoutSetResDTO.class));
    }

    /*
    * 단일 세트 삭제
    * */
    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM workout_sets WHERE id = :id";

        MapSqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
        jdbcTemplate.update(sql, param);
    }

    /*
    * 특정 운동 세트 전체 삭제
    * */
    @Override
    public void deleteAllByWorkoutLogId(long workoutLogId) {
        String sql = "DELETE FROM workout_sets WHERE workout_logs_id = :workoutLogId";

        MapSqlParameterSource param = new MapSqlParameterSource().addValue("workoutLogId", workoutLogId);
        jdbcTemplate.update(sql, param);
    }
}
