package com.example.prefSchedule.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiVersionConfig implements WebMvcConfigurer {
    //interceptor -> "paznic" care sta la intrearea in aplicatie si verifica fiecare cerere HTTP inainte sa ajunga la controller
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiVersionInterceptor());
    }

    public class ApiVersionInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            String apiVersion = request.getHeader("X-API-Version");
            if(apiVersion != null) {
                request.setAttribute("apiVersion", apiVersion);
            }
            return true;
        }

        //postHandle, afterCompletion
    }
}

