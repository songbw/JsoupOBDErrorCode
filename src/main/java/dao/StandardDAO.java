package dao;

import bean.StandardBean;

import java.util.List;

/**
 * Created by sbw22 on 2017/9/18.
 */
public interface StandardDAO {

    Integer addStandard(StandardBean standardBean) ;

    StandardBean selectTopOne() ;

    int countByParentId(Long parentId) ;

    List<StandardBean> selectAll() ;

    int updateImgFlag(StandardBean standardBean) ;

    int updateNameKey(StandardBean standardBean) ;
}
