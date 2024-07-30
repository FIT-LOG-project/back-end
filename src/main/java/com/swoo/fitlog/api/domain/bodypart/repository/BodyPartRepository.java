package com.swoo.fitlog.api.domain.bodypart.repository;

import com.swoo.fitlog.api.domain.bodypart.dto.BodyPartBasicDTO;
import com.swoo.fitlog.api.domain.bodypart.dto.BodyPartDTO;

import java.util.List;

public interface BodyPartRepository {

    byte save(BodyPartDTO bodyPart);

    List<BodyPartBasicDTO> findAll();

    void updateName(byte bodyPartId, BodyPartDTO bodyPart);

    void deleteById(byte bodyPartId);
}