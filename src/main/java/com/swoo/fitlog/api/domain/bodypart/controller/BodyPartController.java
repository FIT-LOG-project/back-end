package com.swoo.fitlog.api.domain.bodypart.controller;

import com.swoo.fitlog.api.domain.bodypart.dto.BodyPartBasicDTO;
import com.swoo.fitlog.api.domain.bodypart.dto.BodyPartDTO;
import com.swoo.fitlog.api.domain.bodypart.service.BodyPartService;
import com.swoo.fitlog.http.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BodyPartController {

    private final BodyPartService bodyPartService;

    @GetMapping("/api/v1/body-parts")
    public ResponseEntity<RestResponse<List<BodyPartBasicDTO>>> getBodyParts() {
        List<BodyPartBasicDTO> findBodyParts = bodyPartService.findAll();

        RestResponse<List<BodyPartBasicDTO>> restResponse =
                RestResponse.ok("모든 운동 부위 조회 성공", findBodyParts);
        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }

    @PostMapping("/api/v1/body-parts")
    public ResponseEntity<RestResponse<Object>> createBodyPart(@RequestBody BodyPartDTO bodyPart) {
        bodyPartService.save(bodyPart);

        RestResponse<Object> restResponse = RestResponse.ok("운동 부위 추가 성공", null);
        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }

    @PatchMapping("/api/v1/body-parts/{bodyPartId}")
    public ResponseEntity<RestResponse<Object>> updateBodyPart(@PathVariable byte bodyPartId,
                                                               @RequestBody BodyPartDTO bodyPart) {
        bodyPartService.updateName(bodyPartId, bodyPart);

        RestResponse<Object> restResponse = RestResponse.ok("운동 부위 이름 수정 성공", null);
        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }

    @DeleteMapping("/api/v1/body-parts/{bodyPartId}")
    public ResponseEntity<RestResponse<Object>> deleteBodyPart(@PathVariable byte bodyPartId) {
        bodyPartService.deleteById(bodyPartId);

        RestResponse<Object> restResponse = RestResponse.ok("운동 부위 삭제 완료", null);
        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }
}
