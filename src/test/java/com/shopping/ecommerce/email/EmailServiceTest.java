package com.shopping.ecommerce.email;

import com.shopping.ecommerce.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTest {
    @Autowired
    private EmailService emailService;

    @Test
    public void testSendEmail() {
        emailService.sendEmail("shardhanand286@email.com", "test", "Hello ji ?");
    }
}
