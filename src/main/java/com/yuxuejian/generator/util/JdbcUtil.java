package com.yuxuejian.generator.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcUtil {
    // 定义数据库的链接
    private Connection conn;
    // 定义sql语句的执行对象
    private PreparedStatement pstmt;
    // 定义查询返回的结果集合
    private ResultSet rs;

    // 初始化
    public JdbcUtil(String hostPort, String database, String username, String password) {
        StringBuilder url = new StringBuilder("jdbc:mysql://");
        url.append(hostPort).append("/").append(database).append("?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=true&nullCatalogMeansCurrent=true");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url.toString(), username, password);
            System.out.println("数据库连接成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 查询多条记录
    public List<Map> selectByParams(String sql, List params) throws SQLException {
        List<Map> list = new ArrayList<>();
        int index = 1;
        pstmt = conn.prepareStatement(sql);
        if (null != params && !params.isEmpty()) {
            for (int i = 0; i < params.size(); i ++) {
                pstmt.setObject(index++, params.get(i));
            }
        }
        rs = pstmt.executeQuery();
        ResultSetMetaData metaData = rs.getMetaData();
        int colsLen = metaData.getColumnCount();
        while (rs.next()) {
            Map map = new HashMap(colsLen);
            for (int i = 0; i < colsLen; i ++) {
                String columnName = metaData.getColumnName(i + 1);
                Object columnValue = rs.getObject(columnName);
                if (null == columnValue) {
                    columnValue = "";
                }
                map.put(columnName, columnValue);
            }
            list.add(map);
        }
        return list;
    }

    // 释放连接
    public void release() {
        try {
            if (null != rs) {
                rs.close();
            }
            if (null != pstmt) {
                pstmt.close();
            }
            if (null != conn) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("释放数据库连接");
    }

    public static boolean isConnect(String ipPort, String database, String username, String password) {
        StringBuilder url = new StringBuilder("jdbc:mysql://");
        url.append(ipPort).append("/").append(database).append("?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=true&nullCatalogMeansCurrent=true");
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url.toString(), username, password);
            System.out.println("数据库连接成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
