package com.hhh.server.pojo;

/**
 * @Description BasicPageRes
 * @Author HHH
 * @Date 2023/5/30 18:07
 */
public class BasicPageRes<E> extends RespRes {

    protected LeoPage page;

    public BasicPageRes() {
    }

    public LeoPage getPage() {
        return this.page;
    }

    public void setPage(LeoPage page) {
        this.page = page;
    }

}
