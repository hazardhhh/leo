package com.hhh.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hhh.server.pojo.Admin;
import com.hhh.server.pojo.RespBean;
import javax.servlet.http.HttpServletRequest;

/**
 * 服务类
 *
 * @author hhh
 * @since 2022-01-19
 */
public interface IAdminService extends IService<Admin> {
  /**
   * 登录之后返回token
   *
   * @param username
   * @param password
   * @param code
   * @return
   */
  RespBean login(String username, String password, String code, HttpServletRequest request);

  /**
   * 根据用户名获取用户
   *
   * @param username
   * @return
   */
  Admin getAdminByUserName(String username);

  /**
   * 更新用户密码
   *
   * @param oldPass
   * @param pass
   * @param adminId
   * @return
   */
  RespBean updateAdminPassword(String oldPass, String pass, Integer adminId);

//  /**
//   * 更新用户头像
//   *
//   * @param url
//   * @param id
//   * @param authentication
//   * @return
//   */
//  RespBean updateAdminUserFace(String url, Integer id, Authentication authentication);

}
