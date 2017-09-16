package bean;

import com.mongodb.ReflectionDBObject;
import org.mongodb.morphia.annotations.Entity;

import java.io.Serializable;

/**
 * Created by song on 16/4/20.
 */
@Entity(value = "Standard", noClassnameStored = true)
public class StandardBean extends ReflectionDBObject implements Serializable {
    private long id;
    // 标准件名称
    private String name ;
    // 父ID
    private long parentId ;
    private String kinship ;
    // 类型（国标，欧标等）
    private String type ;
    //English name
    private String engName ;

    private String code ;
    //小图片网页路径
    private String smallImg ;
    //小图片路径
    private String smallImgPath;
    // 小图片描述
    private String sImgAlt ;
    //大图片网页路径
    private String img ;
    //大图片路径
    private String imgPath;
    // 有效标准
    private String similar ;
    // 大图片描述
    private String imgTitle ;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEngName() {
        return engName;
    }

    public void setEngName(String engName) {
        this.engName = engName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSmallImg() {
        return smallImg;
    }

    public void setSmallImg(String smallImg) {
        this.smallImg = smallImg;
    }

    public String getsImgAlt() {
        return sImgAlt;
    }

    public void setsImgAlt(String sImgAlt) {
        this.sImgAlt = sImgAlt;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getSimilar() {
        return similar;
    }

    public void setSimilar(String similar) {
        this.similar = similar;
    }

    public String getImgTitle() {
        return imgTitle;
    }

    public void setImgTitle(String imgTitle) {
        this.imgTitle = imgTitle;
    }

    public String getSmallImgPath() {
        return smallImgPath;
    }

    public void setSmallImgPath(String smallImgPath) {
        this.smallImgPath = smallImgPath;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
