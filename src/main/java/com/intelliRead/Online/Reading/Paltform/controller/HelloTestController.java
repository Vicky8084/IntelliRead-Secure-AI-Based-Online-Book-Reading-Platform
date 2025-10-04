package com.intelliRead.Online.Reading.Paltform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Controller
@RequestMapping("/abc")
public class HelloTestController {
    @GetMapping("/def")
    public String hello(){
        System.out.println("This is my first web Page");
        return "index";
    }
}
