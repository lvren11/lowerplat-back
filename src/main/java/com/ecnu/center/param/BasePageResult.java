package com.ecnu.center.param;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public class BasePageResult<T> {
    private List<T> data;
    private Long total;

    public BasePageResult(List<T> data, Long total) {
        this.data = data;
        this.total = total;
    }

    public static <E> BasePageResult<E> newInstance(IPage<E> page) {
        return new BasePageResult<E>(page.getRecords(), page.getTotal());
    }

    public List<T> getData() {
        return data;
    }

    public Long gettotal() {
        return total;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public void settotal(Long total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "BasePageResult{" +
                "data=" + data +
                ", total=" + total +
                '}';
    }
}
