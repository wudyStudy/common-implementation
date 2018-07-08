package com.example.commonimplementation.config;

import com.jayway.jsonpath.JsonPath;
import org.springframework.kafka.annotation.KafkaListener;

/**
 * woody
 */

public class KafkaConsumerConfig {

    @KafkaListener(topics = {"xxxxxxx"})
    public void processMessage(String content) {


        String tenantId = JsonPath.read(content, "$.tenantId");

        return;

    }
}