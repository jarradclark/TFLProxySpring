package dev.jarradclark.tflproxy;

import dev.jarradclark.tflproxy.config.MainProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TFLProxyApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(TFLProxyApplication.class);

	@Autowired
	private MainProperties properties;

	public static void main(String[] args) {
		SpringApplication.run(TFLProxyApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		logger.info("Application Started. Currently set to {}", this.properties.getEnv());
		logger.debug("Debug logging is enabled :)");
	}
}
