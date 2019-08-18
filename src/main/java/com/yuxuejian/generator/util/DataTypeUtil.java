package com.yuxuejian.generator.util;

import java.util.HashMap;

public class DataTypeUtil {

    public static final HashMap<String, String> dataTypes = new HashMap<String, String>(){{
        put("char","String");
        put("varchar","String");
        put("longtext","String");
        put("longblob","String");
        put("tinyint","Integer");
        put("smallint","Integer");
        put("int","Integer");
        put("bigint","Long");
        put("date","Date");
        put("datetime","Date");
        put("float","Float");
        put("double","Double");
        put("decimal","BigDecimal");
    }};
}
