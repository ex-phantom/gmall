package com.shadow.gmall;

//import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;


@SpringBootApplication
@MapperScan("com.shadow.gmall.payment.mapper")
public class GmallPaymentWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallPaymentWebApplication.class,args);




	}

}

