package com.example.petshopuser.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class After_sale {
    private String after_sale_id;
    private String user_id;
    private String order_id;
    private String after_sale_content;
    private String service_type;
    private String imgs;
    private BigDecimal refund_price;
    private String refund_reason;
    public After_sale(){

    }
    public After_sale(String after_sale_id,String user_id,String order_id,
                      String after_sale_content,String service_type,String imgs,BigDecimal refund_price,String refund_reason){
        this.after_sale_id = after_sale_id;
        this.user_id = user_id;
        this.order_id = order_id;
        this.after_sale_content =after_sale_content;
        this.service_type = service_type;
        this.imgs = imgs;
        this.refund_price = refund_price;
        this.refund_reason =refund_reason;
    }
}
