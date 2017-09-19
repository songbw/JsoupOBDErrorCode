package utils;

import bean.BZJTreeBean;
import bean.StandardBean;
import dao.BZJTreeDAO;
import dao.StandardDAO;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by song on 2017/9/16.
 */
public class DBUtil {
    private static SqlSessionFactory sqlSessionFactory ;

    static {

        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream("mybatis-config.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static List<BZJTreeBean> findTreeByUrl() {
        SqlSession session = sqlSessionFactory.openSession();
        List<BZJTreeBean> bzjTreeBeans = null;
        try {
            BZJTreeDAO bzjTreeDAO = session.getMapper(BZJTreeDAO.class);
            bzjTreeBeans = bzjTreeDAO.selectByUrl();

        } finally {
            session.close();
        }
        return bzjTreeBeans;
    }



    public static void addStandard(StandardBean standardBean) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            StandardDAO standardDAO = session.getMapper(StandardDAO.class) ;
            int i = standardDAO.addStandard(standardBean);
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            session.close();
        }
    }

    public static StandardBean getStandardLast() {
        SqlSession session = sqlSessionFactory.openSession();
        StandardBean standardBean = new StandardBean();
        try {
            StandardDAO standardDAO = session.getMapper(StandardDAO.class) ;
            standardBean = standardDAO.selectTopOne();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            session.close();
        }
        return standardBean;
    }

    public static int getStandardCountByParentId(long parentId) {
        SqlSession session = sqlSessionFactory.openSession();
        int count = 0;
        try {
            StandardDAO standardDAO = session.getMapper(StandardDAO.class) ;
            count = standardDAO.countByParentId(parentId);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            session.close();
        }
        return count;
    }

    public static List<StandardBean> findStandardAll() {
        SqlSession session = sqlSessionFactory.openSession();
        List<StandardBean> standardBeanList = new ArrayList<>() ;
        try {
            StandardDAO standardDAO = session.getMapper(StandardDAO.class) ;
            standardBeanList = standardDAO.selectAll();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            session.close();
        }
        return standardBeanList;
    }

    public static void updateFlag(StandardBean standardBean) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            StandardDAO standardDAO = session.getMapper(StandardDAO.class) ;
            int i = standardDAO.updateImgFlag(standardBean);
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            session.close();
        }
    }

    public static void updateStandardNameKey(StandardBean standardBean) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            StandardDAO standardDAO = session.getMapper(StandardDAO.class) ;
            int i = standardDAO.updateNameKey(standardBean);
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            session.close();
        }
    }

    public static void main(String[] args) {
        List<StandardBean> standardBeanList = findStandardAll();
        for (StandardBean standardBean : standardBeanList) {
            standardBean.setNameKey(standardBean.getCode() + standardBean.getName());
            updateStandardNameKey(standardBean);
        }
    }

}
