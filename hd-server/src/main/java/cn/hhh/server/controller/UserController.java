package cn.hhh.server.controller;

import cn.hhh.server.common.BasicServiceModel;
import cn.hhh.server.page.HdPage;
import cn.hhh.server.page.UserReqPage;
import cn.hhh.server.constant.CommonConst;
import cn.hhh.server.constant.ResultConst;
import cn.hhh.server.entity.dto.UserDto;
import cn.hhh.server.entity.vo.user.DeleteUserVo;
import cn.hhh.server.entity.vo.user.ResetUserPwdVo;
import cn.hhh.server.entity.vo.user.UpdateUserPwdVo;
import cn.hhh.server.logger.HdLog;
import cn.hhh.server.service.UserService;
import cn.hhh.server.utils.UserUtils;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Objects;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description 用户控制器
 * @Author HHH
 * @Date 2023/7/20 5:44
 */
@Api(tags = "用户模块")
@RestController
@RequestMapping("/user")
public class UserController {

    private static final HdLog log = HdLog.getInstance();

    @Resource
    private UserService userService;

    /**
     * 查询用户列表
     *
     * @param userReqPage
     * @return {@link HdPage}
     */
    @ApiOperation("查询用户列表")
    @PostMapping("/list")
    @ApiImplicitParam(value = "请求流水id", name = "tid", paramType = "query", dataTypeClass = String.class)
    @ResponseBody
    public HdPage<UserDto> list(@RequestBody UserReqPage<UserDto> userReqPage){
        QueryWrapper<UserDto> wrapper = new QueryWrapper<>();
        //排除掉密码字段
        wrapper.select(UserDto.class, info -> !"user_pwd".equals(info.getColumn()));

        //模糊查询用户名称
        if (StringUtils.isNotEmpty(userReqPage.getUserName())) {
            wrapper.lambda().like(UserDto::getUserName, userReqPage.getUserName());
        }

        Page<UserDto> pageList = new Page<>();
        if(Objects.isNull(userReqPage.getPageNum()) || Objects.isNull(userReqPage.getPageSize())){
            List<UserDto> list = userService.listUser(wrapper);
            pageList.setRecords(list);
        }else {
            pageList = userService.pageUser(userReqPage.getPage(), wrapper);
        }

        log.info( "user list size | size={}", pageList.getRecords().size() );
        return new HdPage<>(pageList);
    }

    /**
     * 新增用户
     *
     * @param user
     * @return {@link BasicServiceModel}
     */
    @ApiOperation("新增用户")
    @PostMapping("/add")
    @ApiImplicitParam(value = "请求流水id", name = "tid", paramType = "query", dataTypeClass = String.class)
    @ResponseBody
    public BasicServiceModel<Object> add(@Validated @RequestBody UserDto user){

        boolean save = false;
        try {
            QueryWrapper<UserDto> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(UserDto::getUserName, user.getUserName());
            long count = userService.count(queryWrapper);
            if(count > 0){
                log.warn("user name exists | user name = {}", user.getUserName());
                return BasicServiceModel.error(ResultConst.USERNAME_EXISTS, ResultConst.USERNAME_EXISTS.name());
            }

            save = userService.save(user);
        } catch (Exception e) {
            log.error("save user error | user name = {}", user.getUserName(), e);
        }

        if(save){
            log.info(" save user success | user name = {}", user.getUserName());
            return BasicServiceModel.ok(null);
        } else {
            log.warn("save user fail | user name = {}", user.getUserName());
            return BasicServiceModel.error(ResultConst.FS_UNKNOWN, ResultConst.FS_UNKNOWN.name());
        }

    }

