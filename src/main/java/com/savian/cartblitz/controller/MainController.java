package com.savian.cartblitz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {
    @RequestMapping({"","/","/home"})
    public ModelAndView getHome(){
        return new ModelAndView("main");
    }

    @GetMapping("/login")
    public String showLogInForm(){
        return "login";
    }

    @GetMapping("/access_denied")
    public String accessDeniedPage(){
        return "accessDenied";
    }
}
