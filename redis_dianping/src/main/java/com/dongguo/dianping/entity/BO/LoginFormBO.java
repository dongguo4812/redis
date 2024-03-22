package com.dongguo.dianping.entity.BO;

import lombok.Data;

@Data
public class LoginFormBO {
    private String phone;
    private String code;
    private String password;
}
