package pers.corgiframework.tool.utils.wechat.pojo.message;

/**
 * Created by syk on 2017/3/13.
 */
public class ImageMessage extends BaseMessage {
    // 回复的消息id
    private Image Image;

    public Image getImage() {
        return Image;
    }

    public void setImage(Image image) {
        Image = image;
    }
}
