package main.controller;

import main.model.User;
import main.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminController {
    @Autowired
    private UserRepo userRepo;

    @GetMapping("/profile/{user}/admin")
    public String panel(@AuthenticationPrincipal User user, Model model) {
        Iterable<User> users = userRepo.findAll();
        model.addAttribute("users", users);
        model.addAttribute("userChannel", user);
        return "admin";
    }
}
