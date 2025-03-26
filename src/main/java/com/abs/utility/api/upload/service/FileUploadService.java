package com.abs.utility.api.upload.service;

import com.abs.utility.api.upload.dto.FileUplaodDto;
import com.abs.utility.api.upload.entity.FileInfo;
import com.abs.utility.api.upload.vo.AzureProperties;
import com.abs.utility.api.upload.vo.ServiceProperties;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.abs.utility.api.upload.mapper.FileInfoMapper;
import com.abs.utility.common.util.FileCtlUtil;
import com.abs.utility.common.util.StringCtlUtil;
import com.abs.utility.core.aop.exception.FmException;
import com.abs.utility.core.aop.exception.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final AzureProperties azureProperties;

    private final ServiceProperties serviceProperties;

    private final ImageConvertService imageConvertService;

    private final FileInfoMapper fileInfoMapper;

    private final FileUtilService fileUtilService;

    /**
     * Azure 파일 업로드
     * @param file
     * @param orgFileName
     * @return
     */
    @Transactional
    public FileUplaodDto.FileUploadRes fileUploadToAzure(MultipartFile file, String orgFileName, String userId, String channel) throws IOException {

        if (file == null) {
            // 파라미터 체크
            throw new FmException(ErrorCode.ERROR_FM1003);
        }
        String connectionString = azureProperties.getConnectionString();
        String containerName = azureProperties.getContainerName();
        log.debug("************************************");
        log.debug("connectionString:: {}", connectionString);
        log.debug("containerName:: {}", containerName);
        log.debug("************************************");

        BlobContainerClient container = new BlobContainerClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .buildClient();
        InputStream fis = file.getInputStream();
        long filesize = file.getSize();
        String newOnlyFileName =  UUID.randomUUID() +"-"+ System.currentTimeMillis();
        String fileExt = FileCtlUtil.getExtension(orgFileName);
        if (StringUtils.hasLength(fileExt)) {
            fileExt = "." + fileExt;
        }
        String newFileFullName = newOnlyFileName + fileExt;
        log.debug("newFileFullName: ", newFileFullName);

        BlobClient blob = container.getBlobClient(newFileFullName);
        blob.upload(fis, filesize, true);

        log.debug("upload compleate!!!!");

        String blobUrl = blob.getBlobUrl();
        String blobName = blob.getBlobName();

        log.debug("blobUrl: {}", blobUrl);
        log.debug("blobName: {}", blobName);

        // 도메인 제외한 경로 추출
        String pathInfo = StringCtlUtil.disseverUrlToOnlyPath(blobUrl).replace("/"+blobName, "");
        String mimeType = file.getContentType();

        int width = 0;
        int height = 0;
        // 이미지일 경우 with, height 가져오기
        if (Objects.requireNonNull(file.getContentType()).contains("image")) {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            width = bufferedImage.getWidth();
            height = bufferedImage.getHeight();
        }
        BigInteger kilobyte = BigInteger.valueOf(filesize / 1024);

        // db 저장 (file 테이블)
        String uploadId = fileUtilService.makeFileId();
        String fileId = fileUtilService.makeFileId();
        FileInfo fileInfo = FileInfo.builder()
                .fileId(fileId)
                .uploadId(uploadId)
                .serviceId(serviceProperties.getName())
                .userId(StringUtils.hasLength(userId) ? userId : "0")
                .domainInfo(azureProperties.getCdnUrl())
                .pathInfo(pathInfo)
                .fileName(orgFileName)
                .savedName(newFileFullName)
                .extName(fileExt)
                .fileSize(kilobyte)
                .mimeType(mimeType)
                .width(width)
                .height(height)
                .status(0)
                .channel(StringCtlUtil.isEmptyTrim(channel) ? serviceProperties.getName() : channel)
                .build();


        int res = fileInfoMapper.addFileInfo(fileInfo);

        if (res < 1) {
            throw new FmException(ErrorCode.ERROR_FM1013);
        }

        // 객체 프로퍼티 복사
        FileUplaodDto.FileUploadRes result = new FileUplaodDto.FileUploadRes();
        BeanUtils.copyProperties(fileInfo, result);
        String fileUrl = result.getDomainInfo();
        if (StringUtils.hasLength(result.getPathInfo())) fileUrl = fileUrl + "/" + result.getPathInfo();
        if (StringUtils.hasLength(result.getSavedName())) fileUrl = fileUrl + "/" + result.getSavedName();
        result.setFileUrl(fileUrl);

        return result;
    }

    /**
    * 이미지 resize 후 Azure 업로드
    * @param file
    * @param userId
    * @param maxWith
    * @return
    */
    public FileUplaodDto.FileUploadRes fileResizeUploadToAzure(MultipartFile file, String userId, Integer maxWith, String channel) throws IOException {

        // 파라미터 체크
        if (file == null) {
            throw new FmException(ErrorCode.ERROR_FM1003);
        }
        if (!Objects.requireNonNull(file.getContentType()).contains("image")) {
            throw new FmException(ErrorCode.ERROR_FM1005);
        }

        log.debug("----------------------------");
        log.debug("파일업로드 (이미지변환 후)");
        log.debug("----------------------------");

        String fileName = file.getOriginalFilename();
        String fileFormat = file.getContentType().substring(file.getContentType().lastIndexOf("/") + 1);

        log.debug("fileName: {}",fileName);
        log.debug("fileFormat: {}",fileFormat);

        MultipartFile uploadFile = null;
        try {
            log.debug("1. 업로드 변환");
            int convertMaxWith = 400; // 기본 400 사이즈
            if (maxWith != null && maxWith > 0) {
                convertMaxWith = maxWith;
            }
            uploadFile = imageConvertService.resizeImage(fileName, fileFormat, file, convertMaxWith);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 변환에러
        if (uploadFile == null) throw new FmException(ErrorCode.ERROR_FM1012);

        log.debug("2. 변환종료");
        log.debug("newFileName: {}",uploadFile.getOriginalFilename());
        log.debug("newFileFormat: {}",uploadFile.getContentType().substring(file.getContentType().lastIndexOf("/") + 1));

        // 테스트용: 파일저장하여 변환 제대로 된 지 확인
//        File savefile = new File(System.getProperty("user.dir") + "/" + fileName);
//        uploadFile.transferTo(savefile);
//        log.debug("savefile.getPath(): {}",savefile.getPath());

        return fileUploadToAzure(uploadFile, fileName, userId, channel);
    }


    /**
     * 이미지 resize 후 Azure 업로드
     * @param files
     * @param userId
     * @param maxWith
     * @return
     * @throws IOException
     */
    public FileUplaodDto.FilesUploadRes fileResizeUploadToAzure(MultipartFile files[], String userId, Integer maxWith, String channel) throws IOException {

        // 파라미터 체크
        if (files == null || files.length < 1) {
            throw new FmException(ErrorCode.ERROR_FM1003);
        }

        String[] fileNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            fileNames[i] = files[i] != null ? files[i].getOriginalFilename() : null;
        }
        MultipartFile[] uploadFile = new MultipartFile[files.length];

        log.debug("----------------------------");
        log.debug("파일업로드 (이미지변환 후)");
        log.debug("----------------------------");

        int i = 0;
        int totalCnt = 0;
        List<String> failFileNameList = new ArrayList<>();
        int failCnt = 0;
        for (MultipartFile file: files) {
            if (file != null) {
                String fileName = file.getOriginalFilename();
                String fileFormat = file.getContentType().substring(file.getContentType().lastIndexOf("/") + 1);
                log.debug("fileName: {}", fileName);
                log.debug("fileFormat: {}", fileFormat);

                try {
                    log.debug("1. 업로드 변환");
                    int convertMaxWith = 400; // 기본 400 사이즈
                    if (maxWith != null && maxWith > 0) {
                        convertMaxWith = maxWith;
                    }
                    uploadFile[i] = imageConvertService.resizeImage(UUID.randomUUID().toString(), fileFormat, file, convertMaxWith);
                } catch (Exception e) {
                    e.printStackTrace();
                    failFileNameList.add(fileName);
                    failCnt++;
                }
                totalCnt++;
            }
            i++;
        }


        FileUplaodDto.FilesUploadRes result = fileUploadToAzure(uploadFile, fileNames, userId, channel);
        result.setTotalCnt(totalCnt);
        result.setFailCnt(result.getFailCnt() + failCnt);
        failFileNameList.addAll(result.getFailFileNameList());
        result.setFailFileNameList(failFileNameList);

        return result;
    }

    /**
     * 이미지 resize 후 Azure 업로드
     * @param files
     * @param userId
     * @param maxWith
     * @param channel
     * @return
     * @throws IOException
     */
    public FileUplaodDto.FilesUploadRes fileResizeUploadToAzure(MultipartFile files[], String userId, Integer maxWith[], String channel) throws IOException {

        // 파라미터 체크
        if (files == null || files.length < 1) {
            throw new FmException(ErrorCode.ERROR_FM1003);
        }

        String[] fileNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            fileNames[i] = files[i] != null ? files[i].getOriginalFilename() : null;
        }
        MultipartFile[] uploadFile = new MultipartFile[files.length];

        log.debug("----------------------------");
        log.debug("파일업로드 (이미지변환 후)");
        log.debug("----------------------------");

        int i = 0;
        int totalCnt = 0;
        List<String> failFileNameList = new ArrayList<>();
        int failCnt = 0;
        for (MultipartFile file: files) {
            if (file != null) {
                String fileName = file.getOriginalFilename();
                String fileFormat = file.getContentType().substring(file.getContentType().lastIndexOf("/") + 1);
                log.debug("fileName: {}", fileName);
                log.debug("fileFormat: {}", fileFormat);

                try {
                    log.debug("1. 업로드 변환");
                    int convertMaxWith = 400; // 기본 400 사이즈
                    if (maxWith[i] != null && maxWith[i] > 0) {
                        convertMaxWith = maxWith[i];
                    }
                    uploadFile[i] = imageConvertService.resizeImage(UUID.randomUUID().toString(), fileFormat, file, convertMaxWith);
                } catch (Exception e) {
                    e.printStackTrace();
                    failFileNameList.add(fileName);
                    failCnt++;
                }
                totalCnt++;
            }
            i++;
        }


        FileUplaodDto.FilesUploadRes result = fileUploadToAzure(uploadFile, fileNames, userId, channel);
        result.setTotalCnt(totalCnt);
        result.setFailCnt(result.getFailCnt() + failCnt);
        failFileNameList.addAll(result.getFailFileNameList());
        result.setFailFileNameList(failFileNameList);

        return result;
    }

    /**
     * Azure 파일 업로드
     * @param files
     * @return
     */
    public FileUplaodDto.FilesUploadRes fileUploadToAzure(MultipartFile[] files, String[] orgFileNames, String userId, String channel) {

        List<FileUplaodDto.FileUploadRes> successFileList = new ArrayList<>();
        List<String> failFileList = new ArrayList<>();
        int totalCnt = 0;

        String connectionString = azureProperties.getConnectionString();
        String containerName = azureProperties.getContainerName();
        log.debug("************************************");
        log.debug("connectionString:: {}", connectionString);
        log.debug("containerName:: {}", containerName);
        log.debug("************************************");

        BlobContainerClient container = new BlobContainerClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .buildClient();

        int i = 0;
        String uploadId = fileUtilService.makeFileId();
        for (MultipartFile file: files) {
            if (file != null) {
                totalCnt++;
                String orgFileName = orgFileNames[i];
                try {
                    InputStream fis = file.getInputStream();
                    long filesize = file.getSize();
                    String newOnlyFileName =  UUID.randomUUID() +"-"+ System.currentTimeMillis();
                    String fileExt = FileCtlUtil.getExtension(orgFileName);
                    if (StringUtils.hasLength(fileExt)) {
                        fileExt = "." + fileExt;
                    }
                    String newFileFullName = newOnlyFileName + fileExt;
                    log.debug("newFileFullName: ", newFileFullName);

                    BlobClient blob = container.getBlobClient(newFileFullName);
                    blob.upload(fis, filesize, true);

                    log.debug("upload compleate!!!!");

                    String blobUrl = blob.getBlobUrl();
                    String blobName = blob.getBlobName();

                    log.debug("blobUrl: {}", blobUrl);
                    log.debug("blobName: {}", blobName);

                    // 도메인 제외한 경로 추출
                    String pathInfo = StringCtlUtil.disseverUrlToOnlyPath(blobUrl).replace("/"+blobName, "");
                    String mimeType = file.getContentType();

                    int width = 0;
                    int height = 0;
                    // 이미지일 경우 with, height 가져오기
                    if (Objects.requireNonNull(file.getContentType()).contains("image")) {
                        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
                        width = bufferedImage.getWidth();
                        height = bufferedImage.getHeight();
                    }
                    BigInteger kilobyte = BigInteger.valueOf(filesize / 1024);

                    // db 저장 (file 테이블)
                    String fileId = fileUtilService.makeFileId();
                    FileInfo fileInfo = FileInfo.builder()
                            .fileId(fileId)
                            .uploadId(uploadId)
                            .serviceId(serviceProperties.getName())
                            .userId(StringUtils.hasLength(userId) ? userId : "0")
                            .domainInfo(azureProperties.getCdnUrl())
                            .pathInfo(pathInfo)
                            .fileName(orgFileName)
                            .savedName(newFileFullName)
                            .extName(fileExt)
                            .fileSize(kilobyte)
                            .mimeType(mimeType)
                            .width(width)
                            .height(height)
                            .status(0)
                            .channel(StringCtlUtil.isEmptyTrim(channel) ? serviceProperties.getName() : channel)
                            .build();

                    int res = fileInfoMapper.addFileInfo(fileInfo);

                    // 객체 프로퍼티 복사
                    FileUplaodDto.FileUploadRes item = new FileUplaodDto.FileUploadRes();
                    BeanUtils.copyProperties(fileInfo, item);
                    String fileUrl = item.getDomainInfo();
                    if (StringUtils.hasLength(item.getPathInfo())) fileUrl = fileUrl + "/" + item.getPathInfo();
                    if (StringUtils.hasLength(item.getSavedName())) fileUrl = fileUrl + "/" + item.getSavedName();
                    item.setFileUrl(fileUrl);
                    successFileList.add(item);
                } catch (Exception e) {
                    failFileList.add(orgFileName);
                    e.printStackTrace();
                }
            }
            i++;
        }

        return FileUplaodDto.FilesUploadRes.builder()
                .successCnt(successFileList.size())
                .successFileInfoList(successFileList)
                .failCnt(failFileList.size())
                .failFileNameList(failFileList)
                .totalCnt(totalCnt)
                .build();
    }

}
