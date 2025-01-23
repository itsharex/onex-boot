package com.nb6868.onex.common.config;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * mybatis-plus配置
 * see {https://baomidou.com/guide/interceptor.html}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Configuration
@Slf4j
@ConditionalOnProperty(name = "onex.mybatisplus.enable", havingValue = "true")
public class MybatisPlusConfig extends BaseMybatisPlusConfig {

    @Value("${mybatis-plus.configuration.database-id:mysql}")
    private String datasourceId;
    /**
     * 配置插件
     * see {https://baomidou.com/pages/2976a3/}
     * 顺序:
     * 多租户,动态表名
     * 分页,乐观锁
     * sql 性能规范,防止全表更新与删除
     *
     * @return PaginationInterceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 多租户插件
        // interceptor.addInnerInterceptor(iniTenantInterceptor());
        // 动态表名拦截器
        interceptor.addInnerInterceptor(initDynamicTableNameInnerInterceptor());
        // 分页拦截器
        interceptor.addInnerInterceptor(initPaginationInterceptor(DbType.getDbType(datasourceId)));
        // 乐观锁插件
        interceptor.addInnerInterceptor(new com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor());
        // 添加非法SQL拦截器
        // interceptor.addInnerInterceptor(new com.baomidou.mybatisplus.extension.plugins.inner.IllegalSQLInnerInterceptor());
        return interceptor;
    }

    @Override
    protected InnerInterceptor initDynamicTableNameInnerInterceptor() {
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        dynamicTableNameInnerInterceptor.setTableNameHandler((sql, tableName) -> {
            String paramKey = dataTableMap.get(tableName);
            if (StrUtil.isNotBlank(paramKey)) {
                return DynamicTableParamHelper.getParamData(paramKey, String.class, tableName);
            } else {
                return tableName;
            }
        });
        return dynamicTableNameInnerInterceptor;
    }

    // 表名map
    private final Map<String, String> dataTableMap = new HashMap<>();

    @PostConstruct
    public void init() {
        // 在初始化阶段,将需要做分表的表名称塞入dataTableMap
        /*for (DataTable value : DataTable.values()) {
            dataTableMap.put(value.getTableName(), value.getParamKey());
        }*/
        log.info("MybatisPlusConfig init: " + dataTableMap);
        dataTableMap.forEach((s, s2) -> log.info("dataTableMap{}=>{}", s, s2));
    }

    /**
     * public enum DataTable {
     *
     *     COMMODITY("data_commodity", "电商商品", "data_commodity_table_name"),
     *     GENERAL("data_general", "通用", "data_general_table_name"),
     *
     *     private String tableName;
     *     private String name;
     *     private String paramKey;
     *
     *     public static DataTable findByTableName(String tableName) {
     *         for (DataTable code : values()) {
     *             if (Objects.equals(tableName, code.getTableName())) {
     *                 return code;
     *             }
     *         }
     *         return UNDEFINED;
     *     }
     *
     * }
     */

}
