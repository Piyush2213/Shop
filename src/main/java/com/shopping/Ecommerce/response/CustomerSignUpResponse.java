package com.shopping.Ecommerce.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerSignUpResponse {

    private String name;
    private String email;
    private String phone;
    private String address;
}
