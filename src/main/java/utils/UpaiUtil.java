package utils;

import main.java.com.UpYun;

import java.io.File;
import java.io.IOException;

/**
 * Created by sbw22 on 2017/9/18.
 */
public class UpaiUtil {

    private static UpYun upYun = null;

    static {
        upYun = new UpYun("biaozhunjian","biaozhunjian","biaozhunjian@123") ;
        upYun.setTimeout(60);
        upYun.setApiDomain(UpYun.ED_AUTO);
    }

    public static boolean uploadImg(String filePath) {
        File file = new File("./img/" + filePath) ;
        try {
            upYun.setContentMD5(UpYun.md5(file));
            boolean result = upYun.writeFile(filePath, file, true);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean delImg(String filePath) {
        return upYun.deleteFile(filePath);
    }

    public static void main(String[] args) {
        System.out.println(15/20);


    }

}
