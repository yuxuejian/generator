package com.yuxuejian.generator.Service.impl;

import com.yuxuejian.generator.Service.GeneratorService;
import com.yuxuejian.generator.dto.ColumnInfo;
import com.yuxuejian.generator.dto.GeneratorDto;
import com.yuxuejian.generator.dto.Result;
import com.yuxuejian.generator.dto.TableInfo;
import com.yuxuejian.generator.util.AESUtil;
import com.yuxuejian.generator.util.FileUtil;
import com.yuxuejian.generator.util.GeneratorConfig;
import com.yuxuejian.generator.util.JdbcUtil;
import com.yuxuejian.generator.util.VelocityUtil;

import org.apache.velocity.VelocityContext;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeneratorServiceImpl implements GeneratorService {
	
	@Autowired
	private GeneratorConfig generatorConfig;
	
    @Override
    public Result generator(GeneratorDto generatorDto) {
        Result result = validate(generatorDto);
        if (result.getSuccess() == 0) {
            return result;
        }
        boolean connect = JdbcUtil.isConnect(generatorDto.getHostPort(),generatorDto.getDatabase(), generatorDto.getUsername(), generatorDto.getPassword());
        if (!connect) {
            return new Result(0,"数据库连接失败，请检查数据库连接信息是否正确");
        }
        // 遍历表名
        Map<String, TableInfo> tables = getTables2(generatorDto);
        List<TableInfo> tableList = new ArrayList<TableInfo>();
        String[] tablesNames = generatorDto.getTables();
        if (tablesNames == null) {
            return new Result(0,"至少选择一张表");
        }
        for (String table : tablesNames) {
            if (!tables.containsKey(table)) {
                return new Result(0,"表名【"+table+"】不存在");
            }
            tableList.add(tables.get(table));
        }
        Map<String, String> generatorFile = new HashMap<String, String>();
        result = getGeneratorFile(generatorDto, generatorFile);
        if (result.getSuccess() != 1) {
        	return result;
        }
        String projectName = generatorDto.getProjectName();
        if (StringUtils.isEmpty(projectName)) {
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    		projectName = sdf.format(new Date());
    	}
        FileUtil.createEmptyDir(generatorConfig.getFileBasePath()+projectName + "/src/main/java");
        FileUtil.createEmptyDir(generatorConfig.getFileBasePath()+projectName + "/src/main/resources");
        generatorDto.setProjectName(projectName);
        // 生成entity、dto、Mapper、xml文件
        try {
			generatorDao(generatorDto, generatorFile, tableList);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(0, "生成Dao失败");
		}
        try {
            generatorService(generatorDto, generatorFile, tableList);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(0, "生成Service失败");
        }

        try {
            generatorServiceImpl(generatorDto, generatorFile, tableList);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(0, "生成ServiceImpl失败");
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

    private List<TableInfo> getTables1(GeneratorDto generatorDto) {
        JdbcUtil jdbcUtil = new JdbcUtil(generatorDto.getHostPort(), generatorDto.getDatabase(), generatorDto.getUsername(), generatorDto.getPassword());
        String sql = "SELECT table_name,table_comment FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = '" + generatorDto.getDatabase() + "';";
        List<Map> result = null;
        try {
            result = jdbcUtil.selectByParams(sql, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<TableInfo> tables = new ArrayList<>();
        TableInfo table = null;
        for (Map map : result) {
            table = new TableInfo();
            String tableName = map.get("TABLE_NAME").toString();
            table.setTableName(tableName);
            if (map.get("TABLE_COMMENT") != null) {
            	table.setTableComment(map.get("TABLE_COMMENT").toString());
            }
            tables.add(table);
        }
        jdbcUtil.release();
        return tables;
    }

    private Map<String, TableInfo> getTables2(GeneratorDto generatorDto) {
        JdbcUtil jdbcUtil = new JdbcUtil(generatorDto.getHostPort(), generatorDto.getDatabase(), generatorDto.getUsername(), generatorDto.getPassword());
        String sql = "SELECT table_name FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = '" + generatorDto.getDatabase() + "';";
        List<Map> result = null;
        try {
            result = jdbcUtil.selectByParams(sql, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Map<String, TableInfo> table = new HashMap<>();
        TableInfo tableInfo = null;
        for (Map map : result) {
        	tableInfo = new TableInfo();
            String tableName = map.get("TABLE_NAME").toString();
            tableInfo.setTableName(tableName);
            if (map.get("TABLE_COMMENT") != null) {
            	tableInfo.setTableComment(map.get("TABLE_COMMENT").toString());
            }
            tableInfo.setEntityName(tableInfo.getEntityName(generatorDto.getPrefix(), tableName));
            table.put(tableName, tableInfo);
        }
        jdbcUtil.release();
        return table;
    }

    private Map<String, ColumnInfo> getColumnInfo(GeneratorDto generatorDto, String tableName) {
        JdbcUtil jdbcUtil = new JdbcUtil(generatorDto.getHostPort(), generatorDto.getDatabase(), generatorDto.getUsername(), generatorDto.getPassword());
        String sql="SELECT ordinal_position,column_name,is_nullable,data_type,charActer_maximum_length as charActer_maximum_length,column_type,column_key,column_comment FROM information_schema.columns " +
                "WHERE table_schema = '"+generatorDto.getDatabase()+"' " +
                "AND table_name = '"+tableName+"'";
        List<Map> result = null;
        try {
            result = jdbcUtil.selectByParams(sql, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Map<String, ColumnInfo> columns = new HashMap<>();
        ColumnInfo column = null;
        for (Map map : result) {
        	column = new ColumnInfo();
        	column.setDatabaseColumnName(map.get("COLUMN_NAME").toString());
        	column.setDatabaseIsNullable(map.get("IS_NULLABLE").toString());
        	column.setDatabaseDataType(map.get("DATA_TYPE").toString());
        	column.setDatabaseColumnComment(map.get("COLUMN_COMMENT").toString());
            columns.put(map.get("COLUMN_NAME").toString(), column);
        }
        jdbcUtil.release();
        return columns;
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
        Result result = validate1(generatotDto);
        if (result.getSuccess() != 1) {
            return result;
        }
        if (generatotDto.getCategory() ==  null || generatotDto.getCategory().length == 0) {
            return new Result(0,"请选择需要生产的文件");
        }
        return new Result(1,"成功");
    }
    
    private void generatorDao(GeneratorDto generatorDto, Map<String, String> generatorFile, List<TableInfo> tables) throws Exception {
    	boolean flag = false;
    	VelocityContext context = new VelocityContext();
    	
    	if (generatorFile.containsKey("1")) {
    		flag = true;
    		context.put("dao", "1");
    		context.put("entityPackage", generatorFile.get("1"));
    		context.put("mapperPackage", generatorFile.get("11"));
    	}
    	if (flag == false) {
    		return;
    	}

    	context.put("jdbcDriver", "com.mysql.cj.jdbc.Driver");
    	StringBuilder url = new StringBuilder("jdbc:mysql://");
        url.append(generatorDto.getHostPort()).append("/").append(generatorDto.getDatabase()).append("?serverTimezone=UTC&amp;useUnicode=true&amp;characterEncoding=utf-8&amp;autoReconnect=true&amp;nullCatalogMeansCurrent=true");
    	context.put("jdbcUrl", url.toString());
    	context.put("jdbcUsername", generatorDto.getUsername());
    	context.put("jdbcPassword", generatorDto.getPassword());
    	String projectName = generatorDto.getProjectName();
    	context.put("targetProject", generatorConfig.getFileBasePath()+projectName);
    	context.put("tables", tables);
    	// 生成generatorconfig.xml文件
    	String generatorConfig_vm = GeneratorServiceImpl.class.getResource("/templates/generatorConfig.vm").getPath().replaceFirst("/", "");
    	String generatorConfigXml = GeneratorServiceImpl.class.getResource("/").getPath().replace("/target/classes/", "") + "/src/main/resources/generatorConfig.xml";
    	VelocityUtil.generate(generatorConfig_vm, generatorConfigXml, context);
    	System.out.println("生成generatorConfig.xml完成");
    	// 根据模板生成文件
    	System.out.println("========== 开始生成dao ==========");
		List<String> warnings = new ArrayList<>();
		File configFile = new File(generatorConfigXml);
		ConfigurationParser cp = new ConfigurationParser(warnings);
		Configuration config = cp.parseConfiguration(configFile);
		DefaultShellCallback callback = new DefaultShellCallback(true);
		MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
		myBatisGenerator.generate(null);
		for (String warning : warnings) {
			System.out.println(warning);
		}
		generatorDto(generatorDto, generatorFile, generatorFile.get("12"), tables);
		System.out.println("========== 结束生成dao ==========");
    }

    private void generatorDto(GeneratorDto generatorDto,Map<String, String> generatorFile, String dtoPackage,List<TableInfo> tables) throws Exception{
        String dto_vm = System.getProperty("user.dir") + "/src/main/resources/templates/dto.vm";
        String projectName = generatorDto.getProjectName();
        String basePath = generatorConfig.getFileBasePath() + projectName + "/src/main/java";
        for (TableInfo tableInfo : tables) {
            String filePath = basePath + "/" + dtoPackage.replace(".","/");
            String dto = filePath + "/" + tableInfo.getEntityName()+"Dto.java";
            VelocityContext context = new VelocityContext();
            context.put("dtoPackage", dtoPackage);
            context.put("entityPackage", generatorFile.get("1"));
            context.put("model", tableInfo.getEntityName());
            createFile(filePath, dto, dto_vm, context);
        }
    }

    private Result getGeneratorFile(GeneratorDto generatorDto, Map<String, String> generatorFile) {
    	String[] category = generatorDto.getCategory();
    	String basePackage = generatorDto.getBasePackage();
    	if (StringUtils.isEmpty(basePackage)) {
            return new Result(0, "包名不能为空");
        }
    	for (String file : category) {
    		if (file.equals("1")) {
    			generatorFile.put(file, basePackage + ".api.entity");
    			generatorFile.put("11", basePackage + ".rpc.mapper");
                generatorFile.put("12", basePackage + ".api.dto");
    		}
    		if (file.equals("2")) {
    			generatorFile.put(file, basePackage + ".api.service");
    		}
    		if (file.equals("3")) {
    			generatorFile.put(file, basePackage + ".rpc.service.impl");
    		}
    		if (file.equals("4")) {
    			generatorFile.put(file, basePackage + ".web.service");
    		}
    		if (file.equals("5")) {
    			generatorFile.put(file, basePackage + ".web.service.impl");
    		}
    		if (file.equals("6")) {
    			generatorFile.put(file, basePackage + "web.controller");
    		}
    	}
    	return new Result(1, "成功");
    }

	private void generatorService(GeneratorDto generatorDto, Map<String, String> generatorFile,
			List<TableInfo> tables) throws Exception{
		String service_vm = System.getProperty("user.dir") + "/src/main/resources/templates/service.vm";
		String projectName = generatorDto.getProjectName();
		String basePath = generatorConfig.getFileBasePath() + projectName + "/src/main/java";
		for (TableInfo tableInfo : tables) {
			if (generatorFile.get("2") != null) {
			    String filePath = basePath + "/" + generatorDto.getBasePackage().replace(".", "/") + "/api/service";
				String service = filePath + "/" + tableInfo.getEntityName()+"Service.java";
				VelocityContext context = new VelocityContext();
				context.put("servicePackage", generatorDto.getBasePackage()+".api.service");
				context.put("dtoPackage", generatorDto.getBasePackage()+".api.dto");
				context.put("model", tableInfo.getEntityName());
				context.put("model1", tableInfo.getEntityName().toLowerCase());
                Map<String, ColumnInfo> columnInfoMap = getColumnInfo(generatorDto, tableInfo.getTableName());
                context.put("idType",columnInfoMap.get("id").getDataType());
                createFile(filePath, service, service_vm, context);
			}
		}
	}

    private void generatorServiceImpl(GeneratorDto generatorDto, Map<String, String> generatorFile,
                                  List<TableInfo> tables) throws Exception{
        String service_vm = System.getProperty("user.dir") + "/src/main/resources/templates/serviceImpl.vm";
        String projectName = generatorDto.getProjectName();
        String basePath = generatorConfig.getFileBasePath() + projectName + "/src/main/java";
        for (TableInfo tableInfo : tables) {
            if (generatorFile.get("3") != null) {
                String filePath = basePath + "/" + generatorDto.getBasePackage().replace(".", "/") + "/rpc/service/impl";
                String service = filePath + "/" + tableInfo.getEntityName()+"ServiceImpl.java";
                VelocityContext context = new VelocityContext();
                context.put("servicePackage", generatorDto.getBasePackage()+".api.service");
                context.put("dtoPackage", generatorDto.getBasePackage()+".api.dto");
                context.put("model", tableInfo.getEntityName());
                context.put("model1", tableInfo.getEntityName().toLowerCase());
                context.put("serviceImplPackage", generatorDto.getBasePackage()+"rpc.service.impl");
                context.put("entityPackage",generatorDto.getBasePackage()+"api.entity");
                Map<String, ColumnInfo> columnInfoMap = getColumnInfo(generatorDto, tableInfo.getTableName());
                context.put("idType",columnInfoMap.get("id").getDataType());
                context.put("columns",getListByMap(columnInfoMap));
                createFile(filePath, service, service_vm, context);
            }
        }
    }

    private List<ColumnInfo> getListByMap(Map<String, ColumnInfo> columnInfoMap) {
        List<ColumnInfo> result = new ArrayList<>();
        for (Map.Entry<String, ColumnInfo> entry : columnInfoMap.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }

	private void createFile(String filePath, String file, String vmFile, VelocityContext context) throws Exception{
        File serviceFile = new File(file);
        if (!serviceFile.exists()) {
            FileUtil.createEmptyDir(filePath);
            serviceFile.createNewFile();
        }
        VelocityUtil.generate(vmFile, file, context);
    }
}
