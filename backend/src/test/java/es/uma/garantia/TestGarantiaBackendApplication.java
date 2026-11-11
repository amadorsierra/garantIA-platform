package es.uma.garantia;

import org.springframework.boot.SpringApplication;

public class TestGarantiaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(GarantiaBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
