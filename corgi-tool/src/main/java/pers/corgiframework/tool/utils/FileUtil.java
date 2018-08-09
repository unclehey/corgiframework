package pers.corgiframework.tool.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * 文件操作工具类
 * Created by syk on 2018/7/24.
 */
public class FileUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 上传图片
     * @param file
     * @param objectId
     * @param savePath
     * @return
     */
    public static String upload(MultipartFile file, Integer objectId, String savePath) {
        String returnStr = "";
        try {
            // 如果文件大小为0，则直接忽略
            if (file.getSize() > 0) {
                // 文件名
                String fileName = file.getOriginalFilename();
                // 后缀名
                String suffix = fileName.indexOf(".") != -1 ? fileName.substring(fileName.lastIndexOf("."), fileName.length()) : null;
                suffix = suffix.toLowerCase();
                // 文件保存目录
                String realPath = savePath;
                if (null != objectId) {
                    realPath = savePath + objectId + File.separator;
                }
                // 判断目录是否存在 如不存在则新建
                File newFile = new File(realPath);
                if (!newFile.exists()) {
                    newFile.mkdirs();
                }
                // 生成文件名
                String fileNameStr = StringUtil.createFileNameStr();
                String trueFileName = fileNameStr + suffix;
                // 设置存放图片文件的路径
                String path = realPath + trueFileName;
                File realFile = new File(path);
                // 转存文件到指定的路径
                file.transferTo(realFile);
                returnStr = trueFileName;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return returnStr;
    }

    /**
     * 根据网络地址保存图片
     * @param urlString 网络地址
     * @param savePath 图片存储路径
     * @return
     */
    public static String downloadFromWeb(String urlString, String savePath) {
        String returnStr = "";
        try {
            // 构造URL
            URL url = new URL(urlString);
            // 打开连接
            URLConnection conn = url.openConnection();
            // 设置请求超时为5s
            conn.setConnectTimeout(5*1000);
            // 输入流
            InputStream is = conn.getInputStream();
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 文件保存目录
            String realPath = savePath + "/";
            // 判断目录是否存在 如不存在则新建
            File newFile = new File(realPath);
            if (!newFile.exists()) {
                newFile.mkdirs();
            }
            // 生成文件名
            String fileNameStr = StringUtil.createFileNameStr();
            String trueFileName = fileNameStr + ".jpg";
            // 设置存放图片文件的路径
            String path = realPath + trueFileName;
            returnStr = trueFileName;
            OutputStream os = new FileOutputStream(path);
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            // 完毕，关闭所有链接
            os.close();
            is.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return returnStr;
    }

    /**
     * fileToByte
     * @param filePath
     * @return
     */
    public static byte[] fileToByte(String filePath) {
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            baos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                baos.write(b, 0, n);
            }
            buffer = baos.toByteArray();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (null != fis) {
                try {
                    fis.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            if (null != baos) {
                try {
                    baos.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return buffer;
    }

    /**
     * fileToBase64Str
     * @param filePath
     * @return
     */
    public static String fileToBase64Str(String filePath) {
        byte[] buffer = fileToByte(filePath);
        String img = Base64Util.encode(buffer).trim().replaceAll("[\\s*\t\n\r]", "");
        return img;
    }

    /**
     * multipartFileToByte
     * @param file
     * @return
     */
    public static byte[] multipartFileToByte(MultipartFile file) {
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        byte[] buffer = null;
        try {
            is = file.getInputStream();
            baos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = is.read(b)) != -1) {
                baos.write(b, 0, n);
            }
            buffer = baos.toByteArray();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            if (null != baos) {
                try {
                    baos.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return buffer;
    }

    /**
     * multipartFileToBase64Str
     * @param file
     * @return
     */
    public static String multipartFileToBase64Str(MultipartFile file) {
        byte[] buffer = multipartFileToByte(file);
        String img = Base64Util.encode(buffer).trim().replaceAll("[\\s*\t\n\r]", "");
        return img;
    }

    /**
     * byteToFile
     * @param buf
     * @param filePath
     * @param fileName
     */
    public static void byteToFile(byte[] buf, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(buf);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (null != bos) {
                try {
                    bos.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * base64StrToFile
     * @param base64Str
     * @param filePath
     * @param fileName
     */
    public static void base64StrToFile(String base64Str, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            byte[] buffer = Base64Util.decode(base64Str.trim().replaceAll("[\\s*\t\n\r]", ""));
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(buffer);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (null != bos) {
                try {
                    bos.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 根据路径读取文件
     * @param filePath
     * @return
     */
    public static String readPathToString(String filePath) {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader reader = null;
        StringBuffer sb = new StringBuffer();
        try {
            fis = new FileInputStream(new File(filePath));
            isr = new InputStreamReader(fis, "utf-8");
            reader = new BufferedReader(isr);
            String readLine = null;
            while ((readLine = reader.readLine()) != null) {
                sb.append(readLine).append("\n");
            }
            // 关闭流
            fis.close();
            isr.close();
            reader.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return sb.toString();
    }

    /**
     * 根据流读取文件
     * @param request
     * @return
     */
    public static String readToString(HttpServletRequest request) {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader reader = null;
        StringBuffer sb = new StringBuffer();
        try {
            // 获取输入流
            is = request.getInputStream();
            isr = new InputStreamReader(is, "utf-8");
            reader = new BufferedReader(isr);
            String readLine = null;
            while ((readLine = reader.readLine()) != null) {
                sb.append(readLine);
            }
            // 关闭流
            is.close();
            isr.close();
            reader.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return sb.toString();
    }

    /**
     * 重命名
     * @param path
     * @param from
     * @param to
     */
    public static void reName(String path, String from, String to) {
        File f = new File(path);
        File[] fs = f.listFiles();
        for (int i = 0; i < fs.length; ++i) {
            File f2 = fs[i];
            if (f2.isDirectory()) {
                reName(f2.getPath(), from, to);
            } else {
                String name = f2.getName();
                if (name.endsWith(from)) {
                    f2.renameTo(new File(f2.getParent() + File.separator + name.substring(0, name.indexOf(from)) + to));
                }
            }
        }
    }

    /**
     *过滤存在的乱码
     */
    public static String doFilter(String str) {
        StringBuffer str_Result = new StringBuffer("");
        String str_OneStr = "";
        for (int z = 0; z < str.length(); z++) {
            str_OneStr = str.substring(z, z + 1);
            if (str_OneStr.matches("[\u4e00-\u9fa5]+") || str_OneStr.matches("[\\x00-\\x7F]+")) {
                str_Result.append(str_OneStr);
            }
        }
        return str_Result.toString();
    }
}
