package main.service;

import com.mysql.cj.util.StringUtils;
import main.model.Role;
import main.model.User;
import main.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MailSender mailSender;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username);
    }

    public boolean addUser(User user){
        User userDob = userRepo.findByUsername(user.getUsername());

        if(userDob != null){
            return false;
        }
        if(user.getUsername().equals("admin")){
            user.setActive("true");
            user.setRoles(Collections.singleton(Role.ADMIN));
            user.setActivationCode(UUID.randomUUID().toString());
            userRepo.save(user);
        }
        else {
            user.setActive("true");
            user.setRoles(Collections.singleton(Role.USER));
            user.setActivationCode(UUID.randomUUID().toString());
            userRepo.save(user);
        }

        if(!StringUtils.isEmptyOrWhitespaceOnly(user.getEmail())){
            String message = String.format("Hello, %s!!! Welcome to our forum. We send you the activation code. " +
                    "Pls, go through the link(http://localhost:8080/activate/%s)", user.getUsername(), user.getActivationCode());
            mailSender.send(user.getEmail(), "RegistraionCode", message);
        }
        return true;
    }

    public boolean activateUser(String code) {
        User user = userRepo.findByActivationCode(code);
        if(user == null){
            return false;
        }
        user.setActivationCode(null);
        userRepo.save(user);
        return true;
    }

    public void subscribe(User currentUser, User user) {
        user.getSubscribers().add(currentUser);
        userRepo.save(user);
    }

    public void unsubscribe(User currentUser, User user) {
        user.getSubscribers().remove(currentUser);
        userRepo.save(user);
    }
}
