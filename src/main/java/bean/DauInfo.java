package bean;

import java.io.Serializable;

/**
 * @author Akang
 * @create 2022-11-29 1727
 */
public class DauInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    //基本的页面访问日志的数据
    private String mid;
    private String user_id;
    private String province_id;
    private String channel;
    private String is_new;
    private String model;
    private String operate_system;
    private String version_code;
    private String brand;
    private String page_id;
    private String page_item;
    private String page_item_type;
    private String sourceType;
    private Long during_timeLong;

    //用户性别 年龄
    private String user_gender;
    private String user_age;

    //地区信息
    private String province_name;
    private String province_iso_code;
    private String province_3166_2;
    private String province_area_code;

    //日期
    private String dt;
    private String hr;
    private Long ts;

    public DauInfo() {
    }

    public DauInfo(String mid, String user_id, String province_id, String channel, String is_new, String model, String operate_system, String version_code, String brand, String page_id, String page_item, String page_item_type, String sourceType, Long during_timeLong, String user_gender, String user_age, String province_name, String province_iso_code, String province_3166_2, String province_area_code, String dt, String hr, Long ts) {
        this.mid = mid;
        this.user_id = user_id;
        this.province_id = province_id;
        this.channel = channel;
        this.is_new = is_new;
        this.model = model;
        this.operate_system = operate_system;
        this.version_code = version_code;
        this.brand = brand;
        this.page_id = page_id;
        this.page_item = page_item;
        this.page_item_type = page_item_type;
        this.sourceType = sourceType;
        this.during_timeLong = during_timeLong;
        this.user_gender = user_gender;
        this.user_age = user_age;
        this.province_name = province_name;
        this.province_iso_code = province_iso_code;
        this.province_3166_2 = province_3166_2;
        this.province_area_code = province_area_code;
        this.dt = dt;
        this.hr = hr;
        this.ts = ts;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setProvince_id(String province_id) {
        this.province_id = province_id;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setIs_new(String is_new) {
        this.is_new = is_new;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setOperate_system(String operate_system) {
        this.operate_system = operate_system;
    }

    public void setVersion_code(String version_code) {
        this.version_code = version_code;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setPage_id(String page_id) {
        this.page_id = page_id;
    }

    public void setPage_item(String page_item) {
        this.page_item = page_item;
    }

    public void setPage_item_type(String page_item_type) {
        this.page_item_type = page_item_type;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public void setDuring_timeLong(Long during_timeLong) {
        this.during_timeLong = during_timeLong;
    }

    public void setUser_gender(String user_gender) {
        this.user_gender = user_gender;
    }

    public void setUser_age(String user_age) {
        this.user_age = user_age;
    }

    public void setProvince_name(String province_name) {
        this.province_name = province_name;
    }

    public void setProvince_iso_code(String province_iso_code) {
        this.province_iso_code = province_iso_code;
    }

    public void setProvince_3166_2(String province_3166_2) {
        this.province_3166_2 = province_3166_2;
    }

    public void setProvince_area_code(String province_area_code) {
        this.province_area_code = province_area_code;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public void setHr(String hr) {
        this.hr = hr;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getMid() {
        return mid;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getProvince_id() {
        return province_id;
    }

    public String getChannel() {
        return channel;
    }

    public String getIs_new() {
        return is_new;
    }

    public String getModel() {
        return model;
    }

    public String getOperate_system() {
        return operate_system;
    }

    public String getVersion_code() {
        return version_code;
    }

    public String getBrand() {
        return brand;
    }

    public String getPage_id() {
        return page_id;
    }

    public String getPage_item() {
        return page_item;
    }

    public String getPage_item_type() {
        return page_item_type;
    }

    public String getSourceType() {
        return sourceType;
    }

    public Long getDuring_timeLong() {
        return during_timeLong;
    }

    public String getUser_gender() {
        return user_gender;
    }

    public String getUser_age() {
        return user_age;
    }

    public String getProvince_name() {
        return province_name;
    }

    public String getProvince_iso_code() {
        return province_iso_code;
    }

    public String getProvince_3166_2() {
        return province_3166_2;
    }

    public String getProvince_area_code() {
        return province_area_code;
    }

    public String getDt() {
        return dt;
    }

    public String getHr() {
        return hr;
    }

    public Long getTs() {
        return ts;
    }

    @Override
    public String toString() {
        return "DauInfo{" +
                "mid='" + mid + '\'' +
                ", user_id='" + user_id + '\'' +
                ", province_id='" + province_id + '\'' +
                ", channel='" + channel + '\'' +
                ", is_new='" + is_new + '\'' +
                ", model='" + model + '\'' +
                ", operate_system='" + operate_system + '\'' +
                ", version_code='" + version_code + '\'' +
                ", brand='" + brand + '\'' +
                ", page_id='" + page_id + '\'' +
                ", page_item='" + page_item + '\'' +
                ", page_item_type='" + page_item_type + '\'' +
                ", sourceType='" + sourceType + '\'' +
                ", during_timeLong=" + during_timeLong +
                ", user_gender='" + user_gender + '\'' +
                ", user_age='" + user_age + '\'' +
                ", province_name='" + province_name + '\'' +
                ", province_iso_code='" + province_iso_code + '\'' +
                ", province_3166_2='" + province_3166_2 + '\'' +
                ", province_area_code='" + province_area_code + '\'' +
                ", dt='" + dt + '\'' +
                ", hr='" + hr + '\'' +
                ", ts=" + ts +
                '}';
    }
}
