package com.abs.utility.api.upload.controller;

import com.abs.utility.core.base.dto.ApiResult;
import com.abs.utility.api.upload.dto.FileUplaodDto;
import com.abs.utility.api.upload.service.FileUploadService;
import com.abs.utility.common.util.StringCtlUtil;
import com.abs.utility.core.aop.exception.FmException;
import com.abs.utility.core.aop.exception.type.ErrorCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
@Api(tags = "파일업로드")
public class FileUploadController {

    private final FileUploadService fileUploadService;


    @PostMapping(value = "/upload/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "이미지 파일업로드", notes = "이미지 파일을 변환 후 Azure Blob Storage 에 업로드 합니다.(단건) <br/><br/>" +
            "[요청]<br/>" +
            "file: 업로드파일 (MultipartFile) <br/>" +
            "userId: user id (int) <br/>" +
            "<br/>" +
            "[응답] <br/>" +
            "fileUrl: CDN 파일 URL <br/>" +
            "fileId: 파일아이디 <br/>" +
            "uploadId: 업로드아이디 <br/>"
    )
    public ResponseEntity<ApiResult<FileUplaodDto.FileUploadRes>> fileUploadToAzure(
            @ApiParam(value = "업로드파일", required = true) @RequestPart(value = "file", required = true) MultipartFile file
            , @ApiParam(value = "user id", required = false) @RequestPart(value = "userId", required = false) String userId
            , @ApiParam(value = "변환할 with 최대 사이즈", required = false) @RequestPart(value = "maxWith", required = false) String maxWith
            , @ApiParam(value = "업로드 서비스 채널", required = false) @RequestPart(value = "channel", required = false) String channel
    ) throws IOException {

        // 파라미터 체크
        Integer with = null;
        if (StringUtils.hasLength(maxWith)) {
            try {
                with = Integer.parseInt(maxWith);
            } catch (Exception e) {
                throw new FmException(ErrorCode.ERROR_FM1006);
            }
        }

        FileUplaodDto.FileUploadRes result = fileUploadService.fileResizeUploadToAzure(file, userId, with, channel);
        ApiResult apiResult = new ApiResult(ApiResult.RESULT_CODE_OK, ApiResult.OK);
        apiResult.setDataObj(result);

        return ResponseEntity.ok().body(apiResult);
    }

    @PostMapping(value = "/upload/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "이미지 파일업로드 (복수)", notes = "이미지 파일을 변환 후 Azure Blob Storage 에 업로드 합니다.(복수) <br/><br/>" +
            "[요청]<br/>" +
            "files: 업로드파일 (MultipartFile) <br/>" +
            "userId: user id (int) <br/>" +
            "<br/>" +
            "[응답] <br/>" +
            "fileUrl: CDN 파일 URL <br/>" +
            "fileId: 파일아이디 <br/>" +
            "uploadId: 업로드아이디 <br/>"
    )
    public ResponseEntity<ApiResult<FileUplaodDto.FilesUploadRes>> fileUploadToAzure(
            @ApiParam(value = "업로드파일", required = true) @RequestPart(value = "file", required = true) MultipartFile files[]
            , @ApiParam(value = "user id", required = false) @RequestPart(value = "userId", required = false) String userId
            , @ApiParam(value = "변환할 with 최대 사이즈", required = false) @RequestPart(value = "maxWith", required = false) String maxWith
            , @ApiParam(value = "업로드 서비스 채널", required = false) @RequestPart(value = "channel", required = false) String channel
    ) throws IOException {

        // 파라미터 체크
        Integer with = null;
        if (StringUtils.hasLength(maxWith)) {
            try {
                with = Integer.parseInt(maxWith);
            } catch (Exception e) {
                throw new FmException(ErrorCode.ERROR_FM1006);
            }
        }

        FileUplaodDto.FilesUploadRes result = fileUploadService.fileResizeUploadToAzure(files, userId, with, channel);
        ApiResult apiResult = new ApiResult(ApiResult.RESULT_CODE_OK, ApiResult.OK);
        apiResult.setDataObj(result);

        return ResponseEntity.ok().body(apiResult);
    }

