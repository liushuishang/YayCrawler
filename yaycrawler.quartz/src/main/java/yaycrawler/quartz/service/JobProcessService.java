//package yaycrawler.quartz.service;
//
//import org.apache.commons.collections.MapUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.math.NumberUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.util.WebUtils;
//import yaycrawler.quartz.model.Constant;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.net.URLDecoder;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by ucs_yuananyun on 2016/6/3.
// */
//@Service
//public class JobProcessService {
//
//    @Autowired
//    private ISchedulerService schedulerService;
//
//    /**
//     * 添加Simple Trigger
//     *
//     * @param request
//     * @param response
//     */
//    private void addSimpleTrigger(HttpServletRequest request, HttpServletResponse response) throws IOException {
//
//        // 获取界面以p_参数
//        Map<String, Object> filterMap = WebUtils.getParametersStartingWith(request, "p_");
//        if (StringUtils.isEmpty(MapUtils.getString(filterMap, Constant.STARTTIME))) {
//            response.getWriter().println(1);
//        }
//
//        // 添加任务调试
//        schedulerService.schedule(filterMap);
//
//        // response.setContentType("text/xml;charset=utf-8");
//        response.getWriter().println(0);
//
//    }
//
//    /**
//     * 根据Cron表达式添加Cron Trigger，
//     *
//     * @param request
//     * @param response
//     */
//    private void addCronTriggerByExpression(HttpServletRequest request, HttpServletResponse response)
//            throws IOException {
//
//        // 获取界面以参数
//        String triggerName = request.getParameter("triggerName");
//        String cronExpression = request.getParameter("cronExpression");
//        if (StringUtils.isEmpty(triggerName) || StringUtils.isEmpty(cronExpression)) {
//            response.getWriter().println(1);
//        }
//
//        // 添加任务调试
//        schedulerService.schedule(triggerName, cronExpression);
//
//        // response.setContentType("text/xml;charset=utf-8");
//        response.getWriter().println(0);
//
//    }
//
//    /**
//     * 根据添加Cron Trigger，
//     *
//     * @param request
//     * @param response
//     */
//    private void addCronTriggerBy(HttpServletRequest request, HttpServletResponse response) throws IOException {
//
//        // 获取界面以参数
//        String triggerName = request.getParameter("triggerName");
//        String val = request.getParameter("val");
//        String selType = request.getParameter("selType");
//        if (StringUtils.isEmpty(triggerName) || StringUtils.isEmpty(val) || NumberUtils.toLong(val) < 0
//                || NumberUtils.toLong(val) > 59) {
//            response.getWriter().println(1);
//        }
//
//        String expression = null;
//        if (StringUtils.equals(selType, "second")) {
//            // 每多秒执行一次
//            expression = "0/" + val + " * * ? * * *";
//        } else if (StringUtils.equals(selType, "minute")) {
//            // 每多少分执行一次
//            expression = "0 0/" + val + " * ? * * *";
//        }
//
//        // 添加任务调试
//        schedulerService.schedule(triggerName, expression);
//
//        // response.setContentType("text/xml;charset=utf-8");
//        response.getWriter().println(0);
//
//    }
//
//    /**
//     * 取得所有Trigger
//     *
//     * @param request
//     * @param response
//     * @throws ServletException
//     * @throws IOException
//     */
//    private void getQrtzTriggers(HttpServletRequest request, HttpServletResponse response) throws ServletException,
//            IOException {
//        List<Map<String, Object>> results = this.schedulerService.getQrtzTriggers();
//        request.setAttribute("list", results);
//        request.getRequestDispatcher("/list.jsp").forward(request, response);
//    }
//
//    /**
//     * 根据名称和组别暂停Tigger
//     *
//     * @param request
//     * @param response
//     * @throws IOException
//     */
//    private void pauseTrigger(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        // request.setCharacterEncoding("UTF-8");
//        String triggerName = URLDecoder.decode(request.getParameter("triggerName"), "utf-8");
//        String group = URLDecoder.decode(request.getParameter("group"), "utf-8");
//
//        schedulerService.pauseTrigger(triggerName, group);
//        response.getWriter().println(0);
//    }
//
//    /**
//     * 根据名称和组别暂停Tigger
//     *
//     * @param request
//     * @param response
//     * @throws IOException
//     */
//    private void resumeTrigger(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        // request.setCharacterEncoding("UTF-8");
//        String triggerName = URLDecoder.decode(request.getParameter("triggerName"), "utf-8");
//        String group = URLDecoder.decode(request.getParameter("group"), "utf-8");
//
//        schedulerService.resumeTrigger(triggerName, group);
//        response.getWriter().println(0);
//    }
//
//    /**
//     * 根据名称和组别暂停Tigger
//     *
//     * @param request
//     * @param response
//     * @throws IOException
//     */
//    private void removeTrigdger(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String triggerName = URLDecoder.decode(request.getParameter("triggerName"), "utf-8");
//        String group = URLDecoder.decode(request.getParameter("group"), "utf-8");
//
//        boolean rs = schedulerService.removeTrigdger(triggerName, group);
//        if (rs) {
//            response.getWriter().println(0);
//        } else {
//            response.getWriter().println(1);
//        }
//    }
//}
