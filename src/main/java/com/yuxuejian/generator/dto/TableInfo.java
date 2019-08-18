package com.yuxuejian.generator.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class TableInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String tableName;
	
	private String tableComment;
	
	private String entityName;
	
	public String getEntityName(String prefix,String tableName) {
		String entity = tableName.replace(prefix, "");
		return entity.substring(0,1).toUpperCase()+entity.substring(1);
	}
	
	public static void main(String[] args) {
		String a = "abcd";
		System.out.println(a.substring(0,1));
	}

}
