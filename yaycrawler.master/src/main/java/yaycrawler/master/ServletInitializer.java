package yaycrawler.master;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import yaycrawler.master.listener.MasterRegisterListener;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//		application.listeners(new MasterRegisterListener());
		return application.sources(Application.class);
	}

}
