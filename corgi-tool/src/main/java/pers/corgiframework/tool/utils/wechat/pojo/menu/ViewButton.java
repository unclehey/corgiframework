package pers.corgiframework.tool.utils.wechat.pojo.menu;

/**
 * Created by syk on 2017/3/13.
 */
public class ViewButton extends Button {
    private String type;
    private String url;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
