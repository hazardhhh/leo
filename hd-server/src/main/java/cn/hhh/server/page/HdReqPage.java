package cn.hhh.server.page;

import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description HdReqPage
 * @Author HHH
 * @Date 2023/7/20 5:55
 */
public class HdReqPage<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 默认的每页行数
     */
    private static int defaultPageSize = 20;

    /**
     * 排序的字段，多个逗号分割
     */
    private String order;

    /**
     * 排序方式 true 升序  false 降序
     */
    private Boolean asc;

    /**
     * 总行数
     */
    private Long totalCount;

    /**
     * 每页行数
     */
    @ApiModelProperty(value = "每页行数", example = "10", required = false)
    private Integer pageSize;

    /**
     * 页码
     */
    @ApiModelProperty(value = "页码", example = "1", required = false)
    private Integer pageNum;

    /**
     * 当前页开始下标
     */
    private Long startIndex;

    /**
     * 当前页结束下标
     */
    private Long endIndex;

    /**
     * 总页数
     */
    private Integer totalPageNum;

    public static void setDefaultPageSize(int defaultPageSize) {
        HdReqPage.defaultPageSize = defaultPageSize;
    }

    public HdReqPage() {
    }

    public HdReqPage(Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public Long getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNum() {
        return this.pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Long getStartIndex() {
        return this.startIndex;
    }

    public void setStartIndex(Long startIndex) {
        this.startIndex = startIndex;
    }

    public Long getEndIndex() {
        return this.endIndex;
    }

    public void setEndIndex(Long endIndex) {
        this.endIndex = endIndex;
    }

    public Integer getTotalPageNum() {
        return this.totalPageNum;
    }

    public void setTotalPageNum(Integer totalPageNum) {
        this.totalPageNum = totalPageNum;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Boolean getAsc() {
        return asc;
    }

    public void setAsc(Boolean asc) {
        this.asc = asc;
    }

    /**
     * 构造mybatisPlus封装的Page对象
     *
     * @return
     */
    public Page<T> getPage() {
        Page<T> page = new Page<>();
        page.setCurrent(this.getPageNum());
        page.setSize(this.getPageSize());
        // 多排序字段处理
        if (StringUtils.isNotBlank(this.order)) {
            List<OrderItem> orders = new ArrayList<>();
            List<String> orderList = ListUtil.toList(this.order.split(","));
            for (String str : orderList) {
                OrderItem orderItem = new OrderItem();
                orderItem.setColumn(str);
                orderItem.setAsc(Objects.nonNull(this.asc) ? this.asc : Boolean.FALSE);
                orders.add(orderItem);
            }
            page.setOrders(orders);
        }
        return page;
    }

    public void resetPage() {
        if (this.pageNum == null || this.pageNum <= 0) {
            this.pageNum = 1;
        }

        if (this.pageSize == null || this.pageSize < 0) {
            this.pageSize = defaultPageSize;
        }

        if (this.totalCount == null || this.totalCount < 0L) {
            this.totalCount = 0L;
        }

        if (this.totalCount > 0L) {
            this.totalPageNum = calculatorTotalPage(this.totalCount, (long) this.pageSize);
            this.pageNum = resetPageNum(this.pageNum, this.totalPageNum);
            this.startIndex = (long) (this.pageNum - 1) * this.pageSize;
            this.endIndex = Math.min(this.totalCount, (this.pageNum * this.pageSize));
        } else {
            this.startIndex = 0L;
            this.endIndex = 0L;
            this.totalPageNum = 0;
        }

    }

    public static int resetPageNum(int pageNum, int totalPageNum) {
        return pageNum > 0 && totalPageNum > 0 ? Math.min(pageNum, totalPageNum) : 1;
    }

    public static int calculatorTotalPage(long totalCount, long pageSize) {
        if (totalCount != 0L && pageSize != 0L) {
            int totalPage = (int)(totalCount % pageSize > 0L ? totalCount / pageSize + 1L : totalCount / pageSize);
            return Math.max(totalPage, 1);
        } else {
            return 1;
        }
    }

    public String toString() {
        return "HdPage{totalCount=" + this.totalCount + ", pageSize=" + this.pageSize + ", pageNum=" + this.pageNum + ", startIndex=" + this.startIndex + ", endIndex=" + this.endIndex + ", totalPageNum=" + this.totalPageNum + '}';
    }

}
