package com.runner.files.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.runner.files.resource.FileResource;
import com.runner.files.service.UploaderService;
import com.runner.utils.extend.AliyunResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class UploaderServiceImpl implements UploaderService {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private FileResource fileResource;

    @Autowired
    private AliyunResource aliyunResource;

    @Override
    public String uploadFdfs(MultipartFile file, String fileExtName) throws Exception {
        StorePath storePath = fastFileStorageClient.uploadFile(
                file.getInputStream(),
                file.getSize(),
                fileExtName,
                null
        );
        // 返回相对路径，前端拼接 host 使用
        return storePath.getFullPath();
    }

    @Override
    public String uploadOSS(MultipartFile file, String userId, String fileExtName) throws Exception {
        OSS ossClient = new OSSClientBuilder().build(
                fileResource.getEndpoint(),
                aliyunResource.getAccessKeyId(),
                aliyunResource.getAccessKeySecret()
        );

        InputStream inputStream = file.getInputStream();
        String fileName = UUID.randomUUID().toString().replace("-", "");
        String objectName = fileResource.getObjectName() + "/" + userId + "/" + fileName + "." + fileExtName;

        ossClient.putObject(fileResource.getBucketName(), objectName, inputStream);
        ossClient.shutdown();

        return fileResource.getOssHost() + objectName;
    }
}