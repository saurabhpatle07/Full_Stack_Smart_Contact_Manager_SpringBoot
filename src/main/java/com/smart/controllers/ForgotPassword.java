package com.smart.controllers;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.services.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotPassword {
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    private Random random = new Random(); // Moved inside the class
    
    // No need to initialize the OTP here
    
    @GetMapping("/forgot_password")
    public String forgotPassword() {
        return "forgot_password";
    }
    
    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam("email") String email, HttpSession session) {
        int otp = random.nextInt(999999); // Generate new OTP every time
        
        String subject = "OTP from SCM";
        String message = " OTP = " + otp;
        
        boolean sendEmail = emailService.sendEmail(email, "softengg18@gmail.com", subject, message);
        
        if (sendEmail) {
            session.setAttribute("myOtp", otp);
            session.setAttribute("email", email);
            return "verify_otp";
        } else {
            session.setAttribute("message", "Check your email id !!!");
            return "forgot_password";
        }
    }
    
    
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") Integer otp, HttpSession session) {
        Integer myOtp = (Integer) session.getAttribute("myOtp"); 
        String email = (String) session.getAttribute("email");
        
        if (myOtp != null && myOtp.equals(otp)) { 
        	
        	User user = this.userRepository.getUserByName(email);
        	if(user == null) {
        		// user does not exist
        		session.setAttribute("message", "User does not exist !!!, you should create new account !!");
        		return "forgot_password";
        	}else {
        		// you can change password
        		session.setAttribute("email", email);
        		return "password_change_form";
        	}
            
        } else {
            session.setAttribute("message", "You Entered wrong otp !!");
            return "verify_otp";
        }
    }
    
    // Enter new Password form
    @PostMapping("/new-password")
    public String newPassword(@RequestParam("newPassword") String newPassword,
    		HttpSession session) {
    	String email = (String) session.getAttribute("email");
    	User user = this.userRepository.getUserByName(email);
    	user.setPassword(bCryptPasswordEncoder.encode(newPassword));
    	userRepository.save(user);
    	
    	return "redirect:/signin?change=password changed successfully !!";
    }
}
