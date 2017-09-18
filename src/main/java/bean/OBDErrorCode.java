package bean;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.io.Serializable;

//@Entity(value = "OBDErrorCode2", noClassnameStored = true)
public class OBDErrorCode implements Serializable {
	private static final long serialVersionUID = 8234066333260774300L;

	@Id
	private long id;
	private String code;
	private String carMaker;
	private String scope;
	private String desc;
	private String descChinese;
	private String about;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCarMaker() {
		return carMaker;
	}
	public void setCarMaker(String carMaker) {
		this.carMaker = carMaker;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getDescChinese() {
		return descChinese;
	}
	public void setDescChinese(String descChinese) {
		this.descChinese = descChinese;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
}
