package cn.hhh.server.service.impl;

import cn.hhh.server.entity.dto.UserDto;
import cn.hhh.server.mapper.UserMapper;
import cn.hhh.server.service.UserService;
import cn.hhh.server.utils.FileUtils;
import cn.hhh.server.utils.JwtUtils;
import cn.hhh.server.utils.ServletUtils;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Description UserServiceImpl
 * @Author HHH
 * @Date 2023/7/19 17:24
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDto> implements UserService {

    @Value("${user.login-token-time:10}")
    private Integer loginTokenTime = 10;

    @Override
    public List<UserDto> listUser(AbstractWrapper<?, ?, ?> wrapper) {
        return baseMapper.listUser(wrapper);
    }

    @Override
    public Page<UserDto> pageUser(Page<?> page, AbstractWrapper<?, ?, ?> wrapper) {
        return baseMapper.pageUser(page, wrapper);
    }

    private static final String PREFIX_EXCEL_FILE_NAME = "UserList-";

    @Override
    public UserDto getById(Serializable id) {
        return lambdaQuery().eq(UserDto::getUserId, id).one();
    }

    @Override
    public void export(String userName, HttpServletResponse response) {
        QueryWrapper<UserDto> wrapper = new QueryWrapper<>();
        wrapper.select(UserDto.class, info -> true);

        //模糊查询用户名称
        if (StringUtils.isNotEmpty(userName)) {
            wrapper.lambda().like(UserDto::getUserName, userName);
        }

        List<UserDto> userDtoList = this.listUser(wrapper);

        //输出Excel内容
        ServletUtils.outputExcel(response, userDtoList, UserDto.class, FileUtils.generateExcelFileName(PREFIX_EXCEL_FILE_NAME));

    }

    @Override
    public String createWebToken(UserDto user) {
        Instant now = Instant.now();
        String token = JwtUtils.create(user.getUserId().toString(), user.getUserId().toString(), now, loginTokenTime);

        //更新最后登录时间
        this.lambdaUpdate().set(UserDto::getLastLoginTime, LocalDateTime.ofInstant(now, ZoneId.systemDefault())).eq(UserDto::getUserId, user.getUserId()).update();

        return token;
    }

}
