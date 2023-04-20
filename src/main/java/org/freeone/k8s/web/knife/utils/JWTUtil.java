package org.freeone.k8s.web.knife.utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * jwt的工具
 * @来源 https://blog.csdn.net/q89757316/article/details/80693942
 */
//@Component
public class JWTUtil {

    private Logger logger = LoggerFactory.getLogger(JWTUtil.class);

    // 当前设置1天
    private static final Long EXPIRE_TIME = 1000L * 60L * 60L * 24L  * 1L;
    /**
     * 密钥
     */
    private static  String SECRET = "k8s-web-knife";

    @Value(value = "${jwt.secret:}")
    private String jwtSecret ;

    @PostConstruct
    public void init(){
        logger.info("jwt.secret : {}", StringUtils.isBlank(jwtSecret));
    }

    /**
     * 生成 token
     *
     * @param username 用户名
     * @return 加密的token
     */
    public static String createToken(String username) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        // 附带username信息
        return JWT.create()
                .withClaim("username", username)
                //到期时间
                .withExpiresAt(date)
                //创建一个新的JWT，并使用给定的算法进行标记
                .sign(algorithm);
    }

    /**
     * 校验 token 是否正确
     *
     * @param token    密钥
     * @param username 用户名
     * @return 是否正确
     */
    public static boolean verify(String token, String username) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            //在token中附带了username信息
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("username", username)
                    .build();
            //验证 token
            verifier.verify(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * 获得token中的信息，无需secret解密也能获得
     *
     * @return token中包含的用户名
     */
    public static String getUsername(String token) {
        try {
            if(token == null){
                return null;
            }
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /*public static void main(String[] args) {
        System.err.println(JWTUtil.verify(null, "admin"));
        System.err.println(JWTUtil.getUsername("1"));
        System.err.println(JWTUtil.verify("1",null));
    }
*/



}
