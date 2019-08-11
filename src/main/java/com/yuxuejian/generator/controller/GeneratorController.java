package com.yuxuejian.generator.controller;

import com.yuxuejian.generator.Service.GeneratorService;
import com.yuxuejian.generator.dto.GeneratotDto;
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
    public Result submit(GeneratotDto generatotDto) {
        return generatorService.generator(generatotDto);
    }
}
