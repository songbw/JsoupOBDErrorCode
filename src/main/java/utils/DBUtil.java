package utils;

import bean.BZJTreeBean;
import dao.BZJTreeDAO;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
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

}
