package com.simon.stunningfiesta.hogwartsuser;

import com.simon.stunningfiesta.hogwartsuser.converters.UserDtoToUserConverter;
import com.simon.stunningfiesta.hogwartsuser.converters.UserToUserDtoConverter;
import com.simon.stunningfiesta.system.Result;
import com.simon.stunningfiesta.system.StatusCode;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.base-url}/users")
public class UserController {

    private final UserService userService;

    private final UserToUserDtoConverter userToUserDtoConverter;

    private final UserDtoToUserConverter userDtoToUserConverter;

    public UserController(UserService userService,
                          UserToUserDtoConverter userToUserDtoConverter,
                          UserDtoToUserConverter userDtoToUserConverter) {
        this.userService = userService;
        this.userToUserDtoConverter = userToUserDtoConverter;
        this.userDtoToUserConverter = userDtoToUserConverter;
    }

    @GetMapping
    public Result findAllUsers() {
        List<UserDto> userDtoList = userService.findAll()
                .stream()
                .map(userToUserDtoConverter::convert)
                .collect(Collectors.toList());
        return Result.success("Find All Success")
                .withData(userDtoList);
    }

    @GetMapping("/{userId}")
    public Result findUser(@PathVariable Integer userId) {
        return Result.success("Find User Success")
                .withData(userToUserDtoConverter.convert(userService.findById(userId)));
    }

    @PostMapping
    public Result addUser(@Valid @RequestBody HogwartsUser hogwartsUser) {
        return Result.success("Add User Success")
                .withData(userToUserDtoConverter.convert(userService.save(hogwartsUser)));
    }

    @PutMapping("/{userId}")
    public Result updateUser(@PathVariable Integer userId, @Valid @RequestBody HogwartsUser hogwartsUser) {
        return Result.success("Update User Success")
                .withData(userToUserDtoConverter.convert(userService.update(userId, hogwartsUser)));
    }

    @DeleteMapping("/{userId}")
    public Result deleteUser(@PathVariable Integer userId) {
        userService.deleteById(userId);
        return Result.success("Delete User Success");
    }
}
