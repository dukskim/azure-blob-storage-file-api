package com.abs.utility.api.upload.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

public class FileUplaodDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class FilesUploadRes {

        @ApiModelProperty(value = "전체 건", example = "3")
        private int totalCnt;

        @ApiModelProperty(value = "성공 건", example = "2")
        private int successCnt;

        @ApiModelProperty(value = "실패 건", example = "1")
        private int failCnt;

        @ApiModelProperty(value = "성공한 파일 정보 목록")
        private List<FileUploadRes> successFileInfoList;

        @ApiModelProperty(value = "실패한 파일명 목록")
        private List<String> failFileNameList;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class FileUploadRes {

//        @ApiModelProperty(value = "원본 파일명", example = "file.jpg")
//        private String orgFileName;
//
//        @ApiModelProperty(value = "Blob 명", example = "string")
//        private String blobName;
//
//        @ApiModelProperty(value = "url", example = "string")
//        private String blobUrl;

        @ApiModelProperty(value = "파일 url", example = "https://restiwbdev.wjcompass.com/download/72acfcd6-b365-426c-8f39-0c1eff11e7e9-1698310645839.jpg")
        private String fileUrl;

        @ApiModelProperty(value = "파일아이디", example = "20231026163316066856")
        private String fileId;

        @ApiModelProperty(value = "업로드아이디", example = "20231026163316066855")
        private String uploadId;

//        private String serviceId;
//        private String userId;
        @ApiModelProperty(value = "도메인정보", example = "https://restiwbdev.wjcompass.com")
        private String domainInfo;

        @ApiModelProperty(value = "경로", example = "download")
        private String pathInfo;

        @ApiModelProperty(value = "원본 파일명", example = "file.jpg")
        private String fileName;

        @ApiModelProperty(value = "저장 파일명", example = "file.jpg")
        private String savedName;

        @ApiModelProperty(value = "파일 확장자", example = ".jpg")
        private String extName;

        @ApiModelProperty(value = "파일 사이즈(KB)", example = "13")
        private BigInteger fileSize;

        @ApiModelProperty(value = "mime type", example = "image/jpeg")
        private String mimeType;

        @ApiModelProperty(value = "이미지 with", example = "400")
        private Integer width;

        @ApiModelProperty(value = "이미지 height", example = "266")
        private Integer height;

//        private Integer status;
//        private Integer deleted;
//        private LocalDateTime expireDate;
//        private String channel;
//        private LocalDateTime uploadDate;
//        private LocalDateTime updateDate;

    }

}
