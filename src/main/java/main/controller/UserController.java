package main.controller;

import com.mysql.cj.util.StringUtils;
import main.model.Post;
import main.model.User;
import main.repo.PostRepo;
import main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Set;

@Controller
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    PostRepo postRepo;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/profile/{user}")
    public String userPosts(@AuthenticationPrincipal User currentUser, @PathVariable User user, @RequestParam(required = false) Post post, Model model){
        User userDob = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDob.getUsername();
        Integer id = userDob.getId();
        model.addAttribute("username", username);
        model.addAttribute("id", id);
        Set<Post> posts = user.getPosts();
        for(Post postI : posts) {
            Set<User> likes = postI.getLikes();
            if(likes.contains(user)) {
                model.addAttribute("isLikedMe", likes.contains(user));
            }
        }
        model.addAttribute("subscriptionsCount", user.getSubscriptions().size());
        model.addAttribute("subscribersCount", user.getSubscribers().size());
        model.addAttribute("userChannel", user);
        model.addAttribute("posts", posts);
        model.addAttribute("post", post);
        model.addAttribute("isCurrentUser", currentUser.equals(user));
        model.addAttribute("isSubscriber", user.getSubscribers().contains(currentUser));
        return "profile";
    }

    @PostMapping("/profile/{user}")
    public String editPost(@AuthenticationPrincipal User currentUser,
                           @PathVariable User user,
                           @RequestParam("id") Post post,
                           @RequestParam("text") String text,
                           @RequestParam("tag") String tag,
                           @RequestParam("file") MultipartFile file,
                           Model model) throws IOException {
        if(post.getAuthor().equals(currentUser)){
            if(!StringUtils.isEmptyOrWhitespaceOnly(text)){
                post.setText(text);
            }
            if(!StringUtils.isEmptyOrWhitespaceOnly(tag)){
                post.setTag(tag);
            }
            new PostController().saveFile(file, post);
            postRepo.save(post);
        }
        User userTrue = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer idTrue = user.getId();
        model.addAttribute("id", idTrue);
        return "redirect:/profile/" + user;
    }

    @GetMapping("subscribe/{user}")
    public String subscribe(@AuthenticationPrincipal User currentUser, @PathVariable User user){
        userService.subscribe(currentUser, user);
        return "redirect:/profile/" + user.getId();
    }

    @GetMapping("unsubscribe/{user}")
    public String unsubscribe(@AuthenticationPrincipal User currentUser, @PathVariable User user){
        userService.unsubscribe(currentUser, user);
        return "redirect:/profile/" + user.getId();
    }

    @GetMapping("{type}/{user}/list")
    public String userList(Model model, @PathVariable User user, @PathVariable String type){
        User userDob = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDob.getUsername();
        Integer id = userDob.getId();
        model.addAttribute("username", username);
        model.addAttribute("id", id);
        model.addAttribute("userChannel", user);
        model.addAttribute("type", type);
        if("subscriptions".equals(type)){
            model.addAttribute("users", user.getSubscriptions());
        }else{
            model.addAttribute("users", user.getSubscribers());
        }
        return "subscriptions";
    }
    
}
