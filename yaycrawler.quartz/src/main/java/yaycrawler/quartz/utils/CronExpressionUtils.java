package yaycrawler.quartz.utils;

import org.quartz.CronScheduleBuilder;

/**
 * Created by ucs_yuananyun on 2016/6/6.
 */
public class CronExpressionUtils {

    public static boolean isValidExpression(String cronExpression) {
        try {
            CronScheduleBuilder cronBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}
