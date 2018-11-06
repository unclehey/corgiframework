package pers.corgiframework.dao.model;

/**
 * Created by syk on 2017/2/20.
 */
public class BisPrompt<T> {
    private String bisStatus;
    private String bisMsg;
    private T bisObj;

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

    public T getBisObj() {
        return bisObj;
    }

    public void setBisObj(T bisObj) {
        this.bisObj = bisObj;
    }
}
