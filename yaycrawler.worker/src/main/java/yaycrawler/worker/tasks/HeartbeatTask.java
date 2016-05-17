package yaycrawler.worker.tasks;

import yaycrawler.worker.communication.MasterActor;

/**
 * Created by ucs_yuananyun on 2016/5/13.
 */
public class HeartbeatTask {
    public HeartbeatTask() {

    }
    private MasterActor masterActor;

    public void sendHeartbeart() {
        masterActor.sendHeartbeart();
    }

    public void setMasterActor(MasterActor masterActor) {
        this.masterActor = masterActor;
    }
}
