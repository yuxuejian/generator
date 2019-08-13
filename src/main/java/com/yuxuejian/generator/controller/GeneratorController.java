package com.yuxuejian.generator.controller;

import com.yuxuejian.generator.Service.GeneratorService;
import com.yuxuejian.generator.dto.GeneratorDto;
import com.yuxuejian.generator.dto.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GeneratorController {

    @Autowired
    private GeneratorService generatorService;

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @PostMapping("/submit")
    @ResponseBody
    public Result submit(GeneratorDto generatotDto) {
        return generatorService.generator(generatotDto);
    }

    @PostMapping("/test")
    @ResponseBody
    public Result test(GeneratorDto generatorDto) {
        return generatorService.test(generatorDto);
    }

    @PostMapping("/select")
    @ResponseBody
    public Result select(GeneratorDto generatorDto) {
        return generatorService.select(generatorDto);
    }
}
