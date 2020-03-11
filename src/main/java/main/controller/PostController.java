package main.controller;

import com.mysql.cj.util.StringUtils;
import main.model.Post;
import main.model.User;
import main.repo.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class PostController {
    @Autowired
    PostRepo postRepo;

    @Value("${upload.path}")
    private String uploadPath;

    @RequestMapping("/")
    public String index(Model model){
        return "home";
    }

    @GetMapping("/main")
    public String main(@RequestParam(required = false) String filter, Model model){
        Iterable<Post> posts = postRepo.findAll();
        if(filter != null && !filter.isEmpty()) {
            posts = postRepo.findByTag(filter);
        }
        else {posts = postRepo.findAll();}
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = user.getUsername();
        Integer id = user.getId();
        model.addAttribute("username", username);
        model.addAttribute("id", id);
        model.addAttribute("posts", posts);
        model.addAttribute("filter", filter);
        return "main";

    }

    @PostMapping("/main")
    public String add(@AuthenticationPrincipal User user,
                      @Valid Post post,
                      BindingResult bindingResult,
                      @RequestParam("file") MultipartFile file,
                      Model model) throws IOException {
        post.setAuthor(user);
        if(bindingResult.hasErrors()){
            Map<String, String> errors = ControllerUtil.getErrors(bindingResult);
            //model.addAttribute("errors", errors);
            model.mergeAttributes(errors);
        }else {
            saveFile(file, post);
            postRepo.save(post);
        }
        Iterable<Post> posts = postRepo.findAll();
        model.addAttribute("posts", posts);
        return "main";
    }


    private void saveFile(@RequestParam("file") MultipartFile file, Post post) throws IOException {
        if(file != null){
            File uploadDir = new File(uploadPath);
            File tempDir = new File("/tmp" + uploadPath);
            if(!uploadDir.exists() || !tempDir.exists()){
                tempDir.mkdirs();
                uploadDir.mkdirs();
            }

            String uuidFile = UUID.randomUUID().toString();
            String currentPath = uploadDir.getPath();


            String resultFilename = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(currentPath + "/" + resultFilename));

            post.setFilename(resultFilename);
        }
    }


    @GetMapping("/profile/{user}")
    public String userPosts(@AuthenticationPrincipal User currentUser, @PathVariable User user, @RequestParam(required = false) Post post, Model model){
        User userDob = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDob.getUsername();
        Integer id = userDob.getId();
        model.addAttribute("username", username);
        model.addAttribute("id", id);
        Set<Post> posts = user.getPosts();
        model.addAttribute("posts", posts);
        model.addAttribute("post", post);
        model.addAttribute("isCurrentUser", currentUser.equals(user));
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
            saveFile(file, post);
            postRepo.save(post);
        }
        User userTrue = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer idTrue = user.getId();
        model.addAttribute("id", idTrue);
        return "redirect:/profile/" + user;
    }
   /* @PostMapping("/filter")
    public String filter(@RequestParam String filter, Model model){
        Iterable<Post> posts;
        if(filter != null && !filter.isEmpty()) {
            posts = postRepo.findByTag(filter);
        }
        else {posts = postRepo.findAll();}
        model.addAttribute("posts", posts);

        return "main";
    }*/
}

