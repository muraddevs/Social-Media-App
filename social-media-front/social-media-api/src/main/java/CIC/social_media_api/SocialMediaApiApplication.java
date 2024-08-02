package CIC.social_media_api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@OpenAPIDefinition(security = { @SecurityRequirement(name = "jwt"), @SecurityRequirement(name = "basic") })
@EnableTransactionManagement
public class SocialMediaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialMediaApiApplication.class, args);
	}
}
