package com.runner.api.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.runner.api.service.BaseService;
import com.runner.exception.GraceException;
import com.runner.grace.result.GraceJSONResult;
import com.runner.grace.result.ResponseStatusEnum;
import com.runner.pojo.AppUser;
import com.runner.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserActiveInterceptor extends BaseInterceptor implements HandlerInterceptor {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String userId = request.getHeader("headerUserId");
        if (StringUtils.isBlank(userId)) {
            GraceException.display(ResponseStatusEnum.UN_LOGIN);
            return false;
        }

        AppUser user = null;

        String userJson = redis.get(REDIS_USER_INFO + ":" + userId);
        if (StringUtils.isNotBlank(userJson)) {
            user = JsonUtils.jsonToPojo(userJson, AppUser.class);
        }

        if (user == null) {
            try {
                String url = "http://user.runner.gzmu.com:8003/user/getUserInfo?userId=" + userId;
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> entity = new HttpEntity<>(headers);
                ResponseEntity<GraceJSONResult> resultEntity =
                        restTemplate.postForEntity(url, entity, GraceJSONResult.class);

                GraceJSONResult result = resultEntity.getBody();
                if (result != null && result.getStatus() == 200) {
                    ObjectMapper mapper = new ObjectMapper();
                    user = mapper.convertValue(result.getData(), AppUser.class);
                    if (user != null) {
                        redis.set(REDIS_USER_INFO + ":" + userId, JsonUtils.objectToJson(user), 3600);
                    }
                }
            } catch (Exception e) {
                System.err.println("获取用户信息失败: " + e.getMessage());
            }
        }

        if (user == null) {
            GraceException.display(ResponseStatusEnum.UN_LOGIN);
            return false;
        }

        if (user.getActiveStatus() == null || user.getActiveStatus() != 1) {
            GraceException.display(ResponseStatusEnum.USER_INACTIVE_ERROR);
            return false;
        }

        return true;
    }
}