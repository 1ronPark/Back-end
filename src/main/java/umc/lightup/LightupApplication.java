package umc.lightup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LightupApplication {

	public static void main(String[] args) {
		SpringApplication.run(LightupApplication.class, args);
	}

}
