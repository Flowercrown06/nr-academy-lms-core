package com.nracademy.backend.controller;

import com.nracademy.backend.entity.user.User;
import com.nracademy.backend.repository.UserRepository;
import com.nracademy.backend.tenant.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {

    @Autowired
    CurrentUserService urepo;

    @GetMapping
    public User test(){
        return urepo.requireCurrentUser() ;
    }



}