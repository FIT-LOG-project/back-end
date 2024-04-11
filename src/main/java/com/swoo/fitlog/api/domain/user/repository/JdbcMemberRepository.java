package com.swoo.fitlog.api.domain.user.repository;

import com.swoo.fitlog.api.domain.user.MemberStatus;
import com.swoo.fitlog.api.domain.user.entity.Member;
import com.swoo.fitlog.exception.DuplicatedEmailException;
import com.swoo.fitlog.exception.NotExistMemberException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
@Repository
public class JdbcMemberRepository implements MemberRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcMemberRepository(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Member save(Member member) {
        String sql = "insert into members(email, password, status) values (:email, :password, :status)";

        checkEmailExists(member.getEmail());

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("email", member.getEmail())
                .addValue("password", member.getPassword())
                .addValue("status", String.valueOf(MemberStatus.NEW));

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, param, keyHolder);

        long key = keyHolder.getKey().longValue();
        member.setId(key);

        return member;
    }

    @Override
    public Member findById(Long id) {
        String sql = "select id, email, password, status from members where id=:id";

        MapSqlParameterSource param = new MapSqlParameterSource().addValue("id", id);

        try {
            return jdbcTemplate.queryForObject(sql, param, memberRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new NotExistMemberException("존재하지 않는 회원을 요청했습니다.");
        }
    }

    @Override
    public List<Member> findAll() {
        String sql = "select id, email, password, nick_name from members";

        return jdbcTemplate.query(sql, memberRowMapper());
    }

    @Override
    public void update(Long id, Member updateMember) {
        String sql = "update members set password=:password where id=:id";

        log.info(updateMember.getPassword());

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("password", updateMember.getPassword())
                .addValue("id", id);

        jdbcTemplate.update(sql, param);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "delete from members where id=:id";

        MapSqlParameterSource param = new MapSqlParameterSource().addValue("id", id);

        jdbcTemplate.update(sql, param);
    }

    public void checkEmailExists(String email) {
        String sql = "select count(*) from members where email=:email";

        MapSqlParameterSource param = new MapSqlParameterSource().addValue("email", email);

        int count = jdbcTemplate.queryForObject(sql, param, Integer.class);

        if (count > 0) {
            throw new DuplicatedEmailException("이미 존재하는 이메일을 요청하였습니다");
        }
    }

    private RowMapper<Member> memberRowMapper() {
        return BeanPropertyRowMapper.newInstance(Member.class);
    }
}
