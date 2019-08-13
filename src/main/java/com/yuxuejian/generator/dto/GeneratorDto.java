package com.yuxuejian.generator.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class GeneratorDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostPort;

    private String database;

    private String username;

    private String password;

    private String[] tables;

    private String[] category;

    private String xmlPackage;

    private String entityPackage;

    private String mapperPackage;

    private String servicePackage;

    private String serviceImplPackage;

    private String webServicePackage;

    private String webServiceImplPackage;

    private String controllerPackage;
}
