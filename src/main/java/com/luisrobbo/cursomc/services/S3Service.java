package com.luisrobbo.cursomc.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.luisrobbo.cursomc.services.exceptions.FileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

@Service
public class S3Service {

    private Logger LOG = LoggerFactory.getLogger(S3Service.class);

    @Autowired
    private AmazonS3 s3client;

    @Value("${s3.bucket}")
    private String bucketName;

    public URI uploadFile(MultipartFile multipartFile) {
        try {

            String fileName = multipartFile.getOriginalFilename();
            String contentType = multipartFile.getContentType();
            InputStream inputStream = null;
            inputStream = multipartFile.getInputStream();
            return uploadFile(inputStream, fileName, contentType);

        } catch (IOException ex) {
            throw new FileException("Erro de IO: " + ex.getMessage());
        }
    }


    public URI uploadFile(InputStream is, String fineName, String contentType) {
        try {
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentType(contentType);
            LOG.info("Iniciando Upload");
            s3client.putObject(bucketName, fineName, is, meta);
            LOG.info("Upload Finalizado");

            return s3client.getUrl(bucketName, fineName).toURI();

        } catch (URISyntaxException e) {
            throw new FileException("Erro ao converter URL para URI");
        }
    }


}
