package pers.corgiframework.tool.utils.wechat.pojo.menu;

/**
 * Created by syk on 2017/3/13.
 */
public class ComplexButton extends Button {
    private Button[] sub_button;

    public Button[] getSub_button() {
        return sub_button;
    }

    public void setSub_button(Button[] sub_button) {
        this.sub_button = sub_button;
    }
}
