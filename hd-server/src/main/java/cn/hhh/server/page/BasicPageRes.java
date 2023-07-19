package cn.hhh.server.page;

import cn.hhh.server.common.BasicRes;

/**
 * @Description BasicPageRes
 * @Author HHH
 * @Date 2023/5/30 18:07
 */
public class BasicPageRes<E> extends BasicRes<E> {

    private static final long serialVersionUID = 1L;

    protected BasicPage page;

    public BasicPageRes() {
    }

    public BasicPage getPage() {
        return this.page;
    }

    public void setPage(BasicPage page) {
        this.page = page;
    }

    public String toString() {
        return "BasicPageRes{page=" + this.page + ", code='" + this.code + '\'' + ", errorCode='" + this.errorCode + '\'' + ", message='" + this.message + '\'' + ", summary='" + this.summary + '\'' + ", var=" + this.var + '}';
    }

}
