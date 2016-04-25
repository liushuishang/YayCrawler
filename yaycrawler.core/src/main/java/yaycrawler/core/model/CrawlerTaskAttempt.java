package yaycrawler.core.model;

/**
 * 表示CrawlerTask的一次尝试
 * Created by yuananyun on 2016/4/25.
 */
public class CrawlerTaskAttempt {
    private CrawlerTask crawlerTask;
    private TaskStatusEnum status;
    private String location;

    public CrawlerTaskAttempt(CrawlerTask crawlerTask) {
        this.crawlerTask = crawlerTask;
        status = TaskStatusEnum.init;
    }

    public CrawlerTask getCrawlerTask() {
        return crawlerTask;
    }

    public void setCrawlerTask(CrawlerTask crawlerTask) {
        this.crawlerTask = crawlerTask;
    }

    public TaskStatusEnum getStatus() {
        return status;
    }

    public void setStatus(TaskStatusEnum status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
