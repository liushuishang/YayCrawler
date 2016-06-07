package yaycrawler.quartz.utils;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronScheduleBuilder;

/**
 * Created by ucs_yuananyun on 2016/6/6.
 */
public class CronExpressionUtils {

    public static boolean isValidExpression(String cronExpression) {
        try {
            if (StringUtils.isBlank(cronExpression)) return false;
            CronScheduleBuilder cronBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static String convertToSpringCron(String cronExpression) {
        String[] cronArray = cronExpression.split(" ");
        if (cronArray.length < 6) return null;
        if (!"?".equals(cronArray[3])) cronArray[5] = "?";
        else cronArray[3] = "?";

        return StringUtils.join(cronArray, " ");
    }

}
