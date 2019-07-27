package com.shadow.gmall.ums;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan("com.shadow.gmall.ums.mapper")
@SpringBootApplication
public class GmallumsApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallumsApplication.class, args);
	}

}
