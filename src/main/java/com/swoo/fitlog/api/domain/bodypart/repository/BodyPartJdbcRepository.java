package com.swoo.fitlog.api.domain.bodypart.repository;

import com.swoo.fitlog.api.domain.bodypart.dto.BodyPartBasicDTO;
import com.swoo.fitlog.api.domain.bodypart.dto.BodyPartDTO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;

@Repository
public class BodyPartJdbcRepository implements BodyPartRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public BodyPartJdbcRepository(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public byte save(BodyPartDTO saveBodyPart) {
        String sql = "INSERT INTO body_parts(name) VALUES (:name)";

        MapSqlParameterSource param = new MapSqlParameterSource().addValue("name", saveBodyPart.getName());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, param, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).byteValue();
    }

    @Override
    public List<BodyPartBasicDTO> findAll() {
        String sql = "SELECT id, name FROM body_parts";

        return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(BodyPartBasicDTO.class));
    }

    @Override
    public void updateName(byte bodyPartId, BodyPartDTO bodyPart) {
        String sql = "UPDATE body_parts SET name = :name WHERE id = :id";

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("id", bodyPartId)
                .addValue("name", bodyPart.getName());

        jdbcTemplate.update(sql, param);
    }

    @Override
    public void deleteById(byte bodyPartId) {
        String sql = "DELETE FROM body_parts WHERE id = :bodyPartId";

        MapSqlParameterSource param = new MapSqlParameterSource().addValue("bodyPartId", bodyPartId);

        jdbcTemplate.update(sql, param);
    }
}