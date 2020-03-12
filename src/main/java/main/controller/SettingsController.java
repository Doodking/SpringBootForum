package main.controller;

import main.model.Post;
import main.model.User;
import main.repo.UserRepo;
import main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
public class SettingsController {
    @Value("${avatar.path}")
    private String avatarPath;

    @Autowired
    public UserRepo userRepo;

    @Autowired
    public UserService userService;

    @PostMapping("/profile/{user}/settings")
    public String updateProfile(@AuthenticationPrincipal User user,
                                @RequestParam(value = "file", required = false) MultipartFile file,
                                @RequestParam(value = "username",required = false) String username,
                                @RequestParam(value = "password", required = false) String password,
                                Model model) throws IOException {
        user.setUsername(username);
        user.setPassword(password);
        saveFile(file, user);
        userRepo.save(user);
        model.addAttribute("id", user.getId());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("userChannel", user);
        model.addAttribute("avatar", user.getImage());
        return "settings";

    }

    @GetMapping("/profile/{user}/settings")
    public String settingsOfUser(@AuthenticationPrincipal User user, Model model){
        model.addAttribute("userChannel", user);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("id", user.getId());
        model.addAttribute("image", user.getImage());
        return "settings";
    }

    void saveFile(@RequestParam("file") MultipartFile file, User user) throws IOException {
        if(file != null){
            File uploadDir = new File(avatarPath);
            File tempDir = new File("/tmp" + avatarPath);
            if(!uploadDir.exists() || !tempDir.exists()){
                tempDir.mkdirs();
                uploadDir.mkdirs();
            }

            String uuidFile = UUID.randomUUID().toString();
            String currentPath = uploadDir.getPath();


            String resultFilename = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(currentPath + "/" + resultFilename));

            user.setImage(resultFilename);
        }
    }
}
