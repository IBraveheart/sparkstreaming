package bean;

import java.io.Serializable;

/**
 * @author Akang
 * @create 2022-11-24 22;27
 */
public class PageActionLog implements Serializable {
    private static final long serialVersionUID = 1L;

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
    private String last_page_id;
    private String page_item;
    private String page_item_type;
    private Long during_time;
    private String sourceType;
    private String action_id;
    private String action_item;
    private String action_item_type;
    private Long action_ts;
    private Long ts;

    public PageActionLog() {
    }

    public PageActionLog(String mid, String user_id, String province_id, String channel, String is_new, String model, String operate_system, String version_code, String brand, String page_id, String last_page_id, String page_item, String page_item_type, Long during_time, String sourceType, String action_id, String action_item, String action_item_type, Long action_ts, Long ts) {
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
        this.last_page_id = last_page_id;
        this.page_item = page_item;
        this.page_item_type = page_item_type;
        this.during_time = during_time;
        this.sourceType = sourceType;
        this.action_id = action_id;
        this.action_item = action_item;
        this.action_item_type = action_item_type;
        this.action_ts = action_ts;
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

    public void setLast_page_id(String last_page_id) {
        this.last_page_id = last_page_id;
    }

    public void setPage_item(String page_item) {
        this.page_item = page_item;
    }

    public void setPage_item_type(String page_item_type) {
        this.page_item_type = page_item_type;
    }

    public void setDuring_time(Long during_time) {
        this.during_time = during_time;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public void setAction_id(String action_id) {
        this.action_id = action_id;
    }

    public void setAction_item(String action_item) {
        this.action_item = action_item;
    }

    public void setAction_item_type(String action_item_type) {
        this.action_item_type = action_item_type;
    }

    public void setAction_ts(Long action_ts) {
        this.action_ts = action_ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
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

    public String getLast_page_id() {
        return last_page_id;
    }

    public String getPage_item() {
        return page_item;
    }

    public String getPage_item_type() {
        return page_item_type;
    }

    public Long getDuring_time() {
        return during_time;
    }

    public String getSourceType() {
        return sourceType;
    }

    public String getAction_id() {
        return action_id;
    }

    public String getAction_item() {
        return action_item;
    }

    public String getAction_item_type() {
        return action_item_type;
    }

    public Long getAction_ts() {
        return action_ts;
    }

    public Long getTs() {
        return ts;
    }

    @Override
    public String toString() {
        return "PageActionLog{" +
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
                ", last_page_id='" + last_page_id + '\'' +
                ", page_item='" + page_item + '\'' +
                ", page_item_type='" + page_item_type + '\'' +
                ", during_time=" + during_time +
                ", sourceType='" + sourceType + '\'' +
                ", action_id='" + action_id + '\'' +
                ", action_item='" + action_item + '\'' +
                ", action_item_type='" + action_item_type + '\'' +
                ", action_ts=" + action_ts +
                ", ts=" + ts +
                '}';
    }
}
