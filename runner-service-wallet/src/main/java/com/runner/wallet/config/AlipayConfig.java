package com.runner.wallet.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig {
    // ========== 应用基本信息 ==========
    private String appId = "9021000164695198";

    // ========== 密钥信息 ==========
    private String privateKey = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCPRWdrbVVwyKKffC1g+s1PLZTcxSTKL3zkOKN+JVcJhCK+EGoQ3mxubyQsEs1lL8bmo8MuyXhQSYwBav59dPpgtlBsLUMSbUI/dxUX4OucUTnEBy8V0H8D/1dJOPvwO0nw/p3jA2lpaigZhuBVMHEAaWuuUy7QicCY2K8EFi6G0wny0KsoxggLkjZThnP27TbOfjOg85bcnfPW8UKle9lj76ZnQImvB7ellrYjdE5iyLHrMdO4EfI/limRpck57fDxr35YRL9L8ysn51iCTzAlb6i5X4kU7FRso3caFOQtYh/siikDNzn+LpMU5dOnxOWiAkNvcBJbQB0HnOhpPKL9AgMBAAECggEAY9remtwoGaRs27Vjt1z2LYI6Z6ir3JM+e54SLUB/Ki3DPS8ZjcpITqv9zQ4Jw8WJ3oQy7HAU//hSsUZOO0iFnVRUNyFQxw4Jh0xLEkp8TIW8Oik7ovufGz0jK/Cgf64C+gmsJ2XgWU1Yx3ne7uEFUWEoYOavET+BUcZsLO/825A3SnJdkGQDactCocTQ3foZNGoNLM9yBZeTEX5U9J3KbEppA1cawj36hMXNTSZH6thdzdWMU7Y00cObhH9PCaxF+/LU4DCsFsQGVyxZmtQeZa/rJeoWIQGTkfNQkR6u5cAfSOq2s3DpwiY1OqsLtLcjE8LZ1AlA+xwrdGmePvm4rQKBgQDwhMXuDBiYqt+01jo+zpvYYINwtZnf9uXF/7PYDuLOQGeGR7mk4P3ipWc5ynYV9EKas05zQM+wuXY6oBk2jGh+dhO+aZyBdvdceMAdvZyVutS7JDVevLls9kZGwn7Ub7xZnoDmVo9zu21uJXWTSTlCVA5KX9iB4oiBFH4+Ls8wVwKBgQCYfjOsqIq7j8430TOCpb+TfaYC6cFZeXsKa2T+SptbpHRibTeCk8IIgKVk8AcCDjO450lw1dmK8xeO/iHhn7iYsB5gX8spfCOqnh9oJcans0oCmzpAEquRPRpOfJd9lRZQcMMZVZFu+vz1sfAfjN+sjAkGpaMdgjAnnMIvkxBiywKBgFOuvnbuenhTq3Xtu2Iy3IJSQikq5hjIfHWBW/9mXn9IZTyRJAQ7y9nZlqrCyIcm7y2ICVmMSTbBsGMBZQRXoduuFXyAnlVlIPdpg95VJG/sCyv5kyAdJZ7FqPnhneMn5S6BOXPq1gp8Qby7B8dxCJ0NRdOWuWund7sBeFMGUkwNAoGARBU0ZQXqysrtuyX/5XizC9qxX+OVm6kNMh24vX/51PSbscYpXncxzjIBpgs0VcqZdRhuRZ50vgDCp+j2n5F8DU+0gDrErgTqt6ZZR1p2UzFM/dToMOslBndNY3Vk/YCuscNml80IXZBZmCiVmMCArLzo2rrhFK+naEgouqziR/kCgYBmOe3IqdSoE8YlPvMLl3iv09oRRAYfTzIGf4sbw15gR1+IBYuy+iwzzNwYY58qb30egfUQsG8xNj33Skfa8nD+gwbJzYgKYCI5accMe/zDXLSBZoV0fzW/eKvVwiyt1q8D3OsQ1Bllr5bFrYGfo9Hps6mKf3A+eS9nDllU1+gd2A==";

    private String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAimIQw8f6yNwaJdaX0T78hbKzM4SR5qZZUjKKabc8nkbXoXdiXUR4tP/TQ1GtpwIjjIQCVO0+gvBSzx4UiI6rmVYv5jT0ST6W1+K6QtnWZfa34a/oE/KGFuX6bGp5R8YB3toSKfhLdz1lfvv5ZpT91t2IpkSKSeTAb7it9SHB5ZklHACpzpZ0kPd8Dwmj2pbTpIi7IY9Es2dKmWMlnkYRcv7rKQJ9vYZ1x+c4/BJti+lZJni0bsbhXqgomEc4HhRzf0REox9v5AvZaflZd99D+M58PBZS/5g5wrSDAKNMMTw2Pi5gRbWzabi/j3gg+lODGXZqPOYGmGrPj8Lk3QH9TQIDAQAB";

    // ========== 网关及接口地址 ==========
    private String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";

    // ========== 通用参数 ==========
    private String charset = "UTF-8";
    private String format = "json";
    private String signType = "RSA2";

    // ========== 回调地址 ==========
    private String returnUrl = "http://runner.gzmu.com:9091/runner/portal/wallet.html";
    private String notifyUrl = "http://wallet.runner.gzmu.com:8007/wallet/alipay/notify";

    // ========== getter/setter ==========
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getAlipayPublicKey() {
        return alipayPublicKey;
    }

    public void setAlipayPublicKey(String alipayPublicKey) {
        this.alipayPublicKey = alipayPublicKey;
    }

    public String getGatewayUrl() {
        return gatewayUrl;
    }

    public void setGatewayUrl(String gatewayUrl) {
        this.gatewayUrl = gatewayUrl;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }
}