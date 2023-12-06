package com.nb6868.onex.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Configuration
@ConditionalOnProperty(name = "onex.swagger.enable", havingValue = "true")
public class SwaggerConfig {

    /**
     * 根据@Tag 上的排序，写入x-order
     *
     * @return the global open api customizer
     */
    /*@Bean
    public GlobalOpenApiCustomizer orderGlobalOpenApiCustomizer() {
        return openApi -> {
            if (openApi.getTags() != null) {
                openApi.getTags().forEach(tag -> {
                    // 可以尝试将order写入description,反正也是没用的
                    // 然后docs配置中将tags-sorter改为order
                    Map<String, Object> map = new HashMap<>();
                    map.put("x-order", RandomUtil.randomInt(0, 100));
                    tag.setExtensions(map);
                });
            }
        };
    }*/
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .version(version)
                        .description(description))
                .components(new Components().addSecuritySchemes(authTokenKey, new SecurityScheme()
                        .name(authTokenKey).type(SecurityScheme.Type.HTTP)));
    }

    @Value("${knife4j.title:OneX-API}")
    private String title;
    @Value("${knife4j.description:API}")
    private String description;
    @Value("${knife4j.version:1.0.0}")
    private String version;
    @Value("${onex.auth.token-header-key:auth-token}")
    private String authTokenKey;

}
