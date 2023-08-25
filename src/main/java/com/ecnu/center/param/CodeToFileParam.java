package com.ecnu.center.param;

import com.ecnu.center.entity.Basecodetofileparam;

import java.util.List;

public class CodeToFileParam {
    private String userId;
    private List<Basecodetofileparam> baseCodeToFileParamList;
    @Override
    public String toString() {
        return "CodeToFileParam{" +
                "useId='" + userId + '\'' +
                ", baseCodeToFileParamList=" + baseCodeToFileParamList +
                '}';
    }

    public String getUserId() {
        return userId;
    }

    public List<Basecodetofileparam> getBaseCodeToFileParamList() {
        return baseCodeToFileParamList;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setBaseCodeToFileParamList(List<Basecodetofileparam> baseCodeToFileParamList) {
        this.baseCodeToFileParamList = baseCodeToFileParamList;
    }

}