    /**
     * 修改密码
     *
     * @param userVo
     * @return {@link BasicServiceModel}
     */
    @ApiOperation("修改密码")
    @PostMapping("/updatePwd")
    @ApiImplicitParam(value = "请求流水id", name = "tid", paramType = "query", dataTypeClass = String.class)
    @ResponseBody
    public BasicServiceModel<Object> updatePwd(@Validated @RequestBody UpdateUserPwdVo userVo, HttpServletRequest request){

        String oldPwd = userVo.getOldPwd();

        UserDto userDto = userVo.toUserDto();

        boolean success = false;
        try {

            UserDto userDtoCheck = userService.getById(userVo.getUserId());
            if(Objects.isNull(userDtoCheck)){
                log.warn("user not exists | user id = {}", userVo.getUserId());
                return BasicServiceModel.error(ResultConst.USER_NOT_EXISTS, ResultConst.USER_NOT_EXISTS.name());
            }

            if(!Objects.equals(userDtoCheck.getUserPwd(), oldPwd)){
                log.warn("old pwd error | user id = {}", userVo.getUserId());
                return BasicServiceModel.error(ResultConst.PWD_ERROR, ResultConst.PWD_ERROR.name());
            }

            final String userid = (String) request.getAttribute(CommonConst.USERID_ATTR);
            if(!Objects.equals(userid, userVo.getUserId().toString())){
                log.warn("only update self pwd | user id = {} | headerUserid = {}", userVo.getUserId(), userid);
                return BasicServiceModel.error(ResultConst.PWD_ERROR, ResultConst.PWD_ERROR.name());
            }

            success = userService.updateById(userDto);
        } catch (Exception e) {
            log.error("update user pwd error | user id = {}", userVo.getUserId(), e);
        }

        if(success){
            log.info("update user pwd success | user id = {}", userVo.getUserId());
            return BasicServiceModel.ok(null);
        } else {
            log.warn("update user pwd fail | user id = {}", userVo.getUserId());
            return BasicServiceModel.error(ResultConst.FS_UNKNOWN, ResultConst.FS_UNKNOWN.name());
        }

    }

    /**
     * 重置密码
     *
     * @param userVo
     * @return {@link BasicServiceModel}
     */
    @ApiOperation("重置密码")
    @PostMapping("/resetPwd")
    @ApiImplicitParam(value = "请求流水id", name = "tid", paramType = "query", dataTypeClass = String.class)
    @ResponseBody
    public BasicServiceModel<Object> resetPwd(@Validated @RequestBody ResetUserPwdVo userVo){
        UserDto userDto = userVo.toUserDto();

        boolean success = false;
        try {
            UserDto userDtoCheck = userService.getById(userDto.getUserId());
            if(Objects.isNull(userDtoCheck)){
                log.warn("user not exists | user id = {}", userVo.getUserId());
                return BasicServiceModel.error(ResultConst.USER_NOT_EXISTS, ResultConst.USER_NOT_EXISTS.name());
            }

            if(!UserUtils.isCommon(userDtoCheck)){
                log.warn("user is not common user | user id = {}", userVo.getUserId());
                return BasicServiceModel.error(ResultConst.ONLY_RESET_COMMON_USER_PWD, ResultConst.ONLY_RESET_COMMON_USER_PWD.name());
            }


            success = userService.updateById(userDto);
        } catch (Exception e) {
            log.error("reset user pwd error | user id = {}", userVo.getUserId(), e);
        }

        if(success){
            log.info("reset user pwd success | user id = {}", userVo.getUserId());
            return BasicServiceModel.ok(null);
        } else {
            log.warn("reset user pwd fail | user id = {}", userVo.getUserId());
            return BasicServiceModel.error(ResultConst.FS_UNKNOWN, ResultConst.FS_UNKNOWN.name());
        }

    }

    /**
     * 删除用户
     *
     * @param deleteUserVo
     * @return {@link BasicServiceModel}
     */
    @ApiOperation("删除用户")
    @PostMapping("/delete")
    @ApiImplicitParam(value = "请求流水id", name = "tid", paramType = "query", dataTypeClass = String.class)
    @ResponseBody
    public BasicServiceModel<Object> delete(@Validated @RequestBody DeleteUserVo deleteUserVo){
        boolean success = false;
        List<Long> idList = deleteUserVo.getList();

        try {
            success = userService.removeBatchByIds(idList);
        } catch (Exception e) {
            log.error("delete user error | user ids = {}", JSON.toJSON(idList), e);
        }

        if(success){
            log.info("delete user success | user ids = {}", JSON.toJSON(idList));
            return BasicServiceModel.ok(null);
        } else {
            log.warn("delete user fail | user ids = {}", JSON.toJSON(idList));
            return BasicServiceModel.error(ResultConst.FS_UNKNOWN, ResultConst.FS_UNKNOWN.name());
        }

    }

    /**
     * 导出用户列表
     *
     * @param userName
     * @return {@link ResponseEntity <byte[]>}
     */
    @ApiOperation("导出用户列表")
    @GetMapping("/export")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "请求流水id", name = "tid", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(value = "用户名称", name = "userName", paramType = "query", dataTypeClass = String.class)
    })
    public void export(@RequestParam(value = "userName", required = false) String userName, HttpServletResponse response){
        try {
            log.info("export start | userName = {}", userName);
            userService.export(userName, response);
        } catch (Exception e) {
            log.error("export error | userName = {}", userName, e);
        }

    }

}
