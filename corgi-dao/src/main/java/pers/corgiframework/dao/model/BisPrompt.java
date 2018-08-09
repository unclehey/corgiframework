package pers.corgiframework.dao.model;

/**
 * Created by syk on 2017/2/20.
 */
public class BisPrompt {
    private String bisStatus;
    private String bisMsg;
    private Object bisObj;

    public BisPrompt() {
        this.bisStatus = "1000";
        this.bisMsg = "操作成功";
    }

    public String getBisStatus() {
        return bisStatus;
    }

    public void setBisStatus(String bisStatus) {
        this.bisStatus = bisStatus;
    }

    public String getBisMsg() {
        return bisMsg;
    }

    public void setBisMsg(String bisMsg) {
        this.bisMsg = bisMsg;
    }

    public Object getBisObj() {
        return bisObj;
    }

    public void setBisObj(Object bisObj) {
        this.bisObj = bisObj;
    }
}
