package main.controller;

import main.dto.CaptchaResponseDto;
import main.model.User;
import main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Controller
public class RegController {

    private static final String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response";

    @Autowired
    public UserService userService;

    @Value("${recaptcha.secret}")
    private String secret;

    @Autowired
    private RestTemplate template;
    @GetMapping("/registration")
    public String registration(){
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Model model, @RequestParam("g-recaptcha-response") String captcha){
        String url = String.format(CAPTCHA_URL, secret, captcha);
        CaptchaResponseDto responseDto = template.postForObject(url, Collections.emptyList(), CaptchaResponseDto.class);
        if(!responseDto.isSucces()){
            model.addAttribute("captcha", "Fill captcha");
        }
        if(!userService.addUser(user)){
            model.addAttribute("message", "User exists!");
            return "registration";
        }
        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code){
        boolean isActivate = userService.activateUser(code);
        if(isActivate){
            model.addAttribute("message", "User succesfully activated!!");
        }else{
            model.addAttribute("message", "Code is not found");
        }

        return "login";

    }
}
