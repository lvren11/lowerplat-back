package com.ecnu.center.param;

public class BasePageParam {
    private Long pagenum;
    private Long pagesize;

    public Long getPagenum() {
        return pagenum;
    }

    public Long getPagesize() {
        return pagesize;
    }

    public void setPagenum(Long pagenum) {
        this.pagenum = pagenum;
    }

    public void setPagesize(Long pagesize) {
        this.pagesize = pagesize;
    }

    @Override
    public String toString() {
        return "BasePageParam{" +
                "pagenum=" + pagenum +
                ", pagesize=" + pagesize +
                '}';
    }
}
