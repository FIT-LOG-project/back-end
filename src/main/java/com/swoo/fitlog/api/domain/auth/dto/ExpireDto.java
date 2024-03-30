package com.swoo.fitlog.api.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ExpireDto {

    private Long expire;

    public static ExpireDto from(Long expire) {
        ExpireDto expireDto = new ExpireDto();
        expireDto.setExpire(expire);
        return expireDto;
    }
}
