package cn.hhh.server.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import cn.hhh.server.logger.HdLog;

import java.time.Instant;
import java.util.Date;

/**
 * @Description jwt工具类
 * @Author HHH
 * @Date 2023/7/19 17:25
 */
public class JwtUtils {

    private static final HdLog log = HdLog.getInstance();

    private JwtUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 定义JWT发布者
     */
    private static final String JWT_TOKEN_ISSUER = "hd";

    /**
     * 定义JWT有效时长 10分钟
     */
    private static final Integer TOKEN_VALIDITY_TIME = 5;

    /**
     * 定义 允许刷新JWT token的有效时长(在这个时长范围内 用户的jwt过期了,不用重新登录,重新颁发JWT token 给用户)
     */
    private static final Integer ALLOW_EXPIRES_TIME = 30;


    /**
     * 根据当前用户登录的时间戳生成动态私匙
     *
     * @param instant
     * @return
     */
    public static String getSecretKey(Instant instant) {
        return String.valueOf(instant.getEpochSecond());
    }


    public static String create(String secretKey, String subject, Instant instant) {
        return create(secretKey, subject, instant, TOKEN_VALIDITY_TIME);
    }

    /**
     * 生成token
     *
     * @param secretKey    秘匙
     * @param subject      JWT 中[payload]中自定义的内容
     * @param instant      用户登录的时间,也就是申请令牌的时间
     * @param vaildityTime 有效时长
     * @return
     */
    public static String create(String secretKey, String subject, Instant instant, Integer vaildityTime) {
        String token = "";
        Algorithm algorithm = null;
        try {
            algorithm = Algorithm.HMAC256(secretKey);
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
        }
        // 设置jwt token 有效时长
        Instant instant1 = instant.plusSeconds(60L * vaildityTime);
        // 生成token
        token = JWT.create().withIssuer(JWT_TOKEN_ISSUER)
                .withSubject(subject)
                .withIssuedAt(Date.from(instant))
                .withExpiresAt(Date.from(instant1)).sign(algorithm);
        log.info("create token : [{}]", token);
        return token;

    }

    /**
     * 字符串 token 解析为 jwtToken
     *
     * @param token
     * @return
     */
    public static DecodedJWT decoded(String token) {
        DecodedJWT decode = null;
        try {
            decode = JWT.decode(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decode;
    }

    /**
     * 校验token
     *
     * @param secretKey
     * @param token
     */
    public static void verify(String secretKey, String token) {
        log.info("verify token : [{}]", token);
        Algorithm algorithm = null;
        try {
            algorithm = Algorithm.HMAC256(secretKey);
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
        }
        JWTVerifier verifier = JWT.require(algorithm).withIssuer(JWT_TOKEN_ISSUER).build();
        verifier.verify(token);
    }

    /**
     * 刷新Token
     *
     * @param secretKey
     * @param jwtToken
     * @return
     */
    public static String getRefreshToken(String secretKey, DecodedJWT jwtToken) {
        return getRefreshToken(secretKey, jwtToken, TOKEN_VALIDITY_TIME);
    }

    /**
     * 重载的刷新Token
     *
     * @param secretKey
     * @param jwtToken
     * @param validityTime
     * @return
     */
    public static String getRefreshToken(String secretKey, DecodedJWT jwtToken, int validityTime) {
        return getRefreshToken(secretKey, jwtToken, validityTime, ALLOW_EXPIRES_TIME);
    }


    public static String getRefreshToken(String secretKey, DecodedJWT decodedJWT, Integer vaildityTime, Integer allowTime) {
        String token = "";
        // 新的时间
        Instant now = Instant.now();
        // 取出 token 有效时间
        Instant exp = decodedJWT.getExpiresAt().toInstant();
        //如果当前时间减去jwt有效期大于jwt申请的时间则不可以申请新的token了 需要重新登录,否则可以重新生成
        if ((now.getEpochSecond() - exp.getEpochSecond()) > allowTime * 60) {
            return null;
        }
        Algorithm algorithm = null;
        try {
            algorithm = Algorithm.HMAC256(secretKey);
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
        }
        // 在原有的JWT的过期时间的基础上加上次的有效期,得到新的JWT有效期时间
        Instant newExp = exp.plusSeconds(60L * vaildityTime);
        token = JWT.create().withIssuer(JWT_TOKEN_ISSUER)
                .withSubject(decodedJWT.getSubject())
                .withIssuedAt(Date.from(exp))
                .withExpiresAt(Date.from(newExp)).sign(algorithm);
        log.info("create refresh token : [{}] oldDate: [{}] newDate : [{}]", token, Date.from(exp), Date.from(newExp));
        return token;
    }

}
