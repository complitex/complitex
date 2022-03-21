package ru.complitex.user.service;

import ru.complitex.user.mapper.UserMapper;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.List;

/**
 * @author Ivanov Anatoliy
 */
@RequestScoped
public class UserService {
    @Inject
    private UserMapper userMapper;

    public Boolean authenticate(String username, String password) {
        return userMapper.authenticate(username, password);
    }

    public List<String> roles(String username) {
        return userMapper.roles(username);
    }
}
