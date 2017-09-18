package bean;

import com.mongodb.ReflectionDBObject;

import java.io.Serializable;

/**
 * Created by song on 16/4/19.
 */
//@Entity(value = "BZJTree", noClassnameStored = true)
public class BZJTreeBean extends ReflectionDBObject implements Serializable{

    private long id;
    private String name;
    private int count ;
    private String url ;
    private long parentId ;
    private String kinship;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getKinship() {
        return kinship;
    }

    public void setKinship(String kinship) {
        this.kinship = kinship;
    }
}
