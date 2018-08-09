package pers.corgiframework.tool.utils.wechat.pojo.message;

/**
 * Created by syk on 2017/3/13.
 */
public class TextMessage extends BaseMessage {
    // 回复的消息内容
    private String Content;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
