package com.runner.files.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploaderService {
    String uploadFdfs(MultipartFile file, String fileExtName) throws Exception;
    String uploadOSS(MultipartFile file, String userId, String fileExtName) throws Exception;
}