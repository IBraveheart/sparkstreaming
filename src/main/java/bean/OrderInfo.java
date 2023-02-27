package bean;

import java.io.Serializable;

/**
 * @author Akang
 * @create 2022-12-15 16:03
 */
public class OrderInfo implements Serializable{
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long province_id;
    private String order_status;
    private Long user_id;
    private Double total_amount;
    private Double activity_reduce_amount;
    private Double coupon_reduce_amount;
    private Double original_total_amount;
    private Double feight_fee;
    private Double feight_fee_reduce;
    private String expire_time;
    private String refundable_time;
    private String create_time;
    private String operate_time;

    // 把其他字段处理得到
    private String create_date;
    private String create_hour;

    //查询维度表得到
    private String province_name;
    private String province_area_code;
    private String province_3166_2_code;
    private String province_iso_code;
    //查询维度表得到
    private int user_age;
    private String user_gender;

    public OrderInfo() {
    }

    public OrderInfo(Long id, Long province_id, String order_status, Long user_id, Double total_amount, Double activity_reduce_amount, Double coupon_reduce_amount, Double original_total_amount, Double feight_fee, Double feight_fee_reduce, String expire_time, String refundable_time, String create_time, String operate_time, String create_date, String create_hour, String province_name, String province_area_code, String province_3166_2_code, String province_iso_code, int user_age, String user_gender) {
        this.id = id;
        this.province_id = province_id;
        this.order_status = order_status;
        this.user_id = user_id;
        this.total_amount = total_amount;
        this.activity_reduce_amount = activity_reduce_amount;
        this.coupon_reduce_amount = coupon_reduce_amount;
        this.original_total_amount = original_total_amount;
        this.feight_fee = feight_fee;
        this.feight_fee_reduce = feight_fee_reduce;
        this.expire_time = expire_time;
        this.refundable_time = refundable_time;
        this.create_time = create_time;
        this.operate_time = operate_time;
        this.create_date = create_date;
        this.create_hour = create_hour;
        this.province_name = province_name;
        this.province_area_code = province_area_code;
        this.province_3166_2_code = province_3166_2_code;
        this.province_iso_code = province_iso_code;
        this.user_age = user_age;
        this.user_gender = user_gender;
    }

    public Long getId() {
        return id;
    }

    public Long getProvince_id() {
        return province_id;
    }

    public String getOrder_status() {
        return order_status;
    }

    public Long getUser_id() {
        return user_id;
    }

    public Double getTotal_amount() {
        return total_amount;
    }

    public Double getActivity_reduce_amount() {
        return activity_reduce_amount;
    }

    public Double getCoupon_reduce_amount() {
        return coupon_reduce_amount;
    }

    public Double getOriginal_total_amount() {
        return original_total_amount;
    }

    public Double getFeight_fee() {
        return feight_fee;
    }

    public Double getFeight_fee_reduce() {
        return feight_fee_reduce;
    }

    public String getExpire_time() {
        return expire_time;
    }

    public String getRefundable_time() {
        return refundable_time;
    }

    public String getCreate_time() {
        return create_time;
    }

    public String getOperate_time() {
        return operate_time;
    }

    public String getCreate_date() {
        return create_date;
    }

    public String getCreate_hour() {
        return create_hour;
    }

    public String getProvince_name() {
        return province_name;
    }

    public String getProvince_area_code() {
        return province_area_code;
    }

    public String getProvince_3166_2_code() {
        return province_3166_2_code;
    }

    public String getProvince_iso_code() {
        return province_iso_code;
    }

    public int getUser_age() {
        return user_age;
    }

    public String getUser_gender() {
        return user_gender;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProvince_id(Long province_id) {
        this.province_id = province_id;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public void setTotal_amount(Double total_amount) {
        this.total_amount = total_amount;
    }

    public void setActivity_reduce_amount(Double activity_reduce_amount) {
        this.activity_reduce_amount = activity_reduce_amount;
    }

    public void setCoupon_reduce_amount(Double coupon_reduce_amount) {
        this.coupon_reduce_amount = coupon_reduce_amount;
    }

    public void setOriginal_total_amount(Double original_total_amount) {
        this.original_total_amount = original_total_amount;
    }

    public void setFeight_fee(Double feight_fee) {
        this.feight_fee = feight_fee;
    }

    public void setFeight_fee_reduce(Double feight_fee_reduce) {
        this.feight_fee_reduce = feight_fee_reduce;
    }

    public void setExpire_time(String expire_time) {
        this.expire_time = expire_time;
    }

    public void setRefundable_time(String refundable_time) {
        this.refundable_time = refundable_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public void setOperate_time(String operate_time) {
        this.operate_time = operate_time;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public void setCreate_hour(String create_hour) {
        this.create_hour = create_hour;
    }

    public void setProvince_name(String province_name) {
        this.province_name = province_name;
    }

    public void setProvince_area_code(String province_area_code) {
        this.province_area_code = province_area_code;
    }

    public void setProvince_3166_2_code(String province_3166_2_code) {
        this.province_3166_2_code = province_3166_2_code;
    }

    public void setProvince_iso_code(String province_iso_code) {
        this.province_iso_code = province_iso_code;
    }

    public void setUser_age(int user_age) {
        this.user_age = user_age;
    }

    public void setUser_gender(String user_gender) {
        this.user_gender = user_gender;
    }

    @Override
    public String toString() {
        return "OrderInfo{" +
                "id=" + id +
                ", province_id=" + province_id +
                ", order_status='" + order_status + '\'' +
                ", user_id=" + user_id +
                ", total_amount=" + total_amount +
                ", activity_reduce_amount=" + activity_reduce_amount +
                ", coupon_reduce_amount=" + coupon_reduce_amount +
                ", original_total_amount=" + original_total_amount +
                ", feight_fee=" + feight_fee +
                ", feight_fee_reduce=" + feight_fee_reduce +
                ", expire_time='" + expire_time + '\'' +
                ", refundable_time='" + refundable_time + '\'' +
                ", create_time='" + create_time + '\'' +
                ", operate_time='" + operate_time + '\'' +
                ", create_date='" + create_date + '\'' +
                ", create_hour='" + create_hour + '\'' +
                ", province_name='" + province_name + '\'' +
                ", province_area_code='" + province_area_code + '\'' +
                ", province_3166_2_code='" + province_3166_2_code + '\'' +
                ", province_iso_code='" + province_iso_code + '\'' +
                ", user_age=" + user_age +
                ", user_gender='" + user_gender + '\'' +
                '}';
    }
}
