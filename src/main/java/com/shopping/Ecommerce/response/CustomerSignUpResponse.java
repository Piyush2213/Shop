package com.shopping.Ecommerce.response;

import com.shopping.Ecommerce.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerSignUpResponse {

    private String name;
    private String email;
    private String phone;
    private List<Address> addresses;
    private boolean verified;
    private String message;
}
