package pers.corgiframework.tool.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.SecureRandom;

/**
 * 图片验证码工具类
 * Created by syk on 2017/8/23.
 */
public class VerifyCodeUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyCodeUtil.class);

    public static void verifyCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int width = 60;
        int height = 20;
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = buffImg.createGraphics();

        SecureRandom random = new SecureRandom();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        Font font = new Font("Times New Roman", Font.PLAIN, 18);
        g.setFont(font);

        g.setColor(Color.BLACK);
        g.drawRect(0, 0, width - 1, height - 1);

        g.setColor(Color.GRAY);
        for (int i = 0; i < 30; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int x1 = random.nextInt(12);
            int y1 = random.nextInt(12);
            g.drawLine(x, y, x + x1, y + y1);
        }

        StringBuffer randomCode = new StringBuffer();
        int red = 0, green = 0, blue = 0;
        for (int i = 0; i < 4; i++) {
            String strRand = String.valueOf(random.nextInt(10));
            red = random.nextInt(110);
            green = random.nextInt(50);
            blue = random.nextInt(50);
            g.setColor(new Color(red, green, blue));
            g.drawString(strRand, 13 * i + 6, 16);

            randomCode.append(strRand);
        }

        request.getSession().setAttribute("randomCode", randomCode.toString());

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");

        ServletOutputStream sos = response.getOutputStream();
        ImageIO.write(buffImg, "jpeg", sos);
        sos.close();
    }

}
