package com.shopping.ecommerce.response;

import com.razorpay.PaymentLink;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLinkResponse {
    private String payment_link_url;
    private String Payment_link_id;
    private String reference_id;
}
