package pers.corgiframework.tool.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * Created by syk on 2018/7/24.
 */
public class StringUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringUtil.class);
    /**
     * 基本字符
     */
    private static final String numberChar = "0123456789";
    private static final String chars = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 根据长度生成随机数
     * @param length
     * @return
     */
    public static String generateRandomNumber(int length) {
        StringBuffer sb = new StringBuffer();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(numberChar.charAt(random.nextInt(numberChar.length())));
        }
        return sb.toString();
    }

    /**
     * 根据长度生成随机字符串
     * @param length
     * @return
     */
    public static String generateRandomString(int length) {
        StringBuffer sb = new StringBuffer();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * 根据日期生成文件名
     * @return
     */
    public static String createFileNameStr() {
        StringBuffer sb = new StringBuffer();
        String code = generateRandomNumber(3);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String filename = sdf.format(new Date());
        sb.append(filename);
        sb.append(code);
        return sb.toString();
    }

    /**
     * 字符串格式化拼接：code输入字符串 begin起始位数 middle中间加*号的位数 end最后保留的位数
     * @param code
     * @param begin
     * @param middle
     * @param end
     * @return
     */
    public static String codeFormat(String code, int begin, int middle, int end) {
        StringBuffer sb = new StringBuffer();
        if (code != null && !code.trim().equals("")) {
            int length = code.length();
            sb.append(code.substring(0, begin));
            if (middle != 0) {
                for (int i = 1; i <= middle; i++) {
                    sb.append("*");
                }
            }
            sb.append(code.substring(length - end, length));
        }
        return sb.toString();
    }

    /**
     * 生成16位的随机字符串
     * @return
     */
    public static String createNoncestr() {
        String res = "";
        for (int i = 0; i < 16; i++) {
            Random rd = new Random();
            res += chars.charAt(rd.nextInt(chars.length() - 1));
        }
        return res;
    }

    /**
     * 生成签名的时间戳
     */
    public static String createTimestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }

    /**
     * 去掉字符串中的特殊字符
     * @param text
     * @return
     */
    public static String parseStringParam(String text){
        if (StringUtils.isNotBlank(text)) {
            text = text.replaceAll("<[^>]+>", "").replaceAll("[\',\\\\,/]", "").trim();
            if(StringUtils.isNotBlank(text)){
                return text;
            }
            return null;
        }
        return null;
    }

    /**
     * 正则校验手机号
     * @param text
     * @return
     */
    public static String parsePhoneParam(String text) {
        String str = parseStringParam(text);
        if (str == null) {
            return null;
        }
        Pattern p = Pattern.compile("^(13[0-9]|14[1,4-9]|15[0-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$");
        Matcher m = p.matcher(str);
        if(m.matches()){
            return str;
        }
        return null;
    }

    /**
     * 正则校验密码：8-20位至少包含英文、数字、符号其中两种组合
     * @param text
     * @return
     */
    public static String parsePassParam(String text) {
        String str = parseStringParam(text);
        if (str == null) {
            return null;
        }
        String reg = "^(?![a-zA-Z]+$)(?![\\d]+$)(?![!@#$%^&*_]+$)[a-zA-Z\\d!@#$%^&*_]{8,20}$";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(str);
        if(m.matches()){
            return str;
        }
        return null;
    }

    /**
     * 生成邀请码
     * @return
     */
    public static String generateInviteCode(){
        String base = "qwertyuiopasdfghjklzxcvbnm";
        int length = 6;
        SecureRandom random = new SecureRandom();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 获取UUID
      * @return
     */
    public static String genUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取IP地址
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if(ip.indexOf(",")!=-1){
            String[] temp = ip.split(",");
            for(int i=0;i<temp.length;i++){
                if("unknown".equalsIgnoreCase(temp[i].trim()))
                    continue;
                ip = temp[i].trim();
                break;
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            return "127.0.0.1";
        }
        return ip;
    }

    /**
     * 基于Luhn算法的银行卡卡号格式校验
     * @param cardNo
     * @return
     */
    public static boolean matchLuhn(String cardNo) {
        int[] cardNoArr = new int[cardNo.length()];
        for (int i = 0; i < cardNo.length(); i++) {
            cardNoArr[i] = Integer.valueOf(String.valueOf(cardNo.charAt(i)));
        }
        for (int i = cardNoArr.length - 2; i >= 0; i -= 2) {
            cardNoArr[i] <<= 1;
            cardNoArr[i] = cardNoArr[i] / 10 + cardNoArr[i] % 10;
        }
        int sum = 0;
        for (int i = 0; i < cardNoArr.length; i++) {
            sum += cardNoArr[i];
        }
        return sum % 10 == 0;
    }

    /**
     * 判断身份证号是否合法
     * @param idno
     * @return
     */
    public static boolean isIdno(String idno) {
        String str = parseStringParam(idno);
        if (str == null) {
            return false;
        }
        String reg = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(str);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断字符串是否是数
     * @param tel
     * @return
     */
    public static boolean isNumeric(String tel) {
        String str = parseStringParam(tel);
        if (str == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 判断金额是否合法
     * @param amount
     * @return
     */
    public static boolean isAmount(String amount) {
        String str = parseStringParam(amount);
        if (str == null) {
            return false;
        }
        String reg = "^(([1-9][0-9]*)|(([0]\\.\\d{1,2}|[1-9][0-9]*\\.\\d{1,2})))$";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(str);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

}
