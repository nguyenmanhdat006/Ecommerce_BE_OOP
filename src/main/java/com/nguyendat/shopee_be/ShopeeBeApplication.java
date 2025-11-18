package com.nguyendat.shopee_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ShopeeBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopeeBeApplication.class, args);
	}

}
