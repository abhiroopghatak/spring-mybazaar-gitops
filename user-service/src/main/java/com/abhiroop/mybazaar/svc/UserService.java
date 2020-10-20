package com.abhiroop.mybazaar.svc;

import java.util.List;

import com.abhiroop.mybazaar.dto.UserDto;

public interface UserService {
    List<UserDto> findAll();
    UserDto findOne(long id);
    
    UserDto findOneByUsername(String username);
    UserDto findOneByEmail(String email);
    void delete(long id);
    
    //sign up functions
    
    UserDto signUpUser(UserDto user);
	UserDto save(UserDto userDto, boolean isUpdate) throws RuntimeException;
	UserDto confirmUser(String oneTimeToken) throws Exception;
}
