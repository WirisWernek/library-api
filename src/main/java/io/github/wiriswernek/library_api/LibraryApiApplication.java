package io.github.wiriswernek.library_api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Library API",
				version = "1.0.0",
				description = "API de Gerenciamento de emprestimos de livros",
				termsOfService = "wiriswernek",
				contact = @Contact(
						name = "Wiris Wernek",
						email = "wiriswernek@gmail.com"
				),
				license = @License(
						name = "licence",
						url = "license"
				)
		)
)
public class LibraryApiApplication {
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(LibraryApiApplication.class, args);
	}

}
