package cn.hhh.server.constant;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Description 返回结果枚举
 * @Author HHH
 * @Date 2023/7/19 16:44
 */
public enum ResultConst {

    /**
     * 操作成功
     */
    S_OK("10000"),

    /**
     * 未登录
     */
    NO_LOGIN("10001"),

    /**
     * 用户名为空
     */
    PARAM_USERNAME_EMPTY("10002"),

    /**
     * 登录失败
     */
    LOGIN_FAIL("10003"),

    /**
     * 参数异常
     */
    PARAM_ERROR("10003"),

    /**
     * 用户名称已存在
     */
    USERNAME_EXISTS("10004"),

    /**
     * 用户不存在
     */
    USER_NOT_EXISTS("10013"),

    /**
     * 密码错误
     */
    PWD_ERROR("10014"),

    /**
     * 只能重置普通用户密码
     */
    ONLY_RESET_COMMON_USER_PWD("10015"),

    /**
     * 验证数据失败
     */
    VALIDATE_ERROR("12105"),

    /**
     * 未知异常
     */
    FS_UNKNOWN("13000");

    /**
     * code和errorCode的映射关系
     */
    private static final Map<String, String> CODE_MAP = new HashMap<>();

    /**
     * errorCode和code的映射关系
     */
    private static final Map<String, String> ERROR_CODE_MAP = new HashMap<>();

    /**
     * 错误数字编码
     */
    private final String errorCode;

    ResultConst(String errorCode) {
        this.errorCode = errorCode;
    }

    static {
        //初始化映射关系
        for (ResultConst e:ResultConst.values()) {
            CODE_MAP.put(e.name(),e.errorCode);
            ERROR_CODE_MAP.put(e.errorCode,e.name());
        }
    }

    //============== 静态方法 ===================

    /**
     * 根据WEb模块的code编码，转换为errorCode
     * @param code WEb模块的code编码
     * @return 统一错误码（errorCode）
     */
    public static String getErrorCodeByCode(String code){

        return CODE_MAP.getOrDefault(code, null);
    }

    //==================== 非静态方法 ==========================

    /**
     *  web模块前缀+错误编码
     * @return WEBxxxx
     */
    public String errorCode(){
        return this.errorCode;
    }

    /**
     * 判断CODE值是否是S_OK
     * @param code
     * @return
     */
    public static boolean haveOk(String code){
        return Objects.equals(S_OK.name(), code);
    }

}
