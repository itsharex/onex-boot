package com.nb6868.onex.booster.xss;

import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.exception.OnexException;
import org.apache.commons.lang3.StringUtils;

/**
 * SQL过滤
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
public class SqlFilter {

    /**
     * SQL注入过滤
     * @param str  待验证的字符串
     */
    public static String sqlInject(String str){
        if(StringUtils.isBlank(str)){
            return null;
        }
        //去掉'|"|;|\字符
        str = StringUtils.replace(str, "'", "");
        str = StringUtils.replace(str, "\"", "");
        str = StringUtils.replace(str, ";", "");
        str = StringUtils.replace(str, "\\", "");

        //转换成小写
        str = str.toLowerCase();

        //非法字符
        String[] keywords = {"master", "truncate", "insert", "select", "delete", "update", "declare", "alter", "drop"};

        //判断是否包含非法字符
        for(String keyword : keywords){
            if(str.contains(keyword)){
                throw new OnexException(ErrorCode.INVALID_SYMBOL);
            }
        }

        return str;
    }
}
