package pers.corgiframework.tool.enums;

/**
 * 功能权限枚举
 * Created by syk on 2017/8/23.
 */
public enum MgrFuncEnum {

    // 系统管理
    FUNC_0("func_0", "系统管理", "1", "", "fa fa-cogs"),
    FUNC_0_1("func_0_1", "配置管理", "1", "", "fa fa-wrench"),
    FUNC_0_1_1("func_0_1_1", "配置列表", "2", "sys/properties/list.do", ""),
    FUNC_0_1_1_1("func_0_1_1_1", "配置列表-新增", "3", "", ""),
    FUNC_0_1_1_2("func_0_1_1_2", "配置列表-修改", "3", "", ""),
    FUNC_0_1_1_3("func_0_1_1_3", "配置列表-删除", "3", "", ""),
    FUNC_0_2("func_0_2", "分类管理", "1", "", "fa fa-bars"),
    FUNC_0_2_1("func_0_2_1", "分类列表", "2", "sys/category/list.do", ""),
    FUNC_0_2_1_1("func_0_2_1_1", "分类列表-增加", "3", "", ""),
    FUNC_0_2_1_2("func_0_2_1_2", "分类列表-修改", "3", "", ""),
    FUNC_0_3("func_0_3", "价格管理", "1", "", "fa fa fa-jpy"),
    FUNC_0_3_1("func_0_3_1", "价格列表", "2", "sys/price/list.do", ""),
    FUNC_0_3_1_1("func_0_3_1_1", "价格列表-添加", "3", "", ""),
    FUNC_0_3_1_2("func_0_3_1_2", "价格列表-修改", "3", "", ""),
    FUNC_0_3_1_3("func_0_3_1_3", "价格列表-删除", "3", "", ""),

    // 权限管理
    FUNC_1("func_1", "权限管理", "1", "", "fa fa-key"),
    FUNC_1_1("func_1_1", "功能资源列表", "2", "mgr/func/list.do", ""),
    FUNC_1_1_1("func_1_1_1", "新增功能", "3", "", ""),
    FUNC_1_1_2("func_1_1_2", "修改功能", "3", "", ""),
    FUNC_1_1_3("func_1_1_3", "删除功能及所有子功能", "3", "", ""),
    FUNC_1_2("func_1_2", "部门列表", "2", "mgr/depart/list.do", ""),
    FUNC_1_2_1("func_1_2_1", "新增部门", "3", "", ""),
    FUNC_1_2_2("func_1_2_2", "修改部门", "3", "", ""),
    FUNC_1_2_3("func_1_2_3", "删除部门及所有子部门", "3", "", ""),
    FUNC_1_2_4("func_1_2_4", "分配权限", "3", "", ""),
    FUNC_1_2_5("func_1_2_5", "分配人员", "3", "", ""),
    FUNC_1_3("func_1_3", "部门用户列表", "2", "mgr/depart/getUserOnly.do", ""),

    // 用户管理
    FUNC_2("func_2", "用户管理", "1", "", "fa fa-user"),
    /*FUNC_2_1("func_2_1", "用户列表", "2", "user/list.do", ""),
    FUNC_2_1_1("func_2_1_1", "用户列表-禁用", "3", "", ""),
    FUNC_2_1_2("func_2_1_2", "用户列表-加黑", "3", "", ""),*/
    FUNC_2_2("func_2_2", "后台用户列表", "2", "mgr/user/list.do", ""),
    FUNC_2_2_1("func_2_2_1", "后台用户列表-修改", "3", "", ""),
    FUNC_2_2_2("func_2_2_2", "后台用户列表-删除", "3", "", ""),

    // APP管理
    FUNC_3("func_3", "APP管理", "1", "", "fa fa-tablet"),
    // 图片管理
    FUNC_3_1("func_3_1", "图片管理", "1", "", "fa fa-picture-o"),
    FUNC_3_1_1("func_3_1_1", "图片列表", "2", "app/banner/list.do", ""),
    FUNC_3_1_1_1("func_3_1_1_1", "图片列表-上传", "3", "", ""),
    FUNC_3_1_1_2("func_3_1_1_2", "图片列表-修改", "3", "", ""),
    FUNC_3_1_1_3("func_3_1_1_3", "图片列表-删除", "3", "", ""),
    // 版本管理
    FUNC_3_2("func_3_2", "版本管理", "1", "", "fa fa-arrow-circle-up"),
    FUNC_3_2_1("func_3_2_1", "版本列表", "2", "app/version/list.do", ""),
    FUNC_3_2_1_1("func_3_2_1_1", "版本列表-新增", "3", "", ""),
    FUNC_3_2_1_2("func_3_2_1_2", "版本列表-修改", "3", "", ""),
    FUNC_3_2_1_3("func_3_2_1_3", "版本列表-删除", "3", "", ""),
    // 规则管理
    FUNC_3_3("func_3_3", "规则管理", "1", "", "fa fa-bullhorn"),
    FUNC_3_3_1("func_3_3_1", "规则列表", "2", "app/rule/list.do", ""),
    FUNC_3_3_1_1("func_3_3_1_1", "规则列表-新增", "3", "", ""),
    FUNC_3_3_1_2("func_3_3_1_2", "规则列表-修改", "3", "", ""),
    FUNC_3_3_1_3("func_3_3_1_3", "规则列表-删除", "3", "", ""),

    // 订单管理
    FUNC_4("func_4","订单管理","1","", "fa fa-indent"),
    FUNC_4_1("func_4_1", "订单列表", "2", "order/list.do", ""),
    FUNC_4_1_1("func_4_1_1", "订单列表-部分退款", "3", "", ""),
    FUNC_4_1_2("func_4_1_2", "订单列表-全额退款", "3", "", "");

    private final String code;
    private final String cnName;
    private final String type;
    private final String url;
    private final String icon;

    MgrFuncEnum(String code, String cnName, String type, String url, String icon) {
        this.code = code;
        this.cnName = cnName;
        this.type = type;
        this.url = url;
        this.icon = icon;
    }

    public String getCode() {
        return code;
    }

    public String getCnName() {
        return cnName;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public String getIcon() {
        return icon;
    }
}
