package com.nb6868.onex.common;

import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.util.AmapClient;
import com.nb6868.onex.common.util.GpsUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Gps测试")
@Slf4j
public class GpsTest {

    @DisplayName("经纬度计算")
    @Test
    void testGpsCalc() {
        GpsUtils.LngLat lngLat1 = new GpsUtils.LngLat(121.48705, 29.964235);
        GpsUtils.LngLat lngLat2 = new GpsUtils.LngLat(121.49705, 29.963235);

        log.error("calBD09toGCJ02={}", GpsUtils.calBD09toGCJ02(lngLat1));
        log.error("calBD09toWGS84={}", GpsUtils.calBD09toWGS84(lngLat1));
        log.error("calWGS84toGCJ02={}", GpsUtils.calWGS84toGCJ02(lngLat1));
        log.error("calWGS84toBD09={}", GpsUtils.calWGS84toBD09(lngLat1));
        log.error("calGCJ02toBD09={}", GpsUtils.calGCJ02toBD09(lngLat1));
        log.error("calGCJ02toWGS84={}", GpsUtils.calGCJ02toWGS84(lngLat1));
        log.error("getDistance={}", GpsUtils.getDistance(lngLat1, lngLat2));
    }

    private final static String AMAP_KEY = "";

    @DisplayName("高德测试")
    @Test
    void testAmap() {
        AmapClient amapClient = new AmapClient();

        GpsUtils.LngLat lngLat = new GpsUtils.LngLat(121.570626, 29.90893);
        JSONObject geocodeRegeoForm = new JSONObject()
                .set("radius", 200)
                .set("batch", false)
                .set("extensions", "base")
                .set("key", AMAP_KEY)
                .set("location", lngLat.toString());
        JSONObject resp = amapClient.geocodeRegeo(geocodeRegeoForm);
        log.error("geocodeRegeo={}", resp);

        GpsUtils.LngLat gcj02 = GpsUtils.calWGS84toGCJ02(lngLat);
        JSONObject geocodeRegeoForm2 = new JSONObject()
                .set("radius", 200)
                .set("batch", false)
                .set("extensions", "base")
                .set("key", AMAP_KEY)
                .set("location", gcj02.toString());
        JSONObject resp2 = amapClient.geocodeRegeo(geocodeRegeoForm2);
        log.error("geocodeRegeo={}", resp2);
    }

}
