package com.example.petshopuser.entity.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class After_sale_DTO {
    private String user_id;
    private String order_id;
    private String after_sale_content;
    private String service_type_id;
    private BigDecimal refund_price;
    private String refund_reason;
    private List<String> imgs;
}
