package yaycrawler.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ImportResource(locations = {"classpath*:spring/*.xml"})
@EntityScan(basePackages = {"yaycrawler.common.domain"})
@EnableJpaRepositories(basePackages = {"yaycrawler.common.dao.repositories"})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
