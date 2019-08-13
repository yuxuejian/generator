package com.yuxuejian.generator.Service;

import com.yuxuejian.generator.dto.GeneratorDto;
import com.yuxuejian.generator.dto.Result;

public interface GeneratorService {
    Result generator(GeneratorDto generatotDto);

    Result test(GeneratorDto generatotDto);

    Result select(GeneratorDto generatotDto);
}
