package androidstudio.killvirus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Writer : 王苦苦
 * <p/>
 * Copyright : 王宏伟
 * <p/>
 * Describe : 获取文件的MD5特征码，用于比对数据库中的病毒特征码，若匹配则为病毒
 * <p/>
 * Created : 2015 ; 18701 ; 2015/12/4.
 */
public class MD5Utils {
    /**
     * 获取到文件的MD5值（病毒特征码）
     *
     * @param sourceDir 文件路径
     * @return 返回文件的MD5特征码
     */
    public static String getAppMD5(String sourceDir) {
        File file = new File(sourceDir);
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = -1;
            MessageDigest messageDigest = MessageDigest.getInstance("md5");//获取到md5数字摘要
            while ((len = fis.read(buffer)) != -1) {
                messageDigest.update(buffer, 0, len);
            }
            byte[] digest = messageDigest.digest();

            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : digest) {
                int number = b & 0xff;
                String hex = Integer.toHexString(number);
                if (hex.length() == 1) {
                    stringBuffer.append("0" + hex);
                } else {
                    stringBuffer.append(hex);
                }
            }
            return stringBuffer.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
