package pers.corgiframework.tool.utils.wechat.pojo.menu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.corgiframework.tool.utils.JsonUtil;
import pers.corgiframework.tool.utils.wechat.WeixinUtil;

import java.util.Map;

/**
 * 微信公众号自定义菜单管理
 * Created by syk on 2017/3/13.
 */
public class MenuManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(MenuManager.class);

    /**
     * 1、自定义菜单最多包括3个一级菜单，每个一级菜单最多包含5个二级菜单。
     * 2、一级菜单最多4个汉字，二级菜单最多7个汉字，多出来的部分将会以“...”代替。
     * 3、创建自定义菜单后，菜单的刷新策略是，在用户进入公众号会话页或公众号profile页时，如果发现上一次拉取
     * 菜单的请求在5分钟以前，就会拉取一下菜单，如果菜单有更新，就会刷新客户端的菜单。测试时可以尝试取消关
     * 注公众账号后再次关注，则可以看到创建后的效果。
     *
     * 自定义菜单接口可实现多种类型按钮，如下type类型：
     * 1、click：点击推事件用户点击click类型按钮后，微信服务器会通过消息接口推送消息类型为event的结构给开发者（参考消息接口指南），并且带上按钮中开发者填写的key值，开发者可以通过自定义的key值与用户进行交互；
     * 2、view：跳转URL用户点击view类型按钮后，微信客户端将会打开开发者在按钮中填写的网页URL，可与网页授权获取用户基本信息接口结合，获得用户基本信息。
     * 3、scancode_push：扫码推事件用户点击按钮后，微信客户端将调起扫一扫工具，完成扫码操作后显示扫描结果（如果是URL，将进入URL），且会将扫码的结果传给开发者，开发者可以下发消息。
     * 4、scancode_waitmsg：扫码推事件且弹出“消息接收中”提示框用户点击按钮后，微信客户端将调起扫一扫工具，完成扫码操作后，将扫码的结果传给开发者，同时收起扫一扫工具，然后弹出“消息接收中”提示框，随后可能会收到开发者下发的消息。
     * 5、pic_sysphoto：弹出系统拍照发图用户点击按钮后，微信客户端将调起系统相机，完成拍照操作后，会将拍摄的相片发送给开发者，并推送事件给开发者，同时收起系统相机，随后可能会收到开发者下发的消息。
     * 6、pic_photo_or_album：弹出拍照或者相册发图用户点击按钮后，微信客户端将弹出选择器供用户选择“拍照”或者“从手机相册选择”。用户选择后即走其他两种流程。
     * 7、pic_weixin：弹出微信相册发图器用户点击按钮后，微信客户端将调起微信相册，完成选择操作后，将选择的相片发送给开发者的服务器，并推送事件给开发者，同时收起相册，随后可能会收到开发者下发的消息。
     * 8、location_select：弹出地理位置选择器用户点击按钮后，微信客户端将调起地理位置选择工具，完成选择操作后，将选择的地理位置发送给开发者的服务器，同时收起位置选择工具，随后可能会收到开发者下发的消息。
     * 9、media_id：下发消息（除文本消息）用户点击media_id类型按钮后，微信服务器会将开发者填写的永久素材id对应的素材下发给用户，永久素材类型可以是图片、音频、视频、图文消息。请注意：永久素材id必须是在“素材管理/新增永久素材”接口上传后获得的合法id。
     * 10、view_limited：跳转图文消息URL用户点击view_limited类型按钮后，微信客户端将打开开发者在按钮中填写的永久素材id对应的图文消息URL，永久素材类型只支持图文消息。请注意：永久素材id必须是在“素材管理/新增永久素材”接口上传后获得的合法id。
     * 请注意，3到8的所有事件，仅支持微信iPhone5.4.1以上版本，和Android5.4以上版本的微信用户，旧版本微信用户点击后将没有回应，开发者也不能正常接收到事件推送。9和10，是专门给第三方平台旗下未微信认证（具体而言，是资质认证未通过）的订阅号准备的事件类型，它们是没有事件推送的，能力相对受限，其他类型的公众号不必使用。
     * @return
     */
    private static Menu getMenu() {

        ViewButton btn11 = new ViewButton();
        btn11.setName("个人主页");
        btn11.setType("view");
        btn11.setUrl("http://www.shangyongkuan.com");

        ViewButton btn12 = new ViewButton();
        btn12.setName("Github");
        btn12.setType("view");
        btn12.setUrl("https://github.com/unclehey");

        ViewButton btn21 = new ViewButton();
        btn21.setName("百度");
        btn21.setType("view");
        btn21.setUrl("https://www.baidu.com/");

        CommonButton btn31 = new CommonButton();
        btn31.setName("联系我们");
        btn31.setType("click");
        btn31.setKey("31");

        ComplexButton mainBtn1 = new ComplexButton();
        mainBtn1.setName("嘿の大叔");
        mainBtn1.setSub_button(new Button[]{btn11, btn12});

        /*ComplexButton mainBtn2 = new ComplexButton();
        mainBtn2.setName("Github");
        mainBtn2.setSub_button(new Button[]{btn21, btn22});*/

        ComplexButton mainBtn3 = new ComplexButton();
        mainBtn3.setName("关于我们");
        mainBtn3.setSub_button(new Button[]{btn31});

        Menu menu = new Menu();
        menu.setButton(new Button[]{mainBtn1, btn21, mainBtn3});

        return menu;
    }

    public static void main(String[] args) {
        String appId = "xxx";
        String appSecret = "xxx";
        String token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
        String requestUrl = token_url.replace("APPID", appId).replace("APPSECRET", appSecret);
        Map<Object, Object> map = WeixinUtil.httpRequest(requestUrl, "GET", null);
        try {
            // 如果请求成功
            if (null != map) {
                Object errcode = map.get("errcode");
                if (errcode == null) {
                    // 调用接口创建菜单
                    // 拼装创建菜单的url
                    String menuUrl = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
                    menuUrl = menuUrl.replace("ACCESS_TOKEN", map.get("access_token").toString());
                    // 将菜单对象转换成json字符串
                    String jsonMenu = JsonUtil.objectToJson(getMenu());
                    // 调用接口创建菜单
                    Map<Object, Object> resultMap = WeixinUtil.httpRequest(menuUrl, "POST", jsonMenu);
                    if (null != resultMap) {
                        if (0 != Integer.parseInt(resultMap.get("errcode").toString())) {
                            LOGGER.info("菜单创建失败：" + resultMap.get("errmsg").toString());
                        } else {
                            LOGGER.info("菜单创建成功！");
                        }
                    }
                } else {
                    LOGGER.info("菜单创建失败：" + map.get("errmsg").toString());
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
