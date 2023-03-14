package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
//@ComponentScan(basePackages = {"com.example" ,"com.example.demo.config"})
@ComponentScan(basePackages = {"com.example"})
@MapperScan("com.example.demo.mapper")
@EnableSwagger2
public class PacsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PacsServiceApplication.class, args);
	}

}
