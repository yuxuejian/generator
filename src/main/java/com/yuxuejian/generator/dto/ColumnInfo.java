package com.yuxuejian.generator.dto;

import com.yuxuejian.generator.util.DataTypeUtil;
import lombok.Data;

import java.io.Serializable;

@Data
public class ColumnInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String databaseColumnName;
    private String columnName;
    private String columnName1;

    private String databaseIsNullable;
    private boolean isNullable;

    private String databaseDataType;
    private String dataType;

    private String databaseColumnComment;
    private String columnComment;

    public void setDatabaseColumnName(String databaseColumnName) {
        this.databaseColumnName = databaseColumnName;
        String[] a = databaseColumnName.split("_");
        StringBuilder tmp = new StringBuilder();
        for (String b : a) {
            tmp.append(b.substring(0, 1).toUpperCase()).append(b.substring(1, b.length()));
        }
        this.columnName = tmp.toString();
        this.columnName1 = tmp.substring(0,1).toLowerCase()+tmp.substring(1,tmp.length());
    }

    public void setDatabaseIsNullable(String databaseIsNullable) {
        this.databaseIsNullable = databaseIsNullable;
        if ("YES".equals(databaseIsNullable) || "yes".equals(databaseIsNullable)) {
            this.isNullable = true;
        } else {
            this.isNullable = false;
        }
    }

    public void setDatabaseDataType(String databaseDataType) {
        this.databaseDataType = databaseDataType;
        this.dataType = DataTypeUtil.dataTypes.get(databaseDataType);
    }

    public void setDatabaseColumnComment (String databaseColumnComment) {
        this.databaseColumnComment = databaseColumnComment;
        this.columnComment = databaseColumnComment;
    }

}
