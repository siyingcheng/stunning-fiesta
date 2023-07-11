package com.simon.stunningfiesta.hogwartsuser.converters;

import com.simon.stunningfiesta.hogwartsuser.HogwartsUser;
import com.simon.stunningfiesta.hogwartsuser.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserDtoToUserConverter implements Converter<UserDto, HogwartsUser> {
    @Override
    public HogwartsUser convert(UserDto source) {
        return new HogwartsUser()
                .setId(source.id())
                .setUsername(source.username())
                .setEnabled(source.enabled())
                .setRoles(source.roles());
    }
}
