package com.nb6868.onexboot.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nb6868.onexboot.api.common.config.OnexProps;
import com.nb6868.onexboot.common.util.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

/**
 * Token测试
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TokenTest {

    @Autowired
    OnexProps onexProperties;

    @Test
    public void generateToken() {
        System.out.println("TokenPolicy=" + onexProperties.getLoginAdminProps().getSource());
    }

    @Test
    public void decode() {
        String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjo1LCJvcGVuaWQiOiJvZWoxdTVSdXVXX2hoZUs1UFNXT3d3UGxOOWNnIiwidHlwZSI6MiwiaWF0IjoxNjAwNzM1MTg5LCJleHAiOjE2MDA3NDIzODl9.1YDjgERqLNIWpSbv3h14RRLtxxVDEltIh4sOsoVtqYs";
        // jwt解析identityToken, 获取userIdentifier
        DecodedJWT jwt = JWT.decode(jwtToken);
        int user_id = jwt.getClaim("user_id").asInt();
        String openid = jwt.getClaim("openid").asString();
        int type = jwt.getClaim("type").asInt();
        long exp = jwt.getClaim("exp").asLong();
        Date expireTime = jwt.getExpiresAt();

        boolean isExpired = DateUtils.now().after(expireTime);
        System.out.println("header=" + jwt.getHeader());
        System.out.println("payload=" + jwt.getPayload());
        System.out.println("user_id=" + user_id);
        System.out.println("openid=" + openid);
        System.out.println("type=" + type);
        System.out.println("exp=" + exp);
        System.out.println("过期时间=" + expireTime);
        System.out.println("是否过期=" + isExpired);
    }

}
