package org.example.auto_marketing_website.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacebookPageDto {
    private String pageId;
    private String accessToken;
    private String name;
}
