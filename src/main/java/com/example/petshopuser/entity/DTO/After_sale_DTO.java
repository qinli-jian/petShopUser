package com.example.petshopuser.entity.DTO;

import lombok.Data;

import java.util.List;

@Data
public class After_sale_DTO {
    private String user_id;
    private String order_id;
    private String after_sale_content;
    private String service_type;
    private List<String> imgs;
}
