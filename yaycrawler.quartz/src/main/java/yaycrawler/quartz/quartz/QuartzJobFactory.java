package yaycrawler.quartz.quartz;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yaycrawler.quartz.model.Constant;
import yaycrawler.quartz.model.IExecutable;

/**
 * 有状态的Job工厂类
 * Created by ucs_yuananyun on 2016/6/6.
 */
@DisallowConcurrentExecution
public class QuartzJobFactory implements Job {
    private static Logger logger = LoggerFactory.getLogger(QuartzJobFactory.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Object job = context.getMergedJobDataMap().get(Constant.JOB_PARAM_KEY);
        if (job != null && job instanceof IExecutable) {
            if (((IExecutable) job).execute(context))
                logger.info("任务{}执行成功……", job.toString());
            else logger.info("任务{}执行失败！", job.toString());
        }
    }
}
