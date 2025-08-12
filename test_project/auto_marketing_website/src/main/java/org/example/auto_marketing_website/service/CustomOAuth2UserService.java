package org.example.auto_marketing_website.service;

import org.example.auto_marketing_website.entity.User;
import org.example.auto_marketing_website.repository.IUserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final IUserRepository userRepository;

    public CustomOAuth2UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        Map<String, Object> attributes = oauth2User.getAttributes();

        // Lấy email, id, name
        String email = (String) attributes.get("email");
        String facebookId = (String) attributes.get("id");
        String name = (String) attributes.get("name");

        // Tìm user trong DB theo email
        User user = userRepository.findByUsername(email).orElse(null);
        if (user == null) {
            // Tạo mới nếu chưa có
            user = new User();
            user.setUsername(email);
            user.setFacebookId(facebookId);
            user.setName(name);
            // Thiết lập các trường cần thiết khác (password, roles nếu có)
            user.setEmail(email);

            userRepository.save(user);
        } else {
            // Cập nhật thông tin user nếu muốn
            user.setFacebookId(facebookId);
            user.setName(name);
            userRepository.save(user);
        }

        return oauth2User;
    }
}

