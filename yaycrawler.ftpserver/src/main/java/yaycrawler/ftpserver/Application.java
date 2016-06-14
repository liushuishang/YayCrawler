package yaycrawler.ftpserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import yaycrawler.ftpserver.listener.FtpServerListener;

@SpringBootApplication
@ComponentScan
@ImportResource(locations = {"classpath*:spring/*.xml"})
public class Application {

	public static void main(String[] args) {
		SpringApplication springApplication =new SpringApplication(Application.class);
		springApplication.run(args);
	}
}
