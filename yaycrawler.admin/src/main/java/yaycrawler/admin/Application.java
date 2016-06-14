package yaycrawler.admin;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ImportResource(locations = {"classpath*:spring/*.xml"})
@EntityScan(basePackages = {"yaycrawler.dao.domain"})
@EnableJpaRepositories(basePackages = {"yaycrawler.dao.repositories"})
public class Application {

    @Value("${spring.multipart.maxFileSize}")
    private int maxPostSize;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public EmbeddedServletContainerCustomizer embeddedServletContainerCustomizer() {
        return new EmbeddedServletContainerCustomizer() {
            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {
                if (container instanceof TomcatEmbeddedServletContainerFactory) {
                    TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;
                    TomcatConnectorCustomizer tomcatConnectorCustomizer = new TomcatConnectorCustomizer() {
                        @Override
                        public void customize(Connector connector) {
                            connector.setMaxPostSize(maxPostSize);
                        }
                    };
                }
            }
        };
    }
}
