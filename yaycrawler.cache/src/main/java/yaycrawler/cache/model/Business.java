package yaycrawler.cache.model;

import org.springframework.stereotype.Component;

/**
 * Created by shentong on 2016/7/13.
 */
@Component    //使用自动注解的方式实例化并初始化该类
public class Business {
    // 切入点
    public String delete(String obj) {
        System.out.println("==========调用切入点：" + obj + "说：你敢删除我！===========\n");
        return obj + "：瞄～";
    }

    public String add(String obj) {
        System.out.println("================这个方法不能被切。。。============== \n");
        return obj + "：瞄～ 嘿嘿！";
    }

    public String modify(String obj) {
        System.out.println("=================这个也设置加入切吧====================\n");
        return obj + "：瞄改瞄啊！";
    }

}
