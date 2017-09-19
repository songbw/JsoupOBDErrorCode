package utils;

import bean.StandardBean;
import main.java.com.UpYun;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
        List<StandardBean> standardBeanList = DBUtil.findStandardAll();
        for (StandardBean standardBean : standardBeanList) {
            if (standardBean.getImgPath() != null && (!"".equals(standardBean.getImgPath()))) {
                if (uploadImg(standardBean.getImgPath())) {
                    standardBean.setImgPathFlag(true);
                }
            }
            if (standardBean.getSmallImgPath() != null && (!"".equals(standardBean.getSmallImgPath()))) {
                if (uploadImg(standardBean.getSmallImgPath())) {
                    standardBean.setSmallImgFlag(true);
                }
            }
            if (standardBean.getSmallImgPathT() != null && (!"".equals(standardBean.getSmallImgPathT()))) {
                if (uploadImg(standardBean.getSmallImgPathT())) {
                    standardBean.setSmallImgTFlag(true);
                }
            }
            DBUtil.updateFlag(standardBean);
        }
//        uploadImg("small/1505720441965.jpg");
    }

}
