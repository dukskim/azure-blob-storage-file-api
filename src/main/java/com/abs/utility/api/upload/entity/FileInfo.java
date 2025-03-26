package com.abs.utility.api.upload.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FileInfo {

    private String fileId;
    private String uploadId;
    private String serviceId;
    private String userId;
    private String domainInfo;
    private String pathInfo;
    private String fileName;
    private String savedName;
    private String extName;
    private BigInteger fileSize;
    private String mimeType;
    private Integer width;
    private Integer height;
    private Integer status;
    private Integer deleted;
    private LocalDateTime expireDate;
    private String channel;
    private LocalDateTime uploadDate;
    private LocalDateTime updateDate;
}
