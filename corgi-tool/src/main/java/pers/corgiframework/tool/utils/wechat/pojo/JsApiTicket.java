package pers.corgiframework.tool.utils.wechat.pojo;

import java.io.Serializable;

/**
 * Created by syk on 2017/3/13.
 */
public class JsApiTicket implements Serializable {
    // 获取到的ticket
    private String ticket;
    // 凭证有效时间，单位：秒
    private int expiresIn;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }
}
