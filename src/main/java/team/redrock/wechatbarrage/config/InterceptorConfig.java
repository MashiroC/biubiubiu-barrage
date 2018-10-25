package team.redrock.wechatbarrage.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import team.redrock.wechatbarrage.interceptor.AdminInterceptor;
import team.redrock.wechatbarrage.interceptor.CorsInterceptor;
import team.redrock.wechatbarrage.interceptor.JwtInterceptor;

@Component
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Autowired
    private CorsInterceptor corsInterceptor;
    @Autowired
    private AdminInterceptor adminInterceptor;

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(corsInterceptor).addPathPatterns("/**");
        registry.addInterceptor(jwtInterceptor).addPathPatterns("/barrageIndex").addPathPatterns("/send");
        registry.addInterceptor(adminInterceptor).addPathPatterns("/admin/**").excludePathPatterns("/admin/login").excludePathPatterns("/admin/getUserList");
    }



}