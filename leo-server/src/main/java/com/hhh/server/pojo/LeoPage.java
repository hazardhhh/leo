package com.hhh.server.pojo;

import java.io.Serializable;

/**
 * @Description LeoPage
 * @Author HHH
 * @Date 2023/5/30 18:10
 */
public class LeoPage implements Serializable {

    private static final long serialVersionUID = 1L;

    private static int defaultPageSize = 20;

    private Long totalCount;

    private Integer pageSize;

    private Integer pageNum;

    private Long startIndex;

    private Long endIndex;

    private Integer totalPageNum;

    public static void setDefaultPageSize(int defaultPageSize) {
        LeoPage.defaultPageSize = defaultPageSize;
    }

    public LeoPage() {
    }

    public LeoPage(Integer pageNum, Integer pageSize) {
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

    public void resetPage() {
        if (this.pageSize == null || this.pageSize < 0) {
            this.pageSize = defaultPageSize;
        }

        if (this.pageNum == null && this.startIndex != null) {
            this.pageNum = Math.toIntExact(1L + this.startIndex / (long)this.pageSize);
        }

        if (this.pageNum == null || this.pageNum <= 0) {
            this.pageNum = 1;
        }

        if (this.startIndex == null) {
            this.startIndex = (long)(this.pageNum - 1) * (long)this.pageSize;
        }

        if (this.endIndex == null) {
            this.endIndex = (long)this.pageNum * (long)this.pageSize;
        }

        if (this.startIndex < 0L) {
            this.startIndex = 0L;
        }

        if (this.endIndex < 0L) {
            this.endIndex = 0L;
        }

        if (this.totalCount == null || this.totalCount < 0L) {
            this.totalCount = 0L;
        }

        if (this.totalCount > 0L) {
            this.totalPageNum = calculatorTotalPage(this.totalCount, (long)this.pageSize);
            this.pageNum = resetPageNum(this.pageNum, this.totalPageNum);
            long currStartIndex = (long)(this.pageNum - 1) * (long)this.pageSize;
            long currEndIndex = (long)this.pageNum * (long)this.pageSize;
            currEndIndex = Math.min(this.totalCount, currEndIndex);
            this.startIndex = currStartIndex;
            this.endIndex = currEndIndex;
        } else {
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

}
