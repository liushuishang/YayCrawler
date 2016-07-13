package yaycrawler.cache.service;

/**
 * Created by ucs_guoguibiao on 6/28 0028.
 */

import java.util.List;

/**
 * 对其所有的以testMethod* ,getServiceName方式命令的方法，
 * 进行缓存处理。当调用其他命令时，不进行缓存
 * @author longgangbai
 *
 */
@SuppressWarnings("unchecked")
public class TicketService {

    public String testMethod(){
        System.out.println("没走缓存，直接调用TestService.testMethod()");
        return "china";
    }

    public void updateMethod(){
        System.out.println("updateMethod");
    }

    public void insertMethod(){
        System.out.println("insertMethod");
    }

    public void deleteMethod(){
        System.out.println("deleteMethod");
    }

    /**
     * 需要缓存的集合
     */
    private List ticketList;
    /**
     * 需要缓存的服务名称
     */
    private String serviceName;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List getTicketList() {
        return ticketList;
    }
    public void setTicketList(List ticketList) {
        this.ticketList = ticketList;
    }
    /**
     * 修改的服务端名称备注但是不缓存
     * @param serviceName
     */
    public void changesetServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}

