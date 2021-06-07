package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Random;

@RestController
@RequestMapping("/home")
public class home{

    String codes[] = {"#B3EFFF","#932112","#016178",
            "#373f52","#E2EFF2", "#ff0000","#b58ca3",
            "#000000","#ff7f24", "#c8b9fe","#f2eee0",
            "#871812","#065535","#976fac","#716a5a",
            "#ff4982","#420420","#9eeac1","#dfa61f",
            "#DAF7A6","#98F019","#3CCEDA","#6700CD",
            "#FF4982","#8F6C7D","#FFF68F"};

    public String getHome() throws IOException {
        return "waste man";
    }

    @GetMapping
    public String getRandom() {
        final int[] ints = new Random().ints(0, 25).distinct().limit(6).toArray();
        String res = "";
        for (int i = 0; i < 5; i++) {
            res += codes[ints[i]] + ",";
        }
        res += codes[ints[5]];
        //Gson gson = new Gson();
        //String result = gson.toJson(res);
        return res;
    }

    @PostMapping
    public String postHome(HttpServletRequest request) throws IOException {
        System.out.println("Post"+request.getParameter("name") + request.getMethod()+request.getHeader("body"));
        return "Hello World!"+ request.getRequestURI();
    }
}
