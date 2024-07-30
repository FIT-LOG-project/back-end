package com.swoo.fitlog.api.domain.bodypart.service;

import com.swoo.fitlog.api.domain.bodypart.dto.BodyPartBasicDTO;
import com.swoo.fitlog.api.domain.bodypart.dto.BodyPartDTO;
import com.swoo.fitlog.api.domain.bodypart.repository.BodyPartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BodyPartServiceImpl implements BodyPartService {

    private final BodyPartRepository bodyPartRepository;

    @Override
    public void save(BodyPartDTO bodyPart) {
        bodyPartRepository.save(bodyPart);
    }

    @Override
    public List<BodyPartBasicDTO> findAll() {
        return bodyPartRepository.findAll();
    }

    @Override
    public void updateName(byte bodyPartId, BodyPartDTO bodyPart) {
        bodyPartRepository.updateName(bodyPartId, bodyPart);
    }

    @Override
    public void deleteById(byte bodyPartId) {
        bodyPartRepository.deleteById(bodyPartId);
    }
}
