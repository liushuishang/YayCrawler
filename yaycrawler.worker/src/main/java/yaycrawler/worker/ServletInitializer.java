package yaycrawler.worker;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import yaycrawler.worker.listener.WorkerRegisterListener;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		application.listeners(new WorkerRegisterListener());
		return application.sources(Application.class);
	}

}
