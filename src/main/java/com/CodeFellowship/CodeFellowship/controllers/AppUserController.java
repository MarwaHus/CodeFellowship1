package com.CodeFellowship.CodeFellowship.controllers;

import com.CodeFellowship.CodeFellowship.models.AppUser;
import com.CodeFellowship.CodeFellowship.models.Post;
import com.CodeFellowship.CodeFellowship.repository.AppUserRepository;
import com.CodeFellowship.CodeFellowship.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


@Controller
public class AppUserController {
    @Autowired
    AppUserRepository appUserRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;
    @GetMapping("/login")
    public String getLoginPage() {
        return "login.html";
    }

    @GetMapping("/signup")
    public String getSignupPage() {
        return "signup.html";
    }

    @PostMapping("/signup")
    public RedirectView createUser(String username, String password, String firstName, String lastName, @DateTimeFormat(pattern="yyyy-MM-dd") Date dateOfBirth, String bio) {
        String encryptedPassword = passwordEncoder.encode(password);
        AppUser appUser = new AppUser(username, encryptedPassword, firstName, lastName, dateOfBirth, bio);
        appUserRepository.save(appUser);
        authWithHttpServletRequest(username, password);
        Authentication authentication = new UsernamePasswordAuthenticationToken(appUser, null, appUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new RedirectView("/");
    }

    @GetMapping("/")
    public String getHomePage(Principal p, Model m){
        if(p != null){
            String username = p.getName();
            AppUser appUser= appUserRepository.findByUsername(username);
            m.addAttribute("username", username);

        }
        return "home.html";
    }
    @GetMapping("/users/{id}")
    public String getUserInfo(Model m, @PathVariable Long id) {
        AppUser user = appUserRepository.findById(id).orElseThrow();
        m.addAttribute("username", user.getUsername());
        m.addAttribute("firstname", user.getFirstName());
        m.addAttribute("lastname", user.getLastName());
        m.addAttribute("dateOfBirth", user.getDateOfBirth());
        m.addAttribute("bio", user.getBio());
        return "profile.html";
    }

    @GetMapping("/myprofile")
    public String getUserProfile (Principal p, Model model){
        String username = p.getName();
        AppUser appUser = appUserRepository.findByUsername(username);
        model.addAttribute("username", username);
        model.addAttribute("firstName",appUser.getFirstName());
        model.addAttribute("lastName",appUser.getLastName());
        model.addAttribute("dateOfBirth",appUser.getDateOfBirth());
        model.addAttribute("bio",appUser.getBio());

        return "userprofile.html";

    }

    @PostMapping("/posts")
    public String createPost(@RequestParam String body, Principal principal) {
        String username = principal.getName();
        AppUser appUser = appUserRepository.findByUsername(username);
        LocalDateTime createdAt = LocalDateTime.now();
        Post newPost = new Post();
        newPost.setBody(body);
        newPost.setAppUser(appUser);
        newPost.setCreatedAt(createdAt);
        postRepository.save(newPost);
        return "redirect:/";
    }
    @GetMapping("/users")
    public String getAllUsers(Principal principal, Model model) {
        if (principal != null) {

            List<AppUser> appUsers=appUserRepository.findAll();
            model.addAttribute("applicationUsers", appUsers);
        }
        return "users.html";
    }

   @PutMapping("/users/{id}")
    public RedirectView editUserInfo(Model m, Principal p, @PathVariable Long id, String username, RedirectAttributes redirectAttributes)
    {
        if ((p != null) && (p.getName().equals(username)))
        {
            AppUser appUser = appUserRepository.findById(id).orElseThrow();
            appUser.setUsername(username);
            appUserRepository.save(appUser);
        }
        else
        {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot edit another user's page!");
        }

        return new RedirectView("/users/" + id);
    }


    public void authWithHttpServletRequest(String username, String password) {
        try {
            request.login(username, password);
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }
}