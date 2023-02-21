package com.example.gatewayservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {

    public UserDTO() {
        // 기본 생성자
    }

    public UserDTO(String user_rep_id, String role) {
        this.user_rep_id = user_rep_id;
        this.role = role;
    }
    String user_rep_id;
    String role;


}
