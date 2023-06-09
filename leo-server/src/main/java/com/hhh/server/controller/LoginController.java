package com.hhh.server.controller;

import com.hhh.server.logger.LeoLog;
import com.hhh.server.pojo.AdminRes;
import com.hhh.server.pojo.AdminLoginReq;
import com.hhh.server.pojo.RespRes;
import com.hhh.server.service.IAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * 登录
 *
 * @author HHH
 * @date 2022/1/21 @Version 1.0.0
 */
@Api(tags = "LoginController")
@RestController
@CrossOrigin
public class LoginController {

    private static final LeoLog log = LeoLog.getInstance();

    @Autowired
    private IAdminService adminService;

    @ApiOperation(value = "登录之后返回token")
    @PostMapping("/login")
    public RespRes login(@RequestBody AdminLoginReq adminLoginReq, HttpServletRequest request) {
        return adminService.login(
                adminLoginReq.getUsername(),
                adminLoginReq.getPassword(),
                adminLoginReq.getCode(),
                request);
    }

    @ApiOperation(value = "获取当前登录用户的信息")
    @GetMapping("/admin/info")
    public AdminRes getAdminInfo(Principal principal) {
        if (null == principal) {
            return null;
        }
        String username = principal.getName();
        log.info("LoginController | getAdminInfo | userName = {}", username);
        AdminRes adminRes = adminService.getAdminByUserName(username);
        adminRes.setPassword(null);
        return adminRes;
    }

    @ApiOperation(value = "退出登录")
    @PostMapping("/logout")
    public RespRes logout() {
        return RespRes.success("注销成功!");
    }

}
