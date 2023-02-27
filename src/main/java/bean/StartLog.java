package bean;

import java.io.Serializable;

/**
 * @author Akang
 * @create 2022-11-24 22:17
 */
public class StartLog implements Serializable {
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
    private String entry;
    private String open_ad_id;
    private Long loading_time_ms;
    private Long open_ad_ms;
    private Long open_ad_skip_ms;
    private Long ts;

    public StartLog() {
    }

    public StartLog(String mid, String user_id, String province_id, String channel, String is_new, String model, String operate_system, String version_code, String brand, String entry, String open_ad_id, Long loading_time_ms, Long open_ad_ms, Long open_ad_skip_ms, Long ts) {
        this.mid = mid;
        this.user_id = user_id;
        this.province_id = province_id;
        this.channel = channel;
        this.is_new = is_new;
        this.model = model;
        this.operate_system = operate_system;
        this.version_code = version_code;
        this.brand = brand;
        this.entry = entry;
        this.open_ad_id = open_ad_id;
        this.loading_time_ms = loading_time_ms;
        this.open_ad_ms = open_ad_ms;
        this.open_ad_skip_ms = open_ad_skip_ms;
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

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public void setOpen_ad_id(String open_ad_id) {
        this.open_ad_id = open_ad_id;
    }

    public void setLoading_time_ms(Long loading_time_ms) {
        this.loading_time_ms = loading_time_ms;
    }

    public void setOpen_ad_ms(Long open_ad_ms) {
        this.open_ad_ms = open_ad_ms;
    }

    public void setOpen_ad_skip_ms(Long open_ad_skip_ms) {
        this.open_ad_skip_ms = open_ad_skip_ms;
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

    public String getEntry() {
        return entry;
    }

    public String getOpen_ad_id() {
        return open_ad_id;
    }

    public Long getLoading_time_ms() {
        return loading_time_ms;
    }

    public Long getOpen_ad_ms() {
        return open_ad_ms;
    }

    public Long getOpen_ad_skip_ms() {
        return open_ad_skip_ms;
    }

    public Long getTs() {
        return ts;
    }

    @Override
    public String toString() {
        return "StartLog{" +
                "mid='" + mid + '\'' +
                ", user_id='" + user_id + '\'' +
                ", province_id='" + province_id + '\'' +
                ", channel='" + channel + '\'' +
                ", is_new='" + is_new + '\'' +
                ", model='" + model + '\'' +
                ", operate_system='" + operate_system + '\'' +
                ", version_code='" + version_code + '\'' +
                ", brand='" + brand + '\'' +
                ", entry='" + entry + '\'' +
                ", open_ad_id='" + open_ad_id + '\'' +
                ", loading_time_ms=" + loading_time_ms +
                ", open_ad_ms=" + open_ad_ms +
                ", open_ad_skip_ms=" + open_ad_skip_ms +
                ", ts=" + ts +
                '}';
    }
}
