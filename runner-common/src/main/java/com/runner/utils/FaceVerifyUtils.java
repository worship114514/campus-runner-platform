package com.runner.utils;

import com.runner.enums.FaceVerifyType;
import com.runner.utils.extend.AliyunResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FaceVerifyUtils {

    final static Logger logger = LoggerFactory.getLogger(FaceVerifyUtils.class);

    @Autowired
    private AliyunResource aliyunResource;

    /**
     * 人脸比对
     * 生产环境请替换为真实的阿里云Facebody SDK调用
     */
    public boolean faceVerify(int type, String face1, String face2, double targetConfidence) {
        logger.info("========== 人脸比对模拟 ==========");
        logger.info("比对类型: {}", type == FaceVerifyType.BASE64.type ? "Base64" : "图片URL");
        logger.info("人脸1: {}", face1.substring(0, Math.min(face1.length(), 50)) + "...");
        logger.info("人脸2: {}", face2.substring(0, Math.min(face2.length(), 50)) + "...");
        logger.info("目标置信度: {}", targetConfidence);
        logger.info("==================================");

        // 模拟返回 true（实际生产环境应调用阿里云Facebody SDK）
        // 生产环境真实实现：
        // 1. 使用 aliyun-java-sdk-facebody 1.2.18
        // 2. 调用 CompareFaceRequest 接口
        // 3. 参考阿里云官方文档：https://help.aliyun.com/document_detail/152598.html
        return true;
    }

    /**
     * 人脸比对（新版SDK）
     */
    public boolean faceVerify_new(int type, String face1, String face2, float targetConfidence) {
        return faceVerify(type, face1, face2, targetConfidence);
    }

    public String getImgBase64(String imgUrl) {
        // 模拟返回Base64编码
        return "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD//gA7Q1JFQVRPUjogZ2QtanBlZyB2MS4w";
    }
}