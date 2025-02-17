package com.nb6868.onex.common;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("验证码测试")
@Slf4j
public class CacheTest {

    @Test
    @DisplayName("测试定时缓存")
    void testTimeCache() {
        //创建缓存，默认4毫秒过期
        TimedCache<String, String> timedCache = CacheUtil.newTimedCache(4);
//实例化创建
//TimedCache<String, String> timedCache = new TimedCache<String, String>(4);

        timedCache.put("key1", "value1", 1);//1毫秒过期
        timedCache.put("key2", "value2", DateUnit.SECOND.getMillis() * 5);
        timedCache.put("key3", "value3");//默认过期(4毫秒)

        //启动定时任务，每5毫秒清理一次过期条目，注释此行首次启动仍会清理过期条目
        // timedCache.schedulePrune(5);

        //等待5毫秒
        ThreadUtil.sleep(5);

        //5毫秒后由于value2设置了5毫秒过期，因此只有value2被保留下来
        String value1 = timedCache.get("key1");//null
        String value2 = timedCache.get("key2");//value2
        //5毫秒后，由于设置了默认过期，key3只被保留4毫秒，因此为null
        String value3 = timedCache.get("key3");//null
        log.error("value1={}", value1);
        log.error("value2={}", value2);
        log.error("value3={}", value3);
        //取消定时清理
        // timedCache.cancelPruneSchedule();
    }
}
