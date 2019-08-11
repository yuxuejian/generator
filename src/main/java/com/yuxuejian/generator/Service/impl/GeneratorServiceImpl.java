package com.yuxuejian.generator.Service.impl;

import com.yuxuejian.generator.Service.GeneratorService;
import com.yuxuejian.generator.dto.GeneratotDto;
import com.yuxuejian.generator.dto.Result;
import com.yuxuejian.generator.util.JdbcUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class GeneratorServiceImpl implements GeneratorService {
    @Override
    public Result generator(GeneratotDto generatotDto) {
        Result result = validate(generatotDto);
        if (result.getSuccess() == 0) {
            return result;
        }
        boolean connect = JdbcUtil.isConnect(generatotDto.getHostPort(),generatotDto.getDatabase(), generatotDto.getUsername(), generatotDto.getPassword());
        if (!connect) {
            return new Result(0,"数据库连接失败，请检查数据库连接信息是否正确");
        }
        return new Result(1,"成功");
    }

    private Result validate(GeneratotDto generatotDto) {
        if (StringUtils.isEmpty(generatotDto.getHostPort())) {
            return new Result(0,"数据库地址不能为空");
        }
        if (StringUtils.isEmpty(generatotDto.getUsername())) {
            return new Result(0,"数据库账号不能为空");
        }
        if (StringUtils.isEmpty(generatotDto.getPassword())) {
            return new Result(0,"数据库密码不能为空");
        }
        if (generatotDto.getCategory() ==  null || generatotDto.getCategory().length == 0) {
            return new Result(0,"请选择需要生产的文件");
        }
        return new Result(1,"成功");
    }
}
