package com.example.bookcatalog.controller;


import com.example.bookcatalog.model.User;
import com.example.bookcatalog.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
    BookService service;

    @GetMapping("/welcome")
    public String welcome(){
        return "Welcome to the page!!!";
    }

    @PostMapping("/registration")
    public String addUser(@RequestBody User user) {
        service.addUser(user);
        return "User is saved";
    }
}
