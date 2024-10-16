package api.jarradclark.dev.TFLProxySpring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class TflProxySpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(TflProxySpringApplication.class, args);
	}

	@GetMapping("/hello")
	public String hello(@RequestParam(value = "nane", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

}
