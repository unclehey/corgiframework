package pers.corgiframework.admin.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pers.corgiframework.admin.annotation.NoNeedLogin;
import pers.corgiframework.dao.domain.MgrFunc;
import pers.corgiframework.dao.domain.MgrUser;
import pers.corgiframework.dao.domain.MgrUserExample;
import pers.corgiframework.dao.model.BisPrompt;
import pers.corgiframework.service.*;
import pers.corgiframework.tool.constants.BisPromptConstant;
import pers.corgiframework.tool.utils.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by syk on 2017/8/23.
 */
@Controller
public class MgrController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MgrController.class);

    // 从配置文件读取邮件发送url
    private static final String ACTIVATEURL = PropertiesUtil.getString("activate_url");
    private static final String RETRIEVEURL = PropertiesUtil.getString("retrieve_url");
    private static final String MGR_TITLE = "Corgi后台管理系统";

    @Autowired
    private IMgrUserService mgrUserService;
    @Autowired
    private IMgrFuncService mgrFuncService;
    @Autowired
    private IMgrDepartService mgrDepartService;
    @Autowired
    private IMgrDepartFuncService mgrDepartFuncService;
    @Autowired
    private IMgrUserDepartService mgrUserDepartService;
    @Autowired
    private IPublicService publicService;

    @NoNeedLogin
    @RequestMapping("/tologin")
    public String toLogin() {
        // 跳转到用户登录页面
        return "/mgr/user/login";
    }

    @NoNeedLogin
    @RequestMapping("/login")
    public String userLogin(HttpServletRequest request, ModelMap modelMap) {
        // 从配置文件读取token
        String token = PropertiesUtil.getString("global_token");
        HttpSession session = request.getSession();
        String randomCode = (String) session.getAttribute("randomCode");
        String account = request.getParameter("userNamePage");
        String userpwd = request.getParameter("pwdCodePage");
        String verifycode = request.getParameter("verifycodePage");
        if (StringUtils.isNotBlank(account) && StringUtils.isNotBlank(userpwd) && StringUtils.isNotBlank(verifycode)) {
            if (null != randomCode) {
                if (!randomCode.equals(verifycode)) {
                    modelMap.put("errorTips", "验证码错误！");
                    modelMap.put("userName", account);
                    return "/mgr/user/login";
                }
            }
            // 检测用户是否存在
            MgrUser mgrUser = mgrUserService.selectByAccount(account);
            if (mgrUser == null) {
                modelMap.put("errorTips", "对不起，该用户不存在！");
                return "/mgr/user/login";
            } else {
                // 加密算法:SHA加密
                int status = mgrUser.getStatus();
                if (status == 0 || status == 2) {
                    modelMap.put("errorTips", "对不起，该账号未激活！");
                    return "/mgr/user/login";
                }
                if (status == 3) {
                    modelMap.put("errorTips", "对不起，该账号已禁用！");
                    return "/mgr/user/login";
                }
                String sign = SignUtil.sha256(mgrUser.getAccount() + mgrUser.getPassword() + token);
                if (userpwd.equals(sign)) {
                    session.setAttribute("mgrUser", mgrUser);
                    // 根据用户角色不同加载相应权限
                    String acct = mgrUser.getAccount();
                    Map<String, Map<String, Object>> mgrFuncList = null;
                    if ("admin".equals(acct)) {
                        mgrFuncList = mgrFuncService.getAllToMap();
                    } else {
                        mgrFuncList = mgrFuncService.getByUserIdToMap(mgrUser.getId());
                    }
                    session.setAttribute("mgrFuncList", JsonUtil.objectToJson(mgrFuncList));
                    session.setAttribute("mgrFuncMap", mgrFuncList);
                    // 登录成功 更新最后登录时间
                    mgrUser.setLastLoginTime(DateTimeUtil.getNowDateTime());
                    mgrUserService.update(mgrUser);
                    return "redirect:index.do";
                } else {
                    modelMap.put("errorTips", "密码错误，请核对后重新输入！");
                    modelMap.put("userName", account);
                    return "/mgr/user/login";
                }
            }
        } else {
            return "/mgr/user/login";
        }
    }

    @NoNeedLogin
    @RequestMapping("/register")
    public void userRegister(HttpServletRequest request, HttpServletResponse response) {
        String email = request.getParameter("email").trim();
        String account = request.getParameter("account").trim();
        String realName = request.getParameter("realName").trim();
        String pass = request.getParameter("pass").trim();
        try {
            if (StringUtils.isNotBlank(email) && StringUtils.isNotBlank(account)
                    && StringUtils.isNotBlank(realName) && StringUtils.isNotBlank(pass)) {
                // 检测邮箱或账号是否存在
                MgrUser mgrUser = mgrUserService.selectByAccountOrEmail(account, email);
                if (null != mgrUser) {
                    // 邮箱或账号已存在
                    response.getWriter().write("1");
                } else {
                    // 激活码
                    String token = StringUtil.genUUID();
                    // 获取当前时间
                    LocalDateTime dateTime = DateTimeUtil.getNowDateTime();
                    // 有效期
                    LocalDateTime effectiveTime = DateTimeUtil.plus(dateTime, 2, DateTimeUtil.DAYS);
                    mgrUser = new MgrUser();
                    mgrUser.setAccount(account);
                    mgrUser.setEmail(email);
                    mgrUser.setRealName(realName);
                    mgrUser.setPassword(SignUtil.md5(account + pass));
                    mgrUser.setToken(token);
                    mgrUser.setStatus(0);
                    mgrUser.setCreateTime(dateTime);
                    mgrUser.setUpdateTime(dateTime);
                    mgrUser.setEffectiveTime(effectiveTime);
                    mgrUserService.insert(mgrUser);
                    // 调用邮件发送服务
                    String sendUrl = ACTIVATEURL + "?email=" + email + "&token=" + token;
                    String content = "<p>你好，<br>感谢你注册"+MGR_TITLE+"。<br>你的登录账号为：<font color='red'>" + account + "</font>。请在48小时内点击以下链接激活账号，<br><br>"
                            + "<a href='" + sendUrl + "'>" + sendUrl + "</a></p><br><br>如果以上链接无法点击，请将上面的地址复制到你的浏览器地址栏进入"+MGR_TITLE+"。";
                    publicService.sendRegisterMail(email, ""+MGR_TITLE+"账户激活", content);
                    response.getWriter().write("0");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @NoNeedLogin
    @RequestMapping("/activate")
    public String activate(HttpServletRequest request, ModelMap modelMap) {
        // 从配置文件读取token
        String email = request.getParameter("email").trim();
        String token = request.getParameter("token").trim();
        if (StringUtils.isNotBlank(email) && StringUtils.isNotBlank(token)) {
            // 检测用户是否存在
            MgrUserExample mgrUserExample = new MgrUserExample();
            mgrUserExample.createCriteria().andEmailEqualTo(email).andTokenEqualTo(token);
            MgrUser mgrUser = mgrUserService.getMgrUserByExample(mgrUserExample);
            if (mgrUser == null) {
                modelMap.put("errorTips", "对不起，该邮箱未注册或激活码已篡改！");
                return "/mgr/user/login";
            } else {
                int status = mgrUser.getStatus();
                if (status == 1) {
                    modelMap.put("errorTips", "该账号已激活，请直接登录！");
                    return "/mgr/user/login";
                } else {
                    // 判断激活时间是否有效
                    LocalDateTime now = DateTimeUtil.getNowDateTime();
                    LocalDateTime effectiveTime = mgrUser.getEffectiveTime();
                    if (effectiveTime.isBefore(now)) {
                        // 已过期 重新发送激活邮件
                        // 激活码
                        token = StringUtil.genUUID();
                        effectiveTime = DateTimeUtil.plus(now, 2, DateTimeUtil.DAYS);
                        mgrUser.setToken(token);
                        mgrUser.setUpdateTime(now);
                        mgrUser.setEffectiveTime(effectiveTime);
                        mgrUserService.update(mgrUser);
                        // 调用邮件发送服务
                        String sendUrl = ACTIVATEURL + "?email=" + email + "&token=" + token;
                        String content = "<p>你好，<br>感谢你注册"+MGR_TITLE+"。<br>你的登录账号为：<font color='red'>" + mgrUser.getAccount() + "</font>。请在48小时内点击以下链接激活账号，<br><br>"
                                + "<a href='" + sendUrl + "'>" + sendUrl + "</a></p><br><br>如果以上链接无法点击，请将上面的地址复制到你的浏览器地址栏进入"+MGR_TITLE+"。";
                        publicService.sendRegisterMail(email, ""+MGR_TITLE+"账户激活", content);
                        modelMap.put("dataTarget", "#signup-box");
                        return "/mgr/user/login";
                    } else {
                        // 修改账号状态为已激活并且自动登录
                        mgrUser.setStatus(1);
                        mgrUser.setUpdateTime(now);
                        mgrUser.setLastLoginTime(now);
                        mgrUserService.update(mgrUser);
                        HttpSession session = request.getSession();
                        session.setAttribute("mgrUser", mgrUser);
                        // 查询权限
                        Map<String, Map<String, Object>> mgrFuncList = mgrFuncService.getByUserIdToMap(mgrUser.getId());
                        session.setAttribute("mgrFuncList", JsonUtil.objectToJson(mgrFuncList));
                        session.setAttribute("mgrFuncMap", mgrFuncList);
                        return "redirect:index.do";
                    }
                }
            }
        } else {
            modelMap.put("errorTips", "对不起，参数有误请重试！");
            return "/mgr/user/login";
        }
    }

    @NoNeedLogin
    @RequestMapping("/sendretrieve")
    public String sendRetrieve(HttpServletRequest request, ModelMap modelMap) {
        // 从配置文件读取token
        String email = request.getParameter("retrieveEmail").trim();
        modelMap.put("dataTarget", "#forgot-box");
        if (StringUtils.isNotBlank(email)) {
            // 检测用户是否存在
            MgrUserExample mgrUserExample = new MgrUserExample();
            mgrUserExample.createCriteria().andEmailEqualTo(email);
            MgrUser mgrUser = mgrUserService.getMgrUserByExample(mgrUserExample);
            if (mgrUser == null) {
                modelMap.put("retrieveTips", "对不起，该邮箱未注册！");
                return "/mgr/user/login";
            } else {
                int status = mgrUser.getStatus();
                if (status != 1) {
                    modelMap.put("retrieveTips", "对不起，该账号未激活或已禁用！");
                    return "/mgr/user/login";
                } else {
                    // 获取当前时间
                    LocalDateTime now = DateTimeUtil.getNowDateTime();
                    // 激活码
                    String token = StringUtil.genUUID();
                    LocalDateTime effectiveTime = DateTimeUtil.plus(now, 2, DateTimeUtil.DAYS);
                    mgrUser.setToken(token);
                    mgrUser.setUpdateTime(now);
                    mgrUser.setEffectiveTime(effectiveTime);
                    mgrUserService.update(mgrUser);
                    // 调用邮件发送服务
                    String sendUrl = RETRIEVEURL + "?email=" + email + "&token=" + token;
                    String content = "<p>你好，<br>你正在为"+MGR_TITLE+"账号：<font color='red'>" + mgrUser.getAccount() + "</font>重置密码。请在48小时内点击以下链接重置密码，<br><br>"
                            + "<a href='" + sendUrl + "'>" + sendUrl + "</a></p><br><br>如果以上链接无法点击，请将上面的地址复制到你的浏览器地址栏进入"+MGR_TITLE+"。";
                    publicService.sendRetrievePwdMail(email, ""+MGR_TITLE+"密码找回", content);
                    modelMap.put("retrieveTips", "带有重置密码的链接已发送到您的邮箱！");
                    return "/mgr/user/login";
                }
            }
        } else {
            modelMap.put("retrieveTips", "对不起，参数有误请重试！");
            return "/mgr/user/login";
        }
    }

    @NoNeedLogin
    @RequestMapping("/toretrieve")
    public String toRetrieve(HttpServletRequest request, ModelMap modelMap) {
        // 从配置文件读取token
        String email = request.getParameter("email").trim();
        String token = request.getParameter("token").trim();
        if (StringUtils.isNotBlank(email) && StringUtils.isNotBlank(token)) {
            // 检测用户是否存在
            MgrUserExample mgrUserExample = new MgrUserExample();
            mgrUserExample.createCriteria().andEmailEqualTo(email).andTokenEqualTo(token);
            MgrUser mgrUser = mgrUserService.getMgrUserByExample(mgrUserExample);
            if (mgrUser == null) {
                modelMap.put("errorTips", "对不起，该邮箱未注册或参数已篡改！");
                return "/mgr/user/login";
            } else {
                int status = mgrUser.getStatus();
                if (status != 1) {
                    modelMap.put("errorTips", "对不起，该账号未激活或已禁用！");
                    return "/mgr/user/login";
                } else {
                    // 判断邮件时间是否有效
                    LocalDateTime now = DateTimeUtil.getNowDateTime();
                    LocalDateTime effectiveTime = mgrUser.getEffectiveTime();
                    if (effectiveTime.isBefore(now)) {
                        // 已过期 重新发送重置密码邮件
                        token = StringUtil.genUUID();
                        effectiveTime = DateTimeUtil.plus(now, 2, DateTimeUtil.DAYS);
                        mgrUser.setToken(token);
                        mgrUser.setUpdateTime(now);
                        mgrUser.setEffectiveTime(effectiveTime);
                        mgrUserService.update(mgrUser);
                        // 调用邮件发送服务
                        String sendUrl = RETRIEVEURL + "?email=" + email + "&token=" + token;
                        String content = "<p>你好，<br>你正在为"+MGR_TITLE+"账号：<font color='red'>" + mgrUser.getAccount() + "</font>重置密码。请在48小时内点击以下链接重置密码，<br><br>"
                                + "<a href='" + sendUrl + "'>" + sendUrl + "</a></p><br><br>如果以上链接无法点击，请将上面的地址复制到你的浏览器地址栏进入"+MGR_TITLE+"。";
                        publicService.sendRetrievePwdMail(email, ""+MGR_TITLE+"密码找回", content);
                        modelMap.put("dataTarget", "#forgot-box");
                        modelMap.put("retrieveTips", "链接已过期，重置密码邮件已发送！");
                        return "/mgr/user/login";
                    } else {
                        // 跳转到重置密码页面
                        modelMap.put("email", email);
                        modelMap.put("token", token);
                        return "/mgr/user/reset_pass";
                    }
                }
            }
        } else {
            modelMap.put("errorTips", "对不起，参数有误请重试！");
            return "/mgr/user/login";
        }
    }

    @NoNeedLogin
    @RequestMapping("/retrieve")
    public void retrieve(HttpServletRequest request, HttpServletResponse response) {
        String email = request.getParameter("email").trim();
        String token = request.getParameter("token").trim();
        String newPwd = request.getParameter("newPwd").trim();
        try {
            if (StringUtils.isNotBlank(email) && StringUtils.isNotBlank(token) && StringUtils.isNotBlank(newPwd)) {
                // 检测用户是否合法
                MgrUserExample mgrUserExample = new MgrUserExample();
                mgrUserExample.createCriteria().andEmailEqualTo(email).andTokenEqualTo(token);
                MgrUser mgrUser = mgrUserService.getMgrUserByExample(mgrUserExample);
                if (mgrUser == null) {
                    // 邮箱未注册或参数已篡改
                    response.getWriter().write("1");
                } else {
                    int status = mgrUser.getStatus();
                    if (status != 1) {
                        // 账号未激活
                        response.getWriter().write("2");
                    } else {
                        mgrUser.setPassword(SignUtil.md5(mgrUser.getAccount() + newPwd));
                        mgrUser.setUpdateTime(DateTimeUtil.getNowDateTime());
                        int flag = mgrUserService.update(mgrUser);
                        if (flag == 1) {
                            // 重置密码成功
                            response.getWriter().write("0");
                        } else {
                            // 重置密码失败
                            response.getWriter().write("3");
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute("mgrUser");
        session.invalidate();
        return "/mgr/user/login";
    }

    @RequestMapping("/index")
    public String index(HttpServletRequest request, ModelMap modelMap) {
        // 读取session
        HttpSession session = request.getSession(true);
        MgrUser mgrUser = (MgrUser) session.getAttribute("mgrUser");
        try {
            modelMap.put("mgrUser", mgrUser);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return "/mgr/user/index";
    }

    @RequestMapping("/mgr/user/list")
    public Object mgrUserList() {
        return "/mgr/user/list";
    }

    @RequestMapping("/mgr/user/list/json")
    @ResponseBody
    public Object mgrUserListJson(HttpServletRequest request)  {
        // 第几页
        String page = request.getParameter("page");
        // 每页记录数
        String rows = request.getParameter("rows");
        // 组装查询条件
        Map<String, Object> filtersMap = publicService.assembleSearchFilters(request);
        // 计算总页数
        int curpage = Integer.parseInt(page);
        int pageCount = Integer.parseInt(rows);
        // 返回数据
        Map<String, Object> responseMap = new HashMap<>();
        try {
            // 后台用户列表
            List<MgrUser> list = mgrUserService.selectListsByCondition(filtersMap);
            // 总的记录条数
            int totalCount = mgrUserService.selectListsCountByCondition(filtersMap);
            responseMap.put("records", totalCount);
            int total = (int) Math.ceil((double) totalCount / pageCount);
            responseMap.put("total", total);
            responseMap.put("page", curpage);
            // 返回数据
            responseMap.put("rows", list);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return responseMap;
    }

    @RequestMapping("/tomodifypass")
    public String toModifyPass() {
        return "/mgr/user/modify_pass";
    }

    @RequestMapping("/modifypass")
    public void modifyPass(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        MgrUser mgrUser = (MgrUser) session.getAttribute("mgrUser");
        String oldPwd = request.getParameter("oldPwd").trim();
        String newPwd = request.getParameter("newPwd").trim();
        try {
            if (null != mgrUser) {
                if (StringUtils.isNotBlank(oldPwd)) {
                    String oldPassword = SignUtil.md5(mgrUser.getAccount() + oldPwd);
                    if (!oldPassword.equals(mgrUser.getPassword())) {
                        // 原密码错误
                        response.getWriter().write("2");
                    } else {
                        if (StringUtils.isNotBlank(newPwd)) {
                            mgrUser.setPassword(SignUtil.md5(mgrUser.getAccount() + newPwd));
                            mgrUser.setUpdateTime(DateTimeUtil.getNowDateTime());
                            int flag = mgrUserService.update(mgrUser);
                            if (flag == 1) {
                                // 密码修改成功
                                response.getWriter().write("1");
                            } else {
                                // 密码修改失败
                                response.getWriter().write("0");
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @RequestMapping("/mgr/user/update")
    @ResponseBody
    public Object updateMgrUser(HttpServletRequest request) {
        String updateId = request.getParameter("updateId");
        String updEmail = request.getParameter("updEmail");
        String updRealName = request.getParameter("updRealName");
        String updStatus = request.getParameter("updStatus");
        BisPrompt bisPrompt = new BisPrompt();
        try {
            if (StringUtils.isNotBlank(updateId) && StringUtils.isNotBlank(updEmail) && StringUtils.isNotBlank(updRealName)) {
                MgrUser mgrUser = mgrUserService.selectByPrimaryKey(Integer.valueOf(updateId));
                if (null != mgrUser) {
                    mgrUser.setStatus(Integer.valueOf(updStatus));
                    mgrUser.setRealName(updRealName);
                    mgrUser.setEmail(updEmail);
                    mgrUser.setUpdateTime(DateTimeUtil.getNowDateTime());
                    int flag = mgrUserService.update(mgrUser);
                    if (flag == 1) {
                        // 更新成功
                        bisPrompt.setBisObj(mgrUser);
                    } else {
                        bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                        bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.FAIL_STATUS));
                    }
                } else {
                    // 非法请求
                    bisPrompt.setBisStatus(BisPromptConstant.INVALID_REQUEST_STATUS);
                    bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.INVALID_REQUEST_STATUS));
                }
            } else {
                // 非法请求
                bisPrompt.setBisStatus(BisPromptConstant.INVALID_REQUEST_STATUS);
                bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.INVALID_REQUEST_STATUS));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return bisPrompt;
    }

    @RequestMapping("/mgr/user/del")
    @ResponseBody
    public Object delMgrUser(HttpServletRequest request) {
        String delId = request.getParameter("delId");
        BisPrompt bisPrompt = new BisPrompt();
        try {
            if (StringUtils.isNotBlank(delId)) {
                Integer accountId = Integer.valueOf(delId);
                MgrUser mgrUser = mgrUserService.selectByPrimaryKey(accountId);
                if (null != mgrUser) {
                    int flag = mgrUserService.delete(accountId);
                    if (flag != 1) {
                        bisPrompt.setBisStatus(BisPromptConstant.FAIL_STATUS);
                        bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.FAIL_STATUS));
                    }
                } else {
                    // 非法请求
                    bisPrompt.setBisStatus(BisPromptConstant.INVALID_REQUEST_STATUS);
                    bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.INVALID_REQUEST_STATUS));
                }
            } else {
                // 非法请求
                bisPrompt.setBisStatus(BisPromptConstant.INVALID_REQUEST_STATUS);
                bisPrompt.setBisMsg(BisPromptConstant.BISPROMPT_MAP.get(BisPromptConstant.INVALID_REQUEST_STATUS));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return bisPrompt;
    }

    @NoNeedLogin
    @RequestMapping("/verifycode")
    public String verifyCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        VerifyCodeUtil.verifyCode(request, response);
        return null;
    }

    /**
     * 跳转到功能资源管理页面
     *
     * @param modelMap
     * @return
     */
    @RequestMapping("/mgr/func/list")
    public String mgrFuncList(HttpServletRequest request, ModelMap modelMap) {
        HttpSession session = request.getSession();
        MgrUser mgrUser = (MgrUser) session.getAttribute("mgrUser");
        Integer userId = mgrUser.getId();
        String account = mgrUser.getAccount();
        List<Map<String, Object>> funcList = null;
        if ("admin".equals(account)) {
            // 如果是超级管理员登录查询所有部门
            funcList = mgrFuncService.getTreeAll();
        } else {
            // 如果普通用户查询登录查询用户所在部门及其全部子部门
            funcList = mgrFuncService.getTreeByUserId(userId);
        }
        try {
            modelMap.put("mgrFuncList", JsonUtil.objectToJson(funcList));
            modelMap.put("account", account);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return "/mgr/func/list";
    }

    /**
     * 更新一个功能资源
     */
    @RequestMapping("/mgr/func/update")
    @ResponseBody
    public void mgrFuncUpdate(HttpServletRequest request) {
        String proId = request.getParameter("proId");
        String cnName = request.getParameter("cnName");
        String url = request.getParameter("url");
        String remark = request.getParameter("remark");
        String type = request.getParameter("type");
        String code = request.getParameter("code");
        if (code == null || "".equals(code)) {
            return;
        }
        if (cnName == null || "".equals(cnName)) {
            return;
        }
        if ("2".equals(type)) {
            if (url == null || "".equals(url)) {
                return;
            }
        }
        mgrFuncService.update(Integer.parseInt(proId), cnName, url, remark, type, code);
    }

    /**
     * 新增一个功能资源
     */
    @RequestMapping("/mgr/func/insert")
    @ResponseBody
    public String mgrFuncInsert(HttpServletRequest request) {
        HttpSession session = request.getSession();
        MgrUser mgrUser = (MgrUser) session.getAttribute("mgrUser");
        Integer userId = mgrUser.getId();
        String cnName = request.getParameter("cnName");
        String menuIcon = request.getParameter("menuIcon");
        String parentId = request.getParameter("parentId");
        String type = request.getParameter("type");
        String url = request.getParameter("url");
        String remark = request.getParameter("remark");
        remark = (remark == null || "".equals(remark)) ? "添加功能" : remark;
        String code = request.getParameter("code");
        if (code == null || "".equals(code)) {
            return "fails";
        }
        if (cnName == null || "".equals(cnName)) {
            return "fails";
        }
        if ("2".equals(type)) {
            if (url == null || "".equals(url)) {
                return "fails";
            }
        }
        try {
            mgrFuncService.insert(cnName, Integer.parseInt(parentId), type, url, remark, userId, code, menuIcon);
            return "succ";
        } catch (Exception e) {
            LOGGER.info(e.toString());
            return "fails";
        }
    }

    /**
     * 删除一个功能资源
     */
    @RequestMapping("/mgr/func/delete")
    @ResponseBody
    public String mgrFuncDelete(HttpServletRequest request) {
        String proId = request.getParameter("proId");
        MgrFunc mgrFunc = mgrFuncService.selectByPrimaryKey(Integer.parseInt(proId));
        if (mgrFunc == null) {
            return "fails";
        }
        String code = mgrFunc.getCode();
        if ("func_1".equals(code)
                || "func_1_1".equals(code)
                || "func_1_1_1".equals(code)
                || "func_1_1_2".equals(code)
                || "func_1_1_3".equals(code)) {
            return "fails";
        }
        try {
            mgrFuncService.delete(Integer.parseInt(proId));
            return "succ";
        } catch (Exception e) {
            LOGGER.debug(e.toString());
            return "fails";
        }
    }

    /**
     * 获取有复选款的所有功能资源树
     */
    @RequestMapping("/mgr/func/rules")
    @ResponseBody
    public String mgrFuncRules(HttpServletRequest request) {
        String departId = request.getParameter("departId");
        HttpSession session = request.getSession();
        MgrUser mgrUser = (MgrUser) session.getAttribute("mgrUser");
        Integer userId = mgrUser.getId();
        String account = mgrUser.getAccount();
        List<Map<String, Object>> funcList = null;
        //1、如果是超级管理员登录查询当前部门所用功能
        if ("admin".equals(account)) {
            funcList = mgrFuncService.getTreeAllCheck(departId);
            //2、根据当前登录用户id查询所给部门的所有功能资源
        } else {
            funcList = mgrFuncService.getTreeByUserIdCheck(userId, departId);
        }
        try {
            return JsonUtil.objectToJson(funcList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取有复选款的所有功能资源树
     */
    @RequestMapping("/mgr/func/funcs")
    @ResponseBody
    public String mgrFuncFuncs() {
        List<Map<String, Object>> funcList = mgrFuncService.getFuncs();
        try {
            return JsonUtil.objectToJson(funcList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 跳转到部门管理页面
     *
     * @param modelMap
     * @return
     */
    @RequestMapping("/mgr/depart/list")
    public String mgrDepartList(HttpServletRequest request, ModelMap modelMap) {
        HttpSession session = request.getSession();
        MgrUser mgrUser = (MgrUser) session.getAttribute("mgrUser");
        Integer userId = mgrUser.getId();
        String account = mgrUser.getAccount();
        List<Map<String, Object>> departList = null;
        if ("admin".equals(account)) {
            // 如果是超级管理员登录查询所有部门
            departList = mgrDepartService.getTreeAll();
        } else {
            // 如果普通用户查询登录查询用户所在部门及其全部子部门
            departList = mgrDepartService.getTreeByUserId(userId);
        }
        try {
            modelMap.put("mgrDepartList", JsonUtil.objectToJson(departList));
            modelMap.put("account", account);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return "/mgr/depart/list";
    }

    /**
     * 更新当前部门的名称及备注
     */
    @RequestMapping("/mgr/depart/update")
    @ResponseBody
    public void mgrDepartUpdate(HttpServletRequest request) {
        HttpSession session = request.getSession();
        MgrUser mgrUser = (MgrUser) session.getAttribute("mgrUser");
        Integer userId = mgrUser.getId();
        String account = mgrUser.getAccount();
        String proId = request.getParameter("proId");
        String cnName = request.getParameter("cnName");
        String remark = request.getParameter("remark");
        if ("admin".equals(account)) {
            mgrDepartService.update(Integer.parseInt(proId), cnName, remark);
        } else {
            if (mgrDepartService.isMyDepart(Integer.parseInt(proId), userId)) {
                mgrDepartService.update(Integer.parseInt(proId), cnName, remark);
            }
        }
    }

    /**
     * 在当前部门下新增一个子部门
     */
    @RequestMapping("/mgr/depart/insert")
    @ResponseBody
    public String mgrDepartInsert(HttpServletRequest request) {
        HttpSession session = request.getSession();
        MgrUser mgrUser = (MgrUser) session.getAttribute("mgrUser");
        Integer userId = mgrUser.getId();
        String cnName = request.getParameter("cnName");
        String parentId = request.getParameter("parentId");
        String type = request.getParameter("type");
        String remark = request.getParameter("remark");
        try {
            mgrDepartService.insert(cnName, Integer.parseInt(parentId), type, remark, userId);
            return "succ";
        } catch (Exception e) {
            LOGGER.info(e.toString());
            return "fails";
        }
    }

    /**
     * 删除当前部门及其子部门及相关关联
     */
    @RequestMapping("/mgr/depart/delete")
    @ResponseBody
    public String mgrDepartDelete(HttpServletRequest request) {
        HttpSession session = request.getSession();
        MgrUser mgrUser = (MgrUser) session.getAttribute("mgrUser");
        Integer userId = mgrUser.getId();
        String account = mgrUser.getAccount();
        String proId = request.getParameter("proId");
        try {
            if ("admin".equals(account)) {
                mgrDepartService.delete(proId);
            } else {
                if (mgrDepartService.isMyDepart(Integer.parseInt(proId), userId)) {
                    mgrDepartService.delete(proId);
                }
            }
            return "succ";
        } catch (Exception e) {
            LOGGER.info(e.toString());
            return "fails";
        }
    }

    /**
     * 获取登录用户所在部门极其子部门所属的所有用户
     */
    @RequestMapping("/mgr/depart/users")
    @ResponseBody
    public String mgrDepartUsers(HttpServletRequest request) {
        String departId = request.getParameter("departId");
        //HttpSession session = request.getSession();
        //MgrUser mgrUser = (MgrUser)session.getAttribute("mgrUser");
        //Integer userId = mgrUser.getId();
        //String account = mgrUser.getAccount();
        List<MgrUser> departList = null;
        //1、如果是超级管理员登录查询所有用户
        //if("admin".equals(account)){
        departList = mgrUserDepartService.getUser();
        /*//2、如果普通用户查询登录查询用户所在部门及其全部子部门的所有用户
        }else{
    		departList = mgrUserDepartService.getUserByUserId(String.valueOf(userId));
    	}*/
        List<MgrUser> checkedList = mgrUserDepartService.getUserByDepart(departId);
        Map<String, Object> departMap = new HashMap<String, Object>();
        departMap.put("departList", departList);
        departMap.put("checkedList", checkedList);
        try {
            return JsonUtil.objectToJson(departMap);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取登录用户所在部门极其子部门所属的所有用户
     */
    @RequestMapping("/mgr/depart/isMyDepart")
    @ResponseBody
    public String mgrDepartIsMyDepart(HttpServletRequest request) {
        String departId = request.getParameter("departId");
        HttpSession session = request.getSession();
        MgrUser mgrUser = (MgrUser) session.getAttribute("mgrUser");
        Integer userId = mgrUser.getId();
        String account = mgrUser.getAccount();
        Boolean result = true;
        if (!"admin".equals(account)) {
            result = mgrDepartService.isMyDepart(Integer.parseInt(departId), userId);
        }
        Map<String, Object> departMap = new HashMap<String, Object>();
        departMap.put("result", result);
        try {
            return result.toString();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return "false";
        }
    }

    /**
     * 跳转到部门管理页面
     *
     * @param modelMap
     * @return
     */
    @RequestMapping("/mgr/depart/getUserOnly")
    public String mgrDepartGetUserOnly(HttpServletRequest request, ModelMap modelMap) {
        HttpSession session = request.getSession();
        MgrUser mgrUser = (MgrUser) session.getAttribute("mgrUser");
        Integer userId = mgrUser.getId();
        String account = mgrUser.getAccount();
        List<Map<String, Object>> departList = null;
        //1、如果是超级管理员登录查询所有用户
        if ("admin".equals(account)) {
            departList = mgrUserDepartService.getAllDepartUserByUserId();
            //2、如果普通用户查询登录查询用户所在部门及其全部子部门的所有用户
        } else {
            departList = mgrUserDepartService.getDepartUserByUserId(String.valueOf(userId));
        }
        try {
            modelMap.put("userList", JsonUtil.objectToJson(departList));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return "/mgr/depart/user_list";
    }

    /**
     * 给所选部门赋予功能权限
     */
    @RequestMapping("/mgr/depart/givePerm")
    @ResponseBody
    public String mgrDepartGivePerm(HttpServletRequest request) {
        String funcIds = request.getParameter("funcIds");
        String departId = request.getParameter("departId");
        try {
            mgrDepartFuncService.givePerm(funcIds, departId);
            return "succ";
        } catch (Exception e) {
            return "fails";
        }
    }

    /**
     * 给所选部门添加用户
     */
    @RequestMapping("/mgr/depart/giveUser")
    @ResponseBody
    public String mgrDepartGiveUser(HttpServletRequest request) {
        String userIds = request.getParameter("userIds");
        String departId = request.getParameter("departId");
        try {
            mgrUserDepartService.giveUser(userIds, departId);
            return "succ";
        } catch (Exception e) {
            return "fails";
        }
    }

}
