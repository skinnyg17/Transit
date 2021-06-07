package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/driverPage")
public class DriverController {

    @RequestMapping(value = "/jobSearch", method = RequestMethod.POST)
    public String enableDriverJobSearch(HttpServletRequest request) throws IOException {
        String username = request.getParameter("name");
        return "Job Search is now enabled!";
    }
}
