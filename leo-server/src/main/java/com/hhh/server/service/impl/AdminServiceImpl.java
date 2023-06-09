package com.hhh.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hhh.server.config.security.component.JwtTokenUtil;
import com.hhh.server.logger.LeoLog;
import com.hhh.server.mapper.AdminMapper;
import com.hhh.server.pojo.AdminRes;
import com.hhh.server.pojo.RespRes;
import com.hhh.server.service.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * AdminServiceImpl实现类
 *
 * @author hhh
 * @since 2022-01-19
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, AdminRes> implements IAdminService {

    private static final LeoLog log = LeoLog.getInstance();

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    /**
     * 登录之后返回token
     *
     * @param username
     * @param password
     * @param code
     * @param request
     * @return
     */
    @Override
    public RespRes login(String username, String password, String code, HttpServletRequest request) {
        log.info("AdminServiceImpl | login | headerNames = {}", request.getHeaderNames());
//        String captcha = (String) request.getSession().getAttribute("captcha");
//        if (captcha == null) {
//            log.info("请先获取验证码");
//            return RespRes.error("请先获取验证码");
//        }
//        if (StringUtils.isBlank(code) || !captcha.equalsIgnoreCase(code)) {
//            log.info("验证码输入错误, 请重新输入");
//            return RespRes.error("验证码输入错误, 请重新输入");
//        }
        // 登录
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (null == userDetails || !passwordEncoder.matches(password, userDetails.getPassword())) {
            log.info("用户名或密码不正确");
            return RespRes.error("用户名或密码不正确");
        }
        if (!userDetails.isEnabled()) {
            log.info("账号被禁用, 请联系管理员");
            return RespRes.error("账号被禁用, 请联系管理员");
        }
        // 更新security登录用户对象
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // 生成token
        String token = jwtTokenUtil.generateToken(userDetails);
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return RespRes.success("登录成功", tokenMap);
    }

    /**
     * 根据用户名获取用户
     *
     * @param username
     * @return
     */
    @Override
    public AdminRes getAdminByUserName(String username) {
        return adminMapper.selectOne(new QueryWrapper<AdminRes>().eq("username", username).eq("enabled", true));
    }

    /**
     * 更新用户密码
     *
     * @param oldPass
     * @param pass
     * @param adminId
     * @return
     */
    @Override
    public RespRes updateAdminPassword(String oldPass, String pass, Integer adminId) {
        AdminRes adminRes = adminMapper.selectById(adminId);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // 判断旧密码是否正确
        if (encoder.matches(oldPass, adminRes.getPassword())) {
            adminRes.setPassword(encoder.encode(pass));
            int result = adminMapper.updateById(adminRes);
            if (1 == result) {
                return RespRes.success("更新成功");
            }
        }
        return RespRes.error("更新失败");
    }

//  /**
//   * 更新用户头像
//   *
//   * @param url
//   * @param id
//   * @param authentication
//   * @return
//   */
//  @Override
//  public RespBean updateAdminUserFace(String url, Integer id, Authentication authentication) {
//    Admin admin = adminMapper.selectById(id);
//    admin.setUserFace(url);
//    int result = adminMapper.updateById(admin);
//    if (1 == result) {
//      Admin principal = (Admin) authentication.getPrincipal();
//      principal.setUserFace(url);
//      // 更新Authentication
//      SecurityContextHolder.getContext()
//          .setAuthentication(
//              new UsernamePasswordAuthenticationToken(
//                  admin, null, authentication.getAuthorities()));
//      return RespBean.success("更新成功!", url);
//    }
//    return RespBean.error("更新失败!");
//  }

}
