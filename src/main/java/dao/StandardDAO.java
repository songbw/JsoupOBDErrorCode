package dao;

import bean.StandardBean;

/**
 * Created by sbw22 on 2017/9/18.
 */
public interface StandardDAO {
    Integer addStandard(StandardBean standardBean) ;

    StandardBean selectTopOne() ;

    int countByParentId(Long parentId) ;
}
