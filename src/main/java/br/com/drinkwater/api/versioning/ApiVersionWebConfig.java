package br.com.drinkwater.api.versioning;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(ApiVersionProperties.class)
public class ApiVersionWebConfig implements WebMvcConfigurer {

    private final ApiVersionProperties apiVersionProperties;

    public ApiVersionWebConfig(ApiVersionProperties apiVersionProperties) {
        this.apiVersionProperties = apiVersionProperties;
    }

    @Bean
    ApiVersionInterceptor apiVersionInterceptor() {
        return new ApiVersionInterceptor(apiVersionProperties);
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(apiVersionInterceptor()).addPathPatterns("/api/**");
    }
}
