package com.runner.api.interceptors;

import com.runner.exception.GraceException;
import com.runner.grace.result.ResponseStatusEnum;
import com.runner.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseInterceptor {

    @Autowired
    public RedisOperator redis;

    public static final String REDIS_USER_TOKEN = "redis_user_token";
    public static final String REDIS_ADMIN_TOKEN = "redis_admin_token";
    public static final String REDIS_USER_INFO = "redis_user_info";
    public static final String REDIS_RUNNER_ACCEPTING = "redis_runner_accepting";

    public boolean verifyUserIdToken(String userId, String token, String redisKeyPrefix) {
        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(token)) {
            String uniqueToken = redis.get(redisKeyPrefix + ":" + userId);
            if (StringUtils.isBlank(uniqueToken)) {
                GraceException.display(ResponseStatusEnum.UN_LOGIN);
                return false;
            } else {
                if (!uniqueToken.equals(token)) {
                    GraceException.display(ResponseStatusEnum.TICKET_INVALID);
                    return false;
                }
            }
        } else {
            GraceException.display(ResponseStatusEnum.UN_LOGIN);
            return false;
        }
        return true;
    }
}