package com.yuxuejian.generator.util;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.internal.DefaultCommentGenerator;

public class MyCommentGenerator extends DefaultCommentGenerator{
	@Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        super.addFieldComment(field, introspectedTable, introspectedColumn);
        if (introspectedColumn.getRemarks() != null && !"".equals(introspectedColumn.getRemarks())) {
            field.addJavaDocLine("/**");
            field.addJavaDocLine(" * " + introspectedColumn.getRemarks());
            addJavadocTag(field, false);
            field.addJavaDocLine(" */");
        }
    }
	
	@Override
	public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
		super.addClassComment(innerClass, introspectedTable);
		if (introspectedTable.getRemarks() != null && !"".equals(introspectedTable.getRemarks())) {
			innerClass.addJavaDocLine("/**");
			innerClass.addJavaDocLine(" * " + introspectedTable.getRemarks());
			innerClass.addJavaDocLine(" * @author Generator");
			innerClass.addJavaDocLine(" * @date " + getDateString());
			addJavadocTag(innerClass, false);
			innerClass.addJavaDocLine(" */");
		}
    }
}
