package com.runner.utils.extend;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:aliyun.properties")
@ConfigurationProperties(prefix = "aliyun")
public class AliyunResource {
    private String accessKeyId;
    private String accessKeySecret;
    private String ossEndpoint;
    private String ossBucketName;
    private String greenEndpoint;

    // getter/setter 方法
    public String getAccessKeyId() { return accessKeyId; }
    public void setAccessKeyId(String accessKeyId) { this.accessKeyId = accessKeyId; }

    public String getAccessKeySecret() { return accessKeySecret; }
    public void setAccessKeySecret(String accessKeySecret) { this.accessKeySecret = accessKeySecret; }

    public String getOssEndpoint() { return ossEndpoint; }
    public void setOssEndpoint(String ossEndpoint) { this.ossEndpoint = ossEndpoint; }

    public String getOssBucketName() { return ossBucketName; }
    public void setOssBucketName(String ossBucketName) { this.ossBucketName = ossBucketName; }

    public String getGreenEndpoint() { return greenEndpoint; }
    public void setGreenEndpoint(String greenEndpoint) { this.greenEndpoint = greenEndpoint; }
}