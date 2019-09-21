package io.gyul.projectgyul;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "io.gyul")
@EnableScheduling
public class ProjectGyulApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ProjectGyulApplication.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.run(args);
	}

}
