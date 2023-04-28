package com.hhh.server.controller;

import com.hhh.server.pojo.Admin;
import com.hhh.server.pojo.RespBean;
//import com.hhh.server.utils.FastDFSUtils;
import com.hhh.server.service.IAdminService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 个人中心
 *
 * @author HHH
 * @version 1.0.0
 * @date 2022/5/9 15:25
 */
@RestController
public class AdminInfoController {
    @Autowired
    private IAdminService adminService;

    @ApiOperation(value = "更新当前用户信息")
    @PutMapping("/admin/info")
    public RespBean updateAdmin(@RequestBody Admin admin, Authentication authentication) {
        // 更新成功，重新构建Authentication对象
        if (adminService.updateById(admin)) {
            // 1.用户对象 2.凭证（密码） 3.用户角色
            SecurityContextHolder.getContext()
                    .setAuthentication(
                            new UsernamePasswordAuthenticationToken(
                                    admin, null, authentication.getAuthorities()));
            return RespBean.success("更新成功!");
        }
        return RespBean.error("更新失败!");

    }

    @ApiOperation(value = "更新用户密码")
    @PutMapping("/admin/updatePwd")
    public RespBean updateAdminPassword(@RequestBody Map<String, Object> info) {
        String oldPass = (String) info.get("oldPass");
        String pass = (String) info.get("pass");
        Integer adminId = (Integer) info.get("adminId");
        return adminService.updateAdminPassword(oldPass, pass, adminId);

    }

//    @ApiOperation(value = "更新用户头像")
//    @PostMapping("/admin/userface")
//    public RespBean updateAdminUserFace(
//            MultipartFile file, Integer id, Authentication authentication) {
//        // 获取上传文件地址
//        String[] filePath = FastDFSUtils.upload(file);
//        String url = FastDFSUtils.getTrackerUrl() + filePath[0] + "/" + filePath[1];
//        return adminService.updateAdminUserFace(url, id, authentication);
//    }

}
