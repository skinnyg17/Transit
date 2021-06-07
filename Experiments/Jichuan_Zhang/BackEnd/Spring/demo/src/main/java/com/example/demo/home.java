package com.example.demo;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/home")
public class home{

    @GetMapping
    public String getHome() throws IOException {
        System.out.println("jkjkjkjkk");
        return "stoopad";
    }

    @PostMapping
    public String postHome(HttpServletRequest request) throws IOException {
        System.out.println(request.getParameter("input1") + request.getParameter("input2"));
        return "Hello World!"+ request.getRequestURI();
    }

    @RequestMapping(value = "/MainActivity", method = RequestMethod.POST)
    public String checkLogin(HttpServletRequest request) throws IOException {
        String input1 = request.getParameter("input1");
        String input2 = request.getParameter("input2");
        int tmp1 = Integer.valueOf(input1);
        int tmp2 = Integer.valueOf(input2);
        int result = tmp1 + tmp2;
        return  "The result is: " + String.valueOf(result);
    }

    @RequestMapping(value = "/getTwo", method = RequestMethod.GET)
    public String sample(HttpServletRequest request) throws IOException {

        return  "The result is: Biemoves ";
    }
}
