package yaycrawler.ftpserver.listener;

/**
 * Created by ucs_guoguibiao on 6/13 0013.
 */
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.ftpserver.impl.DefaultFtpServer;
import org.apache.ftpserver.usermanager.SaltedPasswordEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@Component
public class FtpServerListener implements ServletContextListener {

    private static Logger logger = LoggerFactory.getLogger(FtpServerListener.class);

    public void contextDestroyed(ServletContextEvent contextEvent) {
        logger.info("Stopping FtpServer");
        DefaultFtpServer server = (DefaultFtpServer) contextEvent.getServletContext()
                .getAttribute("FTPSERVER_CONTEXT_NAME");
        if (server != null) {
            server.stop();
            contextEvent.getServletContext().removeAttribute("FTPSERVER_CONTEXT_NAME");
            logger.info("FtpServer stopped");
        } else {
            logger.info("No running FtpServer found");
        }
    }

    public void contextInitialized(ServletContextEvent contextEvent) {
        logger.info("Starting FtpServer");
        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(contextEvent.getServletContext());
        DefaultFtpServer server = (DefaultFtpServer) ctx.getBean("myServer");
        contextEvent.getServletContext().setAttribute("FTPSERVER_CONTEXT_NAME", server);
        try {
            server.start();
            logger.info("FtpServer started");
        } catch (Exception e) {
            throw new RuntimeException("Failed to start FtpServer", e);
        }
    }
}
