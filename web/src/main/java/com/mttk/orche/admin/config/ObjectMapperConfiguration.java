package com.mttk.orche.admin.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
@Configuration 
//定义一个ObjectMapper bean  用于接口调用
public class ObjectMapperConfiguration {
	@Bean
	  public ObjectMapper ObjectMapper(){
	   ObjectMapper objectMapper=new ObjectMapper();
	   return objectMapper;
	  }
}
