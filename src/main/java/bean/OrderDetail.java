package bean;

import java.io.Serializable;

/**
 * @author Akang
 * @create 2022-12-15 16:49
 */
public class OrderDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long order_id;
    private Long sku_id;
    private Double order_price;
    private Long sku_num;
    private String sku_name;
    private String create_time;
    private Double split_total_amount;
    private Double split_activity_amount;
    private Double split_coupon_amount;

    public OrderDetail() {
    }

    public OrderDetail(Long id, Long order_id, Long sku_id, Double order_price, Long sku_num, String sku_name, String create_time, Double split_total_amount, Double split_activity_amount, Double split_coupon_amount) {
        this.id = id;
        this.order_id = order_id;
        this.sku_id = sku_id;
        this.order_price = order_price;
        this.sku_num = sku_num;
        this.sku_name = sku_name;
        this.create_time = create_time;
        this.split_total_amount = split_total_amount;
        this.split_activity_amount = split_activity_amount;
        this.split_coupon_amount = split_coupon_amount;
    }

    public Long getId() {
        return id;
    }

    public Long getOrder_id() {
        return order_id;
    }

    public Long getSku_id() {
        return sku_id;
    }

    public Double getOrder_price() {
        return order_price;
    }

    public Long getSku_num() {
        return sku_num;
    }

    public String getSku_name() {
        return sku_name;
    }

    public String getCreate_time() {
        return create_time;
    }

    public Double getSplit_total_amount() {
        return split_total_amount;
    }

    public Double getSplit_activity_amount() {
        return split_activity_amount;
    }

    public Double getSplit_coupon_amount() {
        return split_coupon_amount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrder_id(Long order_id) {
        this.order_id = order_id;
    }

    public void setSku_id(Long sku_id) {
        this.sku_id = sku_id;
    }

    public void setOrder_price(Double order_price) {
        this.order_price = order_price;
    }

    public void setSku_num(Long sku_num) {
        this.sku_num = sku_num;
    }

    public void setSku_name(String sku_name) {
        this.sku_name = sku_name;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public void setSplit_total_amount(Double split_total_amount) {
        this.split_total_amount = split_total_amount;
    }

    public void setSplit_activity_amount(Double split_activity_amount) {
        this.split_activity_amount = split_activity_amount;
    }

    public void setSplit_coupon_amount(Double split_coupon_amount) {
        this.split_coupon_amount = split_coupon_amount;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "id=" + id +
                ", order_id=" + order_id +
                ", sku_id=" + sku_id +
                ", order_price=" + order_price +
                ", sku_num=" + sku_num +
                ", sku_name='" + sku_name + '\'' +
                ", create_time='" + create_time + '\'' +
                ", split_total_amount=" + split_total_amount +
                ", split_activity_amount=" + split_activity_amount +
                ", split_coupon_amount=" + split_coupon_amount +
                '}';
    }
}
