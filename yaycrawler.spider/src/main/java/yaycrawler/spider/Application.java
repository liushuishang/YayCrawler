package yaycrawler.spider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//@ImportResource({"classpath*:/spring/*.xml"})
@ComponentScan(basePackages = {"yaycrawler"})
@EntityScan(basePackages = {"yaycrawler.common.domain"})
@EnableJpaRepositories(basePackages = {"yaycrawler.common.dao.repositories"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
