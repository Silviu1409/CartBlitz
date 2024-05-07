package com.savian.cartblitz.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class MainController {
    @RequestMapping({"","/","/home"})
    public ModelAndView getHome(){
        return new ModelAndView("main");
    }
}
