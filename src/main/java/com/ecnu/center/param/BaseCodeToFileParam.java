package com.ecnu.center.param;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BaseCodeToFileParam {

    private String TbNames;
    private String TbPrefix;
    private String Author;
    private boolean Isswagger;
    private String IdType;
    private String PackageName;
    private String EntityName;
    private String MapperName;
    private String ServiceName;
    private String ServiceImplName;
    private String ControllerName;
    private boolean IsRestController;
    private boolean IsLombook;

    @Override
    public String toString() {
        return "BaseCodeToFileParam{" +
                "TbNames='" + TbNames + '\'' +
                ", TbPrefix='" + TbPrefix + '\'' +
                ", Author='" + Author + '\'' +
                ", Isswagger=" + Isswagger +
                ", IdType='" + IdType + '\'' +
                ", PackageName='" + PackageName + '\'' +
                ", EntityName='" + EntityName + '\'' +
                ", MapperName='" + MapperName + '\'' +
                ", ServiceName='" + ServiceName + '\'' +
                ", ServiceImplName='" + ServiceImplName + '\'' +
                ", ControllerName='" + ControllerName + '\'' +
                ", IsRestController=" + IsRestController +
                ", IsLombook=" + IsLombook +
                '}';
    }

    public void setTbPrefix(String tbPrefix) {
        TbPrefix = tbPrefix;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public void setIsswagger(boolean isswagger) {
        Isswagger = isswagger;
    }

    public void setIdType(String idType) {
        IdType = idType;
    }

    public void setPackageName(String packageName) {
        PackageName = packageName;
    }

    public void setEntityName(String entityName) {
        EntityName = entityName;
    }

    public void setMapperName(String mapperName) {
        MapperName = mapperName;
    }

    public void setServiceName(String serviceName) {
        ServiceName = serviceName;
    }

    public void setServiceImplName(String serviceImplName) {
        ServiceImplName = serviceImplName;
    }

    public void setControllerName(String controllerName) {
        ControllerName = controllerName;
    }

    public void setRestController(boolean restController) {
        IsRestController = restController;
    }

    public void setLombook(boolean lombook) {
        IsLombook = lombook;
    }

    public void setTbNames(String tbNames) {
        TbNames = tbNames;
    }

    public String getTbNames() {
        return TbNames;
    }

    public String getTbPrefix() {
        return TbPrefix;
    }

    public String getAuthor() {
        return Author;
    }

    public boolean isIsswagger() {
        return Isswagger;
    }

    public String getIdType() {
        return IdType;
    }

    public String getPackageName() {
        return PackageName;
    }

    public String getEntityName() {
        return EntityName;
    }

    public String getMapperName() {
        return MapperName;
    }

    public String getServiceName() {
        return ServiceName;
    }

    public String getServiceImplName() {
        return ServiceImplName;
    }

    public String getControllerName() {
        return ControllerName;
    }

    public boolean isRestController() {
        return IsRestController;
    }

    public boolean isLombook() {
        return IsLombook;
    }
}
