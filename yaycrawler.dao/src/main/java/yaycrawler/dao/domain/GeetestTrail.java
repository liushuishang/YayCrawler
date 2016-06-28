package yaycrawler.dao.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by ucs_yuananyun on 2016/6/27.
 */
@Entity
@Table(name = "res_geetest_trail")
public class GeetestTrail {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    private Integer deltaX;
    @Column(name = "trailArray", columnDefinition = "text")
    private String trailArray;

    private Integer failureTimes;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getDeltaX() {
        return deltaX;
    }

    public void setDeltaX(Integer deltaX) {
        this.deltaX = deltaX;
    }

    public String getTrailArray() {
        return trailArray;
    }

    public void setTrailArray(String trailArray) {
        this.trailArray = trailArray;
    }

    public int getFailureTimes() {
        return failureTimes;
    }

    public void setFailureTimes(int failureTimes) {
        this.failureTimes = failureTimes;
    }

}
