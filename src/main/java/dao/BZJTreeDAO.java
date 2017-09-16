package dao;

import bean.BZJTreeBean;

import java.util.List;

/**
 * Created by song on 2017/9/16.
 */
public interface BZJTreeDAO {
    List<BZJTreeBean> selectByUrl();
}
