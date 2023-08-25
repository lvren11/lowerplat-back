package com.ecnu.center.param;

public class DatatablePageParam extends BasePageParam {
    private String userId;
    private String datasourceId;

    public String getUserId() {
        return userId;
    }

    public void setDatasourceId(String datasourceId) {
        this.datasourceId = datasourceId;
    }

    public String getDatasourceId() {
        return datasourceId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "DatatablePageParam{" +
                "userId='" + userId + '\'' +
                ", datasourceId='" + datasourceId + '\'' +
                '}' + super.toString();
    }
}
