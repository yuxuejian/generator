package com.yuxuejian.generator.Service.impl;

import com.yuxuejian.generator.Service.GeneratorService;
import com.yuxuejian.generator.dto.GeneratorDto;
import com.yuxuejian.generator.dto.Result;
import com.yuxuejian.generator.util.JdbcUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeneratorServiceImpl implements GeneratorService {
    @Override
    public Result generator(GeneratorDto generatotDto) {
        Result result = validate(generatotDto);
        if (result.getSuccess() == 0) {
            return result;
        }
        boolean connect = JdbcUtil.isConnect(generatotDto.getHostPort(),generatotDto.getDatabase(), generatotDto.getUsername(), generatotDto.getPassword());
        if (!connect) {
            return new Result(0,"数据库连接失败，请检查数据库连接信息是否正确");
        }
        // 遍历表名
        Map<String, Object> tables = getTables2(generatotDto);
        String[] tablesNames = generatotDto.getTables();
        if (tablesNames == null) {
            return new Result(0,"至少选择一张表");
        }
        for (String table : tablesNames) {
            if (!tables.containsKey(table)) {
                return new Result(0,"表名【"+table+"】不存在");
            }
        }
        return new Result(1,"成功");
    }

    @Override
    public Result test(GeneratorDto generatorDto) {
        Result result1 = validate1(generatorDto);
        if (result1.getSuccess() != 1) {
            return result1;
        }
        boolean connect = JdbcUtil.isConnect(generatorDto.getHostPort(),generatorDto.getDatabase(), generatorDto.getUsername(), generatorDto.getPassword());
        if (!connect) {
            return new Result(0,"数据库连接失败，请检查数据库连接信息是否正确");
        }
        return new Result(1,"成功");
    }

    @Override
    public Result select(GeneratorDto generatotDto) {
        return getTables(generatotDto);
    }

    private Result getTables(GeneratorDto generatorDto) {
        Result result1 = validate1(generatorDto);
        if (result1.getSuccess() != 1) {
            return result1;
        }
        result1.setData(getTables1(generatorDto));
        return result1;
    }

    private List<Map<String, Object>> getTables1(GeneratorDto generatorDto) {
        JdbcUtil jdbcUtil = new JdbcUtil(generatorDto.getHostPort(), generatorDto.getDatabase(), generatorDto.getUsername(), generatorDto.getPassword());
        String sql = "SELECT table_name FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = '" + generatorDto.getDatabase() + "';";
        List<Map> result = null;
        try {
            result = jdbcUtil.selectByParams(sql, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> tables = new ArrayList<>();
        Map<String, Object> table = null;
        int num = 1;
        for (Map map : result) {
            table = new HashMap<>(1);
            String tableName = map.get("TABLE_NAME").toString();
            table.put("id", num++);
            table.put("tableName", tableName);
            tables.add(table);
        }
        jdbcUtil.release();
        return tables;
    }

    private Map<String, Object> getTables2(GeneratorDto generatorDto) {
        JdbcUtil jdbcUtil = new JdbcUtil(generatorDto.getHostPort(), generatorDto.getDatabase(), generatorDto.getUsername(), generatorDto.getPassword());
        String sql = "SELECT table_name FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = '" + generatorDto.getDatabase() + "';";
        List<Map> result = null;
        try {
            result = jdbcUtil.selectByParams(sql, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Map<String, Object> table = new HashMap<>();
        for (Map map : result) {
            String tableName = map.get("TABLE_NAME").toString();
            table.put("tableName", tableName);
        }
        jdbcUtil.release();
        return table;
    }

    private Result validate1(GeneratorDto generatotDto) {
        if (StringUtils.isEmpty(generatotDto.getHostPort())) {
            return new Result(0,"数据库地址不能为空");
        }
        if (StringUtils.isEmpty(generatotDto.getUsername())) {
            return new Result(0,"数据库账号不能为空");
        }
        if (StringUtils.isEmpty(generatotDto.getPassword())) {
            return new Result(0,"数据库密码不能为空");
        }
        if (StringUtils.isEmpty(generatotDto.getDatabase())) {
            return new Result(0,"数据库名称不能为空");
        }
        return new Result(1,"成功");
    }

    private Result validate(GeneratorDto generatotDto) {
        if (StringUtils.isEmpty(generatotDto.getHostPort())) {
            return new Result(0,"数据库地址不能为空");
        }
        if (StringUtils.isEmpty(generatotDto.getUsername())) {
            return new Result(0,"数据库账号不能为空");
        }
        if (StringUtils.isEmpty(generatotDto.getPassword())) {
            return new Result(0,"数据库密码不能为空");
        }
        if (StringUtils.isEmpty(generatotDto.getDatabase())) {
            return new Result(0,"数据库名称不能为空");
        }
        if (generatotDto.getCategory() ==  null || generatotDto.getCategory().length == 0) {
            return new Result(0,"请选择需要生产的文件");
        }
        return new Result(1,"成功");
    }
}
