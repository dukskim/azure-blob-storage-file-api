package com.abs.utility.api.upload.mapper;

import com.abs.utility.api.upload.entity.FileInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileInfoMapper {

    String findNextFileId();

    int addFileInfo(FileInfo fileInfo);
}