    @PostMapping(value = "/upload/images-size", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "이미지 파일업로드 (업로드 파일 복수, 사이즈 복수 지정)", notes = "이미지 파일을 변환 후 Azure Blob Storage 에 업로드 합니다.(업로드 파일 복수, 사이즈 복수 지정) <br/><br/>" +
            "[요청]<br/>" +
            "files: 업로드파일 (MultipartFile) <br/>" +
            "userId: user id (int) <br/>" +
            "<br/>" +
            "[응답] <br/>" +
            "fileUrl: CDN 파일 URL <br/>" +
            "fileId: 파일아이디 <br/>" +
            "uploadId: 업로드아이디 <br/>"
    )
    public ResponseEntity<ApiResult<FileUplaodDto.FilesUploadRes>> fileUploadToAzureSize(
            @ApiParam(value = "업로드파일1", required = false) @RequestPart(value = "file1", required = false) MultipartFile file1
            , @ApiParam(value = "업로드파일2", required = false) @RequestPart(value = "file2", required = false) MultipartFile file2
            , @ApiParam(value = "업로드파일3", required = false) @RequestPart(value = "file3", required = false) MultipartFile file3
            , @ApiParam(value = "업로드파일4", required = false) @RequestPart(value = "file4", required = false) MultipartFile file4
            , @ApiParam(value = "업로드파일5", required = false) @RequestPart(value = "file5", required = false) MultipartFile file5
            , @ApiParam(value = "업로드파일6", required = false) @RequestPart(value = "file6", required = false) MultipartFile file6
            , @ApiParam(value = "업로드파일7", required = false) @RequestPart(value = "file7", required = false) MultipartFile file7
            , @ApiParam(value = "업로드파일8", required = false) @RequestPart(value = "file8", required = false) MultipartFile file8
            , @ApiParam(value = "업로드파일9", required = false) @RequestPart(value = "file9", required = false) MultipartFile file9
            , @ApiParam(value = "업로드파일10", required = false) @RequestPart(value = "file10", required = false) MultipartFile file10
            , @ApiParam(value = "변환할 with 최대 사이즈", required = false) @RequestPart(value = "maxWith1", required = false) String maxWith1
            , @ApiParam(value = "변환할 with 최대 사이즈", required = false) @RequestPart(value = "maxWith2", required = false) String maxWith2
            , @ApiParam(value = "변환할 with 최대 사이즈", required = false) @RequestPart(value = "maxWith3", required = false) String maxWith3
            , @ApiParam(value = "변환할 with 최대 사이즈", required = false) @RequestPart(value = "maxWith4", required = false) String maxWith4
            , @ApiParam(value = "변환할 with 최대 사이즈", required = false) @RequestPart(value = "maxWith5", required = false) String maxWith5
            , @ApiParam(value = "변환할 with 최대 사이즈", required = false) @RequestPart(value = "maxWith6", required = false) String maxWith6
            , @ApiParam(value = "변환할 with 최대 사이즈", required = false) @RequestPart(value = "maxWith7", required = false) String maxWith7
            , @ApiParam(value = "변환할 with 최대 사이즈", required = false) @RequestPart(value = "maxWith8", required = false) String maxWith8
            , @ApiParam(value = "변환할 with 최대 사이즈", required = false) @RequestPart(value = "maxWith9", required = false) String maxWith9
            , @ApiParam(value = "변환할 with 최대 사이즈", required = false) @RequestPart(value = "maxWith10", required = false) String maxWith10
            , @ApiParam(value = "user id", required = false) @RequestPart(value = "userId", required = false) String userId
            , @ApiParam(value = "업로드 서비스 채널", required = false) @RequestPart(value = "channel", required = false) String channel
    ) throws IOException {

        MultipartFile[] files = new MultipartFile[10];
        files[0] = file1;
        files[1] = file2;
        files[2] = file3;
        files[3] = file4;
        files[4] = file5;
        files[5] = file6;
        files[6] = file7;
        files[7] = file8;
        files[8] = file9;
        files[9] = file10;

        Integer[] maxWiths = new Integer[10];
        maxWiths[0] = StringCtlUtil.parseInteger(maxWith1);
        maxWiths[1] = StringCtlUtil.parseInteger(maxWith2);
        maxWiths[2] = StringCtlUtil.parseInteger(maxWith3);
        maxWiths[3] = StringCtlUtil.parseInteger(maxWith4);
        maxWiths[4] = StringCtlUtil.parseInteger(maxWith5);
        maxWiths[5] = StringCtlUtil.parseInteger(maxWith6);
        maxWiths[6] = StringCtlUtil.parseInteger(maxWith7);
        maxWiths[7] = StringCtlUtil.parseInteger(maxWith8);
        maxWiths[8] = StringCtlUtil.parseInteger(maxWith9);
        maxWiths[9] = StringCtlUtil.parseInteger(maxWith10);


        FileUplaodDto.FilesUploadRes result = fileUploadService.fileResizeUploadToAzure(files, userId, maxWiths, channel);
        ApiResult apiResult = new ApiResult(ApiResult.RESULT_CODE_OK, ApiResult.OK);
        apiResult.setDataObj(result);


        return ResponseEntity.ok().body(apiResult);
    }


