package com.swoo.fitlog.api.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swoo.fitlog.api.domain.auth.dto.EmailDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("이메일 필드 에러 발생 - 에러 코드 10")
    void occurFieldError() throws Exception {
        // given
        String email = "sdlkfj";
        String URI = "/api/v1/auth/locals/email";

        EmailDto emailDto = new EmailDto();
        emailDto.setEmail(email);

        String emailJson = objectMapper.writeValueAsString(emailDto);

        // when
        // then
        mvc.perform(MockMvcRequestBuilders
                        .post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emailJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCodes[0]").value(10))
                .andDo(print());
    }
}