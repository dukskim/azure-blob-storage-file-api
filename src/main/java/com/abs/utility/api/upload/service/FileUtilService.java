package com.abs.utility.api.upload.service;

import com.abs.utility.api.upload.mapper.FileInfoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUtilService {

    private final FileInfoMapper fileInfoMapper;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public String makeFileId() {

        return fileInfoMapper.findNextFileId();

    }

}
