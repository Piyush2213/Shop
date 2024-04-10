package com.shopping.Ecommerce.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerSignUpRequest {
    private String name;

    private String email;

    private String password;

    private String phone;

    private String address;

}
