package com.ecnu.center.param;

public class DatasourcePageParam extends BasePageParam{
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "DatasourcePageParam{" +
                "userId='" + userId + '\'' +
                '}' + super.toString();
    }
}
