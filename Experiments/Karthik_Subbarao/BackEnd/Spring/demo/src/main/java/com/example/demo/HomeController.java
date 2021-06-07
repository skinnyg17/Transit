package com.example.demo;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/home")
public class HomeController {

    private Map<String, String> userInfo;
    private Map<String, String> driverInfo;


    @RequestMapping("/normal")
    public String home(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();

        String ip = request.getRemoteAddr();
        int port = request.getLocalPort();
        int port2 = request.getServerPort();
        return "Hello World! "+ip+":"+port+":"+port2;
    }


    @GetMapping
    public ResponseEntity<String> getHome() throws IOException {
        System.out.println("user logged");
        return new ResponseEntity<>(
                "1dayumay",
                HttpStatus.OK);
    }

    @RequestMapping(value = "/Image", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImage() throws IOException {
//        File fnew=new File("C:\\Users\\Karth\\Desktop\\gnomechild.png");
//        BufferedImage originalImage= ImageIO.read(fnew);
//        ByteArrayOutputStream baos=new ByteArrayOutputStream();
//        ImageIO.write(originalImage, "PNG", baos );
//        byte[] imageInByte=baos.toByteArray();


        File f = new File("C:\\Users\\Karth\\Desktop\\gnomechild.png");		//change path of image according to you
        FileInputStream fis = new FileInputStream(f);
        byte byteArray[] = new byte[(int)f.length()];
        fis.read(byteArray);
        String imageString = Base64.encodeBase64String(byteArray);

        //decode Base64 String to image
//        FileOutputStream fos = new FileOutputStream("H:/decode/destinationImage.png"); //change path of image according to you
//        byteArray = Base64.decodeBase64(imageString);
//        fos.write(byteArray);
//
//        fis.close();
//        fos.close();

        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(byteArray);
    }

    @PostMapping
    public String postHome(HttpServletRequest request) throws IOException {
//        System.out.println("Post"+request.getParameter("name") + request.getMethod()+request.getHeader("body"));
        String username = request.getParameter("name");
        String password = request.getParameter("password");

        userInfo.put(username,password);
        System.out.println(username + " : " + password);
        return "Hello World! ";
    }


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String checkLogin(HttpServletRequest request) throws IOException {
        tempHelper();
        String username = request.getParameter("name");
        String password = request.getParameter("password");
        if(driverCheck(username,password)){
            return "driver";
        }
        if(userInfo.containsKey(username)){
            String checkPassword = userInfo.get(username);
            System.out.println("______________________________");
            System.out.println("username"+username);
            System.out.println("db password: "+checkPassword + " | " + "userpassword: " + password);
            if(checkPassword != null){
                if(checkPassword.equals(password)){
                    return "user";
                }
            }
        }
//        for (String key: userInfo.keySet()) {
//            System.out.println("username : " + key);
//            System.out.println("password : " + userInfo.get(key));
//        }
        return "badRequest";
    }

    public void tempHelper(){
        if(userInfo == null){
            userInfo = new HashMap<String, String>();
            userInfo.put("lol","lol");
        }
        if(driverInfo == null){
            driverInfo = new HashMap<String, String>();
            driverInfo.put("driver","d");
        }
    }

    public boolean driverCheck(String username, String password){
        if(driverInfo.containsKey(username)){
            String checkPassword = driverInfo.get(username);
            if(checkPassword != null){
                if(checkPassword.equals(password)){
                    return true;
                }
            }
        }
        return false;
    }

    @RequestMapping(value = "/driver/jobSearch", method = RequestMethod.POST)
    public String enableDriverJobSearch(HttpServletRequest request) throws IOException {
        String username = request.getParameter("name");
        return "Job Search is now enabled!";
    }
}