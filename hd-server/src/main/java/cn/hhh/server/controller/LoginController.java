package cn.hhh.server.controller;

import cn.hhh.server.constant.ResultConst;
import cn.hhh.server.entity.dto.UserDto;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cn.hhh.server.common.LoginResModel;
import cn.hhh.server.constant.CommonConst;
import cn.hhh.server.entity.vo.user.LoginVo;
import cn.hhh.server.logger.HdLog;
import cn.hhh.server.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录控制器
 *
 * @author HHH
 * @date 2022/1/21 @Version 1.0.0
 */
@Api(tags = "登录模块")
@RestController
public class LoginController {

    private static final HdLog log = HdLog.getInstance();

    @Resource
    private UserService userService;

    @ApiOperation("登录接口")
    @PostMapping("/login")
    @ApiImplicitParam(value = "请求流水id", name = "tid", paramType = "query", dataTypeClass = String.class)
    @ResponseBody
    public LoginResModel login(@Validated @RequestBody LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        QueryWrapper<UserDto> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UserDto::getUserName, loginVo.getUserName());
        UserDto user = userService.getOne(wrapper);
        if (user != null && user.getUserPwd().equals(loginVo.getUserPwd())) {
            log.info(" login success | userName={} ", user.getUserName());
            user.setUserPwd("");//把密码字段置空再返回

            // 生成Token
            String webToken = userService.createWebToken(user);
            user.setToken(webToken);

            Cookie cookie = new Cookie(CommonConst.USERID_COOKIE, user.getUserId().toString());
            response.addCookie(cookie);

            LoginResModel loginResModel = new LoginResModel();
            loginResModel.setCode(ResultConst.S_OK.name());
            loginResModel.setVar(user);

            return loginResModel;
        } else {
            log.info(" login fail | userName={} ", user == null ? "user is null" : user.getUserName());
            return LoginResModel.error(ResultConst.LOGIN_FAIL, ResultConst.LOGIN_FAIL.name());
        }

    }

}
