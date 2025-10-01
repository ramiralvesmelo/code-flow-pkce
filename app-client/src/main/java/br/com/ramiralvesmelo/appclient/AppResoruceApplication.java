package br.com.ramiralvesmelo.appclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "br.com.ramiralvesmelo.appclient")
public class AppResoruceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppResoruceApplication.class, args);
	}

}
