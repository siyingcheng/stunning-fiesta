package com.simon.stunningfiesta.hogwartsuser.converters;

import com.simon.stunningfiesta.hogwartsuser.HogwartsUser;
import com.simon.stunningfiesta.hogwartsuser.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToUserDtoConverter implements Converter<HogwartsUser, UserDto> {
    @Override
    public UserDto convert(HogwartsUser source) {
        return new UserDto(source.getId(), source.getUsername(), source.isEnabled(), source.getRoles());
    }
}
