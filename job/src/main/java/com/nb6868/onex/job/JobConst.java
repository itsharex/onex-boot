package com.nb6868.onex.job;

import cn.hutool.core.util.ObjUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * 定时任务相关常量
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface JobConst {

    /**
     * 定时任务状态
     */
    @Getter
    @AllArgsConstructor
    enum JobStateEnum {

        PAUSE(0, "暂停"),
        NORMAL(1, "正常");

        private int code;
        private String title;

        public static JobStateEnum findByCode(Integer codeValue) {
            return Stream.of(values())
                    .filter(p -> ObjUtil.equal(codeValue, p.getCode()))
                    .findFirst()
                    .orElse(null);
        }

        public static String getTitleByCode(Integer codeValue) {
            JobStateEnum r = findByCode(codeValue);
            return ObjUtil.isNull(r) ? "未定义" + codeValue : r.getTitle();
        }

        public static boolean isValid(Integer codeValue) {
            return findByCode(codeValue) != null;
        }
    }

    /**
     * 任务日志状态
     */
    @Getter
    @AllArgsConstructor
    enum JobLogStateEnum {

        INIT(0, "初始化"),
        START(1, "开始运行"),
        RUNNING(10, "运行中"),
        COMPLETED(100, "任务完成"),
        NORMAL(-1, "取消"),
        ERROR(-100, "错误");

        private int code;
        private String title;

        public static JobLogStateEnum findByCode(Integer codeValue) {
            return Stream.of(values())
                    .filter(p -> ObjUtil.equal(codeValue, p.getCode()))
                    .findFirst()
                    .orElse(null);
        }

        public static String getTitleByCode(Integer codeValue) {
            JobLogStateEnum r = findByCode(codeValue);
            return ObjUtil.isNull(r) ? "未定义" + codeValue : r.getTitle();
        }

        public static boolean isValid(Integer codeValue) {
            return findByCode(codeValue) != null;
        }
    }

}
