package cn.hhh.server.common;

import java.io.Serializable;

/**
 * @Description BasicRes
 * @Author HHH
 * @Date 2023/7/19 16:39
 */
public class BasicRes<E> implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String code;

    protected String errorCode;

    protected String message;

    protected String summary;

    protected E var;

    public BasicRes() {
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public E getVar() {
        return this.var;
    }

    public void setVar(E var) {
        this.var = var;
    }

    public String toString() {
        return "BasicRes{code='" + this.code + '\'' + ", errorCode='" + this.errorCode + '\'' + ", message='" + this.message + '\'' + ", summary='" + this.summary + '\'' + ", var=" + this.var + '}';
    }

}
