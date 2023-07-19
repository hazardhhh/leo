package cn.hhh.server.mapper;

import cn.hhh.server.entity.dto.UserDto;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;

/**
 * @Description UserMapper
 * @Author HHH
 * @Date 2023/7/19 16:54
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDto> {

    @Select("SELECT * FROM hd_user WHERE user_id = #{id}")
    UserDto selectById(Integer id);

    @Select("SELECT user_name FROM hd_user WHERE user_id = #{id}")
    String selectNameById(Integer id);

    @Select(" SELECT ${ew.sqlSelect} FROM hd_user ${ew.customSqlSegment} ")
    @Results(id = "userLinkName", value = {
            @Result(property = "createUserName", column = "createUserId", jdbcType=JdbcType.VARCHAR, one = @One(select = "cn.hhh.server.mapper.UserMapper.selectNameById")),
            @Result(property = "lastModifyUserName", column = "lastModifyUserId", jdbcType=JdbcType.VARCHAR, one = @One(select = "cn.hhh.server.mapper.UserMapper.selectNameById")),
            @Result(property = "createUserId", column = "createUserId"),
            @Result(property = "lastModifyUserId", column = "lastModifyUserId")
    })
    List<UserDto> listUser(@Param(Constants.WRAPPER) AbstractWrapper<?,?,?> wrapper);

    @Select(" SELECT ${ew.sqlSelect} FROM hd_user ${ew.customSqlSegment} ")
    @ResultMap(value="userLinkName")
    Page<UserDto> pageUser(Page<?> page, @Param(Constants.WRAPPER) AbstractWrapper<?,?,?> wrapper);

}
