package main.controller;

import main.model.User;
import main.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DialogsController {
    @Value("${avatar.path}")
    private String avatarPath;

    @Autowired
    public UserRepo userRepo;



    @GetMapping("/profile/{user}/dialogs")
    public String dialogs(@AuthenticationPrincipal User user, @RequestParam(required = false) String filter, Model model){
        if(filter != null && !filter.isEmpty()) {
            User users = userRepo.findByUsername(filter);
            model.addAttribute("users", users);
        }
        else {Iterable<User> users = userRepo.findAll();
            model.addAttribute("users", users);}
        model.addAttribute("userChannel", user);
        model.addAttribute("username", user.getUsername());
        return "dialogs";
    }


}
