package com.example.demo;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
@RestController
@RequestMapping(path ="/student")
public class student {
    private Integer id = 10;
    private String firstName ="Mark";
    private String lastName = "Endeshaw";
    private String email ="ssh@gmail.com";
    public student() {

    }

    public student(Integer id, String firstName, String lastName, String email) {
        super();
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    //getters and setters

    @GetMapping
    @Override
    public String toString() {
        return "student [id=" + id + ", firstName=" + firstName
                + ", lastName=" + lastName + ", email=" + email + "]";
    }
    @RequestMapping(value="/student",method =RequestMethod.POST)
    public List<student> getStudents()
    {
        List<student> studentsList = new ArrayList<student>();
        studentsList.add(new student(1," "," ",""));
        return studentsList;
    }
}

