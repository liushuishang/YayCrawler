package yaycrawler.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import yaycrawler.worker.listener.WorkerRegisterListener;

@SpringBootApplication
@ImportResource(locations = {"classpath*:spring/*.xml"})
@EntityScan(basePackages = {"yaycrawler.dao.domain"})
@EnableJpaRepositories(basePackages = {"yaycrawler.dao.repositories"})
public class Application {

	public static void main(String[] args) {
        SpringApplication springApplication =new SpringApplication(Application.class);
        springApplication.addListeners(new WorkerRegisterListener());
        springApplication.run(args);
	}

}
