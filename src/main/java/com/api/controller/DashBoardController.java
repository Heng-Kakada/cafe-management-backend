package com.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashborad")
public class DashBoardController {
    @GetMapping("")
    public String dashBoard(){
        return "Hello DashBoard";
    }

    @GetMapping("/test")
    public String test(){
        return "Test";
    }

}
