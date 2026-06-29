package fr.foreach.barapp_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"fr.foreach.barapp_backend", "fr.foreach.barapp"})
@EntityScan(basePackages = "fr.foreach.barapp.entities")
@EnableJpaRepositories(basePackages = "fr.foreach.barapp.repository")
public class BarappBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BarappBackendApplication.class, args);
	}

}
