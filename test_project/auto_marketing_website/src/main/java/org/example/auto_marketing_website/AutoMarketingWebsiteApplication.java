package org.example.auto_marketing_website;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AutoMarketingWebsiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoMarketingWebsiteApplication.class, args);
    }

}
