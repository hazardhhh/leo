package cn.hhh.server.service;

import cn.hhh.server.entity.dto.UserDto;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description UserService
 * @Author HHH
 * @Date 2023/7/19 17:22
 */
public interface UserService extends IService<UserDto> {

    List<UserDto> listUser(AbstractWrapper<?,?,?> wrapper);

    Page<UserDto> pageUser(Page<?> page, AbstractWrapper<?,?,?> wrapper);

    void export(String userName, HttpServletResponse response);

    String createWebToken(UserDto user);

}
