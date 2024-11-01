package com.shopping.ecommerce.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReferenceIdGenerator {
    private static long counter = 0;

    public static String generateReferenceId() {
        String uniqueId = UUID.randomUUID().toString().replace("-", "");

        long currentTimeMillis = System.currentTimeMillis();

        String referenceId = String.format("%d%s%d", currentTimeMillis, uniqueId, counter++);

        return referenceId.length() > 40 ? referenceId.substring(0, 40) : referenceId;
    }
}
