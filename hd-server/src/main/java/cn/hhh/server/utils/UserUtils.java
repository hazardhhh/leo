package cn.hhh.server.utils;

import cn.hhh.server.entity.dto.UserDto;
import java.util.Objects;

/**
 * @Description UserUtils
 * @Author HHH
 * @Date 2023/7/19 22:50
 */
public class UserUtils {
    private static final Integer COMMON_USER_TYPE = 1;
    private static final Integer ADMIN_USER_TYPE = 0;

    private UserUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 判断是否是普通用户
     * @param user
     * @return {@link boolean}
     */
    public static boolean isCommon(UserDto user) {
        if(Objects.isNull(user.getUserType())){
            throw new IllegalArgumentException(" user type is null ");
        }

        return Objects.equals(COMMON_USER_TYPE, user.getUserType());
    }

    /**
     * 判断是否是管理员
     * @param user
     * @return {@link boolean}
     */
    public static boolean isAdmin(UserDto user) {
        if(Objects.isNull(user.getUserType())){
            throw new IllegalArgumentException(" user type is null ");
        }

        return Objects.equals(ADMIN_USER_TYPE, user.getUserType());
    }

}
