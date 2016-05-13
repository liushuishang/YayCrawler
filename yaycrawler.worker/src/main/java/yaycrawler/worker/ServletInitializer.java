package yaycrawler.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.web.context.WebApplicationContext;
import yaycrawler.worker.communication.MasterActor;
import yaycrawler.worker.model.WorkerContext;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {

		return application.sources(Application.class);
	}

	@Override
	protected WebApplicationContext run(SpringApplication application) {
		WebApplicationContext context = super.run(application);
		WorkerContext.webApplicationContext = context;
		WorkerContext.masterServerAddress = context.getEnvironment().getProperty("master.server.address");

		MasterActor masterActor = context.getBean(MasterActor.class);
		WorkerContext.isSuccessRegisted=masterActor.register();
		return context;
	}
}
