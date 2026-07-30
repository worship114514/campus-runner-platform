package com.runner.api.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.runner.api.service.BaseService;
import com.runner.pojo.AppUser;
import com.runner.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserTokenInterceptor extends BaseInterceptor implements HandlerInterceptor {

    final static Logger logger = LoggerFactory.getLogger(UserTokenInterceptor.class);

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");

        boolean run = verifyUserIdToken(userId, userToken, REDIS_USER_TOKEN);
        logger.info("用户会话拦截放行状态::: " + run);

        if (run && StringUtils.isNotBlank(userId)) {
            String userJson = redis.get(REDIS_USER_INFO + ":" + userId);
            if (StringUtils.isBlank(userJson)) {
                try {
                    // 改为 POST 调用
                    String url = "http://user.runner.gzmu.com:8003/user/getUserInfo?userId=" + userId;
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<String> entity = new HttpEntity<>(headers);
                    ResponseEntity<com.runner.grace.result.GraceJSONResult> resultEntity =
                            restTemplate.postForEntity(url, entity, com.runner.grace.result.GraceJSONResult.class);

                    com.runner.grace.result.GraceJSONResult result = resultEntity.getBody();
                    if (result != null && result.getStatus() == 200) {
                        ObjectMapper mapper = new ObjectMapper();
                        AppUser user = mapper.convertValue(result.getData(), AppUser.class);
                        if (user != null) {
                            redis.set(REDIS_USER_INFO + ":" + userId, JsonUtils.objectToJson(user), 3600);
                            logger.info("用户信息已缓存到Redis: " + userId);
                        }
                    }
                } catch (Exception e) {
                    logger.warn("缓存用户信息失败: " + e.getMessage());
                }
            }
        }

        return run;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}