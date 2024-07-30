package com.swoo.fitlog.api.domain.workout.log.repository;

import com.swoo.fitlog.api.domain.workout.log.dto.WorkoutLogResDTO;
import com.swoo.fitlog.api.domain.workout.log.dto.WorkoutLogSaveDTO;
import com.swoo.fitlog.api.domain.workout.log.dto.WorkoutLogUpdateDTO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class WorkoutLogJdbcRepository implements WorkoutLogRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public WorkoutLogJdbcRepository(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<Long> save(WorkoutLogSaveDTO workoutLogs) {
        String sql = """
                INSERT INTO workout_logs(
                    note,
                    created_date,
                    members_email,
                    workouts_id)
                SELECT note, :createdDate, :memberEmail, :workoutId
                from workout_logs
                where members_email = :memberEmail and workouts_id = :workoutId and created_date < :createdDate
                ORDER BY created_date DESC LIMIT 1;
                """;

        String memberEmail = workoutLogs.getMemberEmail();
        LocalDate createdDate = workoutLogs.getCreatedDate();
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int[] result = jdbcTemplate.batchUpdate(
                sql,
                extractBatchForWorkoutLogSave(workoutLogs.getWorkoutIds(), createdDate, memberEmail),
                keyHolder
        );

        saveNoExistPreviousWorkoutLog(workoutLogs, result, keyHolder);

        return extractKeys(keyHolder);
    }

    private void saveNoExistPreviousWorkoutLog(WorkoutLogSaveDTO workoutLogs, int[] result, KeyHolder keyHolder) {
        LocalDate createDate = workoutLogs.getCreatedDate();
        String memberEmail = workoutLogs.getMemberEmail();
        List<Integer> NoExistPreviousData = extractNoExistPreviousWorkoutLog(workoutLogs, result);

        if (!NoExistPreviousData.isEmpty()) {
            String sql = """
                INSERT INTO workout_logs(created_date, members_email, workouts_id)
                VALUES(:createdDate, :memberEmail, :workoutId);
                """;

            jdbcTemplate.batchUpdate(
                    sql,
                    extractBatchForWorkoutLogSave(NoExistPreviousData, createDate, memberEmail),
                    keyHolder
            );
        }
    }

    private List<Long> extractKeys(KeyHolder keyHolder) {
        List<Long> keys = new ArrayList<>();
        List<Map<String, Object>> keyList = keyHolder.getKeyList();
        for (Map<String, Object> keyMap : keyList) {
            keys.add((Long) keyMap.get("ID"));
        }
        return keys;
    }

    /*
    * 이전에 기록한 이력이 없는 경우를 찾는다.
    * */
    private List<Integer> extractNoExistPreviousWorkoutLog(WorkoutLogSaveDTO workoutLogs, int[] result) {
        List<Integer> NoExistPreviousData = new ArrayList<>();
        for (int i = 0; i < result.length; i++) {
            int effect = result[i];
            if (effect == 0) {
                NoExistPreviousData.add(workoutLogs.getWorkoutIds().get(i));
            }
        }
        return NoExistPreviousData;
    }

    private SqlParameterSource[] extractBatchForWorkoutLogSave(List<Integer> workoutIds,
                                                                      LocalDate createDate,
                                                                      String memberEmail) {
        List<Map<String, Object>> batchValues = workoutIds.stream()
                .map(workoutId -> {
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("createdDate", createDate);
                    paramMap.put("memberEmail", memberEmail);
                    paramMap.put("workoutId", workoutId);
                    return paramMap;
                }).toList();

        return SqlParameterSourceUtils.createBatch(batchValues);
    }

    @Override
    public void update(long id, WorkoutLogUpdateDTO workoutLog) {
        String sql = "UPDATE workout_logs SET note =:note WHERE id =:id";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("note", workoutLog.getNote())
                .addValue("id", id);

        jdbcTemplate.update(sql, param);
    }

    /*
    * 사용할 이유가 없음.
    * 운동 추가 목록에서 최근 정보를 조회하기 위해선 id가 아니라 email의 최신 날짜의 운동 id임
    * */
    @Override
    public WorkoutLogResDTO findById(long id) {
        String sql = """
                SELECT workout_logs.id, name, note, workout_sets.id, number_of_perform, weight from workout_logs
                INNER JOIN workouts ON workout_logs.workouts_id = workouts.id
                LEFT JOIN workout_sets ON workout_logs.id = workout_sets.workout_logs_id
                WHERE workout_logs.id = :id;
                """;

        MapSqlParameterSource param = new MapSqlParameterSource("id", id);

        List<WorkoutLogResDTO> workoutLogResDTOList =
                jdbcTemplate.query(sql, param, new WorkoutLogResDTORowMapper(new HashMap<>()));
        try {
            return workoutLogResDTOList.get(0);
        } catch (IndexOutOfBoundsException e) {
            throw new EmptyResultDataAccessException(1);
        }
    }

    /*
    * 특정 사용자가 특정 날짜에 추가한 운동 기록들을 불러온다.
    * */
    @Override
    public List<WorkoutLogResDTO> findByMemberAndDate(String memberEmail, LocalDate createdDate) {
        String sql = """
                SELECT workout_logs.id, name, note, workout_sets.id, number_of_perform, weight from workout_logs
                INNER JOIN workouts ON workout_logs.workouts_id = workouts.id
                LEFT JOIN workout_sets ON workout_logs.id = workout_sets.workout_logs_id
                WHERE workout_logs.members_email = :memberEmail AND workout_logs.created_date = :createdDate
                """;

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("memberEmail", memberEmail)
                .addValue("createdDate", createdDate);

        Map<Long, WorkoutLogResDTO> workoutLogResDTOMap = new HashMap<>();

        jdbcTemplate.query(sql, param, new WorkoutLogResDTORowMapper(workoutLogResDTOMap));

        return new ArrayList<>(workoutLogResDTOMap.values());
    }

    /*
    * 특정 운동 기록을 삭제한다. - 세트의 정보도 같이 삭제된다.
    * */
    @Override
    public void deleteById(long id) {
        String sql = """
                DELETE FROM workout_logs WHERE id = :id;
                """;

        MapSqlParameterSource param = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, param);
    }

    /*
    * 특정 사용자가 특정 날짜에 추가한 모든 운동 기록을 삭제한다. - 연관된 세트의 정보도 모두 삭제된다.
    * */
    @Override
    public void deleteByMemberAndDate(String memberEmail, LocalDate createdDate) {
        String sql = """
                DELETE FROM workout_logs WHERE members_email = :memberEmail AND created_date = :createdDate
                """;

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("memberEmail", memberEmail)
                .addValue("createdDate", createdDate);

        jdbcTemplate.update(sql, param);
    }
}
