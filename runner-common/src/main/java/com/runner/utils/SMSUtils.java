package com.runner.utils;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dypnsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.aliyun.sdk.service.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import com.google.gson.Gson;
import com.runner.utils.extend.AliyunResource;
import darabonba.core.client.ClientOverrideConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class SMSUtils {

    final static Logger logger = LoggerFactory.getLogger(SMSUtils.class);

    @Autowired
    public AliyunResource aliyunResource;

    public void sendSMS(String mobile, String code) {
        // 1. 获取阿里云账号认证信息
        String accessKeyId = aliyunResource.getAccessKeyId();
        String accessKeySecret = aliyunResource.getAccessKeySecret();

        // 2. 手动创建认证凭证
        Credential credential = Credential.builder()
                .accessKeyId(accessKeyId)
                .accessKeySecret(accessKeySecret)
                .build();

        // 3. 构建阿里云异步客户端
        try (AsyncClient client = AsyncClient.builder()
                .region("ap-southeast-1")
                .credentialsProvider(StaticCredentialProvider.create(credential))
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("dypnsapi.aliyuncs.com")
                )
                .build()) {

            // 4. 构造短信模板参数（动态验证码）
            String templateParam = String.format("{\"code\":\"%s\",\"min\":\"5\"}", code);

            // 5. 构造发送验证码请求
            SendSmsVerifyCodeRequest request = SendSmsVerifyCodeRequest.builder()
                    .phoneNumber(mobile)
                    .signName("速通互联验证码")
                    .templateCode("100001")
                    .templateParam(templateParam)
                    .build();

            // 6. 发送短信请求
            CompletableFuture<SendSmsVerifyCodeResponse> responseFuture = client.sendSmsVerifyCode(request);
            SendSmsVerifyCodeResponse response = responseFuture.get();

            // 7. 输出接口返回结果
            logger.info("阿里云短信接口返回结果：{}", new Gson().toJson(response));

        } catch (InterruptedException | ExecutionException e) {
            logger.error("短信发送失败，异常信息：", e);
        }
    }
}