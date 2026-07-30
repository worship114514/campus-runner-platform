package com.runner.utils.extend;

import com.runner.utils.extend.AliyunResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AliTextReviewUtils {

    final static Logger logger = LoggerFactory.getLogger(AliTextReviewUtils.class);

    @Autowired
    private AliyunResource aliyunResource;

    /**
     * 文本审核（模拟版本）
     * 生产环境请替换为真实的阿里云内容安全SDK调用
     *
     * 真实实现参考：
     * 1. 使用 aliyun-java-sdk-green 3.5.2
     * 2. 调用 TextScanRequest 接口
     * 3. 参考官方文档：https://help.aliyun.com/document_detail/53427.html
     */
    public String reviewTextContent(String content) {
        logger.info("========== 文本审核模拟 ==========");
        logger.info("待审核内容: {}", content);
        logger.info("==================================");

        // 模拟审核通过（实际应调用阿里云内容安全API）
        // 返回值说明：
        // "pass"   - 文本正常，审核通过
        // "review" - 需要人工审核
        // "block"  - 文本违规，审核不通过
        // null     - 审核失败
        return "pass";
    }

    /**
     * 文本审核增强版（模拟版本）
     */
    public String reviewTextContentStrong(String content) {
        return reviewTextContent(content);
    }
}