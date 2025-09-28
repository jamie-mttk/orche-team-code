package com.mttk.orche.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import com.mttk.orche.admin.util.bson.DocumentMessageConverter;
import com.mttk.orche.core.Server;
import com.mttk.orche.core.ServerLocator;

@Configuration
@EnableWebMvc

public class WebConfig implements WebMvcConfigurer {

	@Override
	public void extendMessageConverters(java.util.List<HttpMessageConverter<?>> converters) {
		converters.add(0, messageConverter());
	}

	@Bean
	public DocumentMessageConverter messageConverter() {

		return new DocumentMessageConverter();
	}

	@Bean
	public Server server() {
		return ServerLocator.getServer();
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOriginPatterns("*")
				.allowedMethods("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH", "HEAD")
				.allowedHeaders("*")
				.allowCredentials(true)
				.maxAge(3600);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// registry.addInterceptor(new AccessLogInterceptor());
		// registry.addInterceptor(new SecurityHandlerInterceptor());

		// registry.addInterceptor(new Test1Interceptor());
		// registry.addInterceptor(new Test2Interceptor());
		// registry.addInterceptor(new Test3Interceptor());
		// registry.addInterceptor(new
		// SecurityCheckInterceptor()).addPathPatterns("/**").excludePathPatterns("/static/**");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 配置静态资源映射，支持直接访问根路径的静态资源
		registry.addResourceHandler("/**")
				.addResourceLocations("/static/")
				.setCachePeriod(31556926);
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		// 配置根路径访问，直接转发到 index.html
		registry.addViewController("/").setViewName("forward:/index.html");
	}

}