    @PostMapping(value = "/upload/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "일반 파일업로드", notes = "Azure Blob Storage 에 파일을 업로드 합니다.(단건) <br/><br/>" +
            "[요청]<br/>" +
            "file: 업로드파일 (MultipartFile) <br/>" +
            "userId: user id (int) <br/>" +
            "<br/>" +
            "[응답] <br/>" +
            "fileUrl: CDN 파일 URL <br/>" +
            "fileId: 파일아이디 <br/>" +
            "uploadId: 업로드아이디 <br/>"
    )
    public ResponseEntity<ApiResult<FileUplaodDto.FileUploadRes>> fileUploadToAzure(
            @ApiParam(value = "업로드파일", required = true) @RequestPart(value = "file", required = true) MultipartFile file
            , @ApiParam(value = "user id", required = false) @RequestPart(value = "userId", required = false) String userId
            , @ApiParam(value = "업로드 서비스 채널", required = false) @RequestPart(value = "channel", required = false) String channel
    ) throws IOException {
        String fileName = file.getOriginalFilename();

        log.debug("fileName: {}",fileName);

        FileUplaodDto.FileUploadRes result = fileUploadService.fileUploadToAzure(file, fileName, userId, channel);
        ApiResult apiResult = new ApiResult(ApiResult.RESULT_CODE_OK, ApiResult.OK);
        apiResult.setDataObj(result);

        return ResponseEntity.ok().body(apiResult);
    }


    @PostMapping(value = "/upload/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "일반 파일업로드 (복수)", notes = "Azure Blob Storage 에 파일을 업로드 합니다.(복수) <br/><br/>" +
            "[요청]<br/>" +
            "files: 업로드파일 (MultipartFile) <br/>" +
            "userId: user id (int) <br/>" +
            "<br/>" +
            "[응답] <br/>" +
            "fileUrl: CDN 파일 URL <br/>" +
            "fileId: 파일아이디 <br/>" +
            "uploadId: 업로드아이디 <br/>"
    )
    public ResponseEntity<ApiResult<FileUplaodDto.FilesUploadRes>> filesUploadToAzure(
            @ApiParam(value = "업로드파일", required = true) @RequestPart(value = "file", required = true) MultipartFile files[]
            , @ApiParam(value = "user id", required = false) @RequestPart(value = "userId", required = false) String userId
            , @ApiParam(value = "업로드 서비스 채널", required = false) @RequestPart(value = "channel", required = false) String channel
    ) throws IOException {

        if (files == null || files.length <1) {
            throw new FmException(ErrorCode.ERROR_FM1003);
        }

        String[] fileNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            fileNames[i] = files[i].getOriginalFilename();
        }

        FileUplaodDto.FilesUploadRes result = fileUploadService.fileUploadToAzure(files, fileNames, userId, channel);
        ApiResult apiResult = new ApiResult(ApiResult.RESULT_CODE_OK, ApiResult.OK);
        apiResult.setDataObj(result);

        return ResponseEntity.ok().body(apiResult);
    }


}
