package yaycrawler.cache.service;

/**
 * Created by ucs_guoguibiao on 6/28 0028.
 */

import org.springframework.context.ApplicationContext;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 这里使用了ehcache与spring结合，这里并没用用到数据库，用spring只是用来管理bean，
 * 这里用ehcache就相当于数据库，存放对象信息
 *
 * @author longgangbai
 */

@SuppressWarnings({"unchecked"})
public class HelloEhcacheSpring {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:/spring/applicationContext.xml");

        TicketService ticketSrv = (TicketService) context.getBean("ticketService");

        //配置了spring就可以从配置文件里找到对应的接口实现类，再生成实例对象，以完成业务处理
        String srvName0 = ticketSrv.testMethod();

        //获取初始化服务端名称
        System.out.println("srvName0=" + srvName0);

        //设置存储的名称
        ticketSrv.setServiceName("ticketService");

        String srvName1 = ticketSrv.testMethod();

        //获取服务端名称
        System.out.println("srvName1=" + srvName1);

        //修改服务名称但是不缓存
        ticketSrv.updateMethod();

        String srvName2 = ticketSrv.testMethod();

        //获取服务端名称来源自缓存注意观察
        System.out.println("srvName2=" + srvName2);


    }

}

