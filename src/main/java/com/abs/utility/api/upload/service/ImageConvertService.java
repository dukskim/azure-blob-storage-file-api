package com.abs.utility.api.upload.service;

import lombok.extern.slf4j.Slf4j;
import marvin.image.MarvinImage;
import org.marvinproject.image.transform.scale.Scale;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Service
public class ImageConvertService {

    public MultipartFile resizeImage(String fileName, String fileFormat, MultipartFile originalImage, int maxWith) throws IOException {
       log.debug("----------------------------");
       log.debug("이미지 리사이즈 시작");
       log.debug("----------------------------");
       BufferedImage image = ImageIO.read(originalImage.getInputStream());// MultipartFile -> BufferedImage Convert

       int originWidth = image.getWidth();
       int originHeight = image.getHeight();

       log.debug("originWidth: {}",originWidth);
       log.debug("originHeight: {}",originHeight);

       // origin 이미지가 400보다 작으면 패스
       if(originWidth < maxWith)
           return originalImage;

       MarvinImage imageMarvin = new MarvinImage(image);

       Scale scale = new Scale();
       scale.load();
       scale.setAttribute("newWidth", maxWith);
       scale.setAttribute("newHeight", maxWith * originHeight / originWidth); //비율유지를 위해 높이 유지
       scale.process(imageMarvin.clone(), imageMarvin, null, null, false);

       BufferedImage imageNoAlpha = imageMarvin.getBufferedImageNoAlpha();
       ByteArrayOutputStream baos = new ByteArrayOutputStream();
       ImageIO.write(imageNoAlpha, fileFormat, baos);
       baos.flush();

       return new MockMultipartFile(fileName,fileFormat,originalImage.getContentType(), baos.toByteArray());

    }
}
