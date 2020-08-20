package com.jm.online_store;


import com.jm.online_store.service.interf.ProductService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class OnlineStoreApplication {

	private static ProductService productService;


	public OnlineStoreApplication(ProductService productService){
		this.productService = productService;
	}


	public static void main(String[] args) {
		SpringApplication.run(OnlineStoreApplication.class, args);

		System.out.println(productService.findProductById(7L));


	}

}