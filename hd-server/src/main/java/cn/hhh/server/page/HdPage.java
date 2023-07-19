package cn.hhh.server.page;

import cn.hhh.server.constant.ResultConst;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * @Description 基础列表类
 * @Author HHH
 * @Date 2023/7/19 22:57
 */
public class HdPage<T> extends BasicPageRes<List<T>> {

    public HdPage(Page<T> page) {
        this.code = ResultConst.S_OK.toString();
        this.page = new BasicPage();
        this.page.setPageNum((int) page.getCurrent());
        this.page.setPageSize((int) page.getSize());
        this.page.setTotalCount(page.getTotal());
        this.var = page.getRecords();
        this.page.setTotalPageNum((int) page.getPages());
    }

}
