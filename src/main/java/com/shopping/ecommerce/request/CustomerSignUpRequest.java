package com.shopping.ecommerce.request;

import com.shopping.ecommerce.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerSignUpRequest {
    private String name;

    private String email;

    private String password;

    private String phone;

    private List<Address> addresses;

    public CustomerSignUpRequest(String name, String email, String password, String phone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.addresses = new ArrayList<>();
    }

}
