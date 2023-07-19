package cn.hhh.server.utils;

import cn.hhh.server.constant.CommonConst;
import cn.hhh.server.entity.dto.UserDto;
import cn.hhh.server.logger.HdLog;
import cn.hhh.server.mapper.UserMapper;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Description 用户校验工具类
 * @Author HHH
 * @Date 2023/7/19 16:51
 */
@Service
public class UserSecurityUtils extends ServiceImpl<UserMapper, UserDto> {

    private static final HdLog log = HdLog.getInstance();

    @Value("${user.login-token-time:10}")
    private Integer loginTokenTime = 10;

    @Value("${user.login-allow-time:30}")
    private Integer loginAllowTime = 30;

    /**
     * 校验token
     *
     * @param request
     * @param response
     * @return {@link boolean}
     */
    public boolean verifyWebToken(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader(CommonConst.TOKEN_HEADER);
        if (StringUtils.isEmpty(token)) {
            return false;
        }

        DecodedJWT decoded = JwtUtils.decoded(token);
        if (decoded == null) {
            return false;
        }

        // 从jwt payload中取出 subject 转换成 userId
        String userId = decoded.getSubject();

        //设置到请求中
        request.setAttribute(CommonConst.USERID_ATTR, userId);

        UserDto user = lambdaQuery().eq(UserDto::getUserId, userId).one();

        String secretKey = user.getUserId().toString();
        try {
            JwtUtils.verify(secretKey, token);
        } catch (SignatureVerificationException e) {
            log.error("无效签名！ 错误 ->", e);
            return false;

        } catch (TokenExpiredException e) {
            // 允许一段时间有效时间同时返回新的token
            String newToken = JwtUtils.getRefreshToken(secretKey, decoded, loginTokenTime, loginAllowTime);
            if (StringUtils.isEmpty(newToken)) {
                log.error(e.getMessage());
                return false;
            }
            log.debug("Subject : [" + userId + "] token expired, allow get refresh token [" + newToken + "]");
            response.setHeader(CommonConst.TOKEN_HEADER, newToken);

        } catch (Exception e) {
            log.error("UserSecurityUtils verifyWebToken error", e);
            return false;

        }
        return true;

    }

}
