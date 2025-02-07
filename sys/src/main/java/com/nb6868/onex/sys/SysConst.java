package com.nb6868.onex.sys;

import cn.hutool.core.util.ObjUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * 系统模块常量
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface SysConst {

    String OSS_PUBLIC = "OSS_PUBLIC";

    /**
     * 日志类型
     */
    @Getter
    @AllArgsConstructor
    enum LogTypeEnum {

        /**
         * 详见name
         */
        LOGIN("login", "登录"),
        OPERATION("operation", "操作"),
        ERROR("error", "错误");

        private String code;
        private String title;

        public static LogTypeEnum findByCode(String codeValue) {
            return Stream.of(values())
                    .filter(p -> ObjUtil.equal(codeValue, p.getCode()))
                    .findFirst()
                    .orElse(null);
        }

        public static String getTitleByCode(String codeValue) {
            LogTypeEnum r = findByCode(codeValue);
            return ObjUtil.isNull(r) ? "未定义" + codeValue : r.getTitle();
        }

        public static boolean isValid(String codeValue) {
            return findByCode(codeValue) != null;
        }
    }


    /**
     * 日历类型
     * 0工作日/1周末/2节日/3调休
     */
    @Getter
    @AllArgsConstructor
    enum CalenderTypeEnum {

        /**
         * 详见name
         */
        WORKDAY(0, "工作日"),
        WEEKEND(1, "周末"),
        HOLIDAY(2, "节日"),
        HOLIDAY_EXCHANGE(3, "调休");

        private int code;
        private String title;

        public static CalenderTypeEnum findByCode(Integer codeValue) {
            return Stream.of(values())
                    .filter(p -> ObjUtil.equal(codeValue, p.getCode()))
                    .findFirst()
                    .orElse(null);
        }

        public static String getTitleByCode(Integer codeValue) {
            CalenderTypeEnum r = findByCode(codeValue);
            return ObjUtil.isNull(r) ? "未定义" + codeValue : r.getTitle();
        }

        public static boolean isValid(Integer codeValue) {
            return findByCode(codeValue) != null;
        }

    }

}
