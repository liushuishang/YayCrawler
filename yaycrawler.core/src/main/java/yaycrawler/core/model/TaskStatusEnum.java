package yaycrawler.core.model;

/**
 * Created by yuananyun on 2016/4/25.
 */
public enum TaskStatusEnum {
    init(0), running(1), failure(2), success(3);
    private int value;

    TaskStatusEnum(int value) {
        this.value = value;
    }
}
