package com.nb6868.onex.common;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Slf4j
public class HttpTest {

    @Test
    @DisplayName("urlencode")
    void urlencode() {
        HashMap<String, Object> paramMap = new LinkedHashMap<>();
        paramMap.put("key", "你好吗");
        paramMap.put("key1", null);
        paramMap.put("key2", "");
        paramMap.put("key3", "456");
        log.error(HttpUtil.urlWithFormUrlEncoded("http://127.0.0.1/search", paramMap, Charset.defaultCharset()));
        log.error(HttpUtil.urlWithForm("http://127.0.0.1/search", paramMap, Charset.defaultCharset(), false));
        log.error(HttpUtil.toParams(paramMap, null, false));
        log.error(HttpUtil.toParams(paramMap, null, true));
        log.error(HttpUtil.toParams(paramMap, Charset.defaultCharset(), false));
        log.error(HttpUtil.toParams(paramMap, Charset.defaultCharset(), true));
    }

}
