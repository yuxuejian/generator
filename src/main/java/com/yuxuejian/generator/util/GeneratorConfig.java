package com.yuxuejian.generator.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class GeneratorConfig {
	
	@Value("${fileBasePath}")
	private String fileBasePath;

}
