package cn.itcast.baobiao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "cn.itcast.baobiao.mapper")
public class BaobiaoApplication {

	public static void main(String[] args) {
		SpringApplication.run(BaobiaoApplication.class, args);
	}

}
