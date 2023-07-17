package com.simon.stunningfiesta.security;

import com.simon.stunningfiesta.hogwartsuser.MyUserPrincipal;
import com.simon.stunningfiesta.hogwartsuser.UserDto;
import com.simon.stunningfiesta.hogwartsuser.converters.UserToUserDtoConverter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    private final JwtProvider jwtProvider;

    private final UserToUserDtoConverter userToUserDtoConverter;

    public AuthService(JwtProvider jwtProvider, UserToUserDtoConverter userToUserDtoConverter) {
        this.jwtProvider = jwtProvider;
        this.userToUserDtoConverter = userToUserDtoConverter;
    }

    public Map<String, Object> createLoginInfo(Authentication authentication) {
        // create user info.
        MyUserPrincipal principal = (MyUserPrincipal) authentication.getPrincipal();
        UserDto userDto = userToUserDtoConverter.convert(principal.getHogwartsUser());
        // crate a JWT
        String token = this.jwtProvider.createToken(authentication);
        return Map.of("userInfo", userDto, "token", token);
    }
}
