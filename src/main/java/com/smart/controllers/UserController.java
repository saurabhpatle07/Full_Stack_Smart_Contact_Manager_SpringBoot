package com.smart.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;



@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@ModelAttribute
	public void addCommonData(Model model,Principal principal) {
		String name = principal.getName();
		
		User user = userRepository.getUserByName(name);
		model.addAttribute(user);
	}
	
	@GetMapping("/index")
	public String index(Model model,Principal principal) {
		model.addAttribute("title", "User Dashboard");
		return "normal/dashboard";
	}
	
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add contact");
		model.addAttribute("contact", new Contact());
		
		return "normal/add_contact_form";
	}
	
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute("contact") @Valid Contact contact, BindingResult bindingResult,
			@RequestParam("profileImage") MultipartFile file, Model model, Principal principal,HttpSession session) {
		
	    try{
	    
	    String name = principal.getName();
	    User user = this.userRepository.getUserByName(name);
	    // processing and uploading image
	    
	    
	    if(file.isEmpty()) {
	    	contact.setImage("contact.png");
	    	System.out.println("file is empty");
	    }else {
	    	// upload the file to the folder and update the name to contact
	    	contact.setImage(file.getOriginalFilename());
	    	File saveFile = new ClassPathResource("static/images").getFile();
	    	Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
	    	
	    	Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
	    }
	    contact.setUser(user);
	    user.getContacts().add(contact);
	    this.userRepository.save(user);
	    // success message
	    session.setAttribute("message", new Message("Your contact added successfully !! Add more","success"));
	    } catch(Exception e) {
	    	System.out.println("ERROR" + e.getMessage());
	    	e.printStackTrace();
	    	session.setAttribute("message", new Message("Something went wrong !! try again !!","danger"));
	    }
	    return "normal/add_contact_form";
	}
	
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page,Model model,Principal principal) {
		model.addAttribute("title", "view contacts");
		String name = principal.getName();
		User user = this.userRepository.getUserByName(name);
		Pageable pageable = PageRequest.of(page, 5);
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);
		model.addAttribute("contacts",contacts);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		return "normal/show_contacts";
	}
	
	// show user details
	
	@GetMapping("/{cId}/contacts")
	public String showDetails(@PathVariable("cId") Integer cId,Model model,Principal principal) {
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		
		
		
		String name = principal.getName();
		User user = this.userRepository.getUserByName(name);
		
		if(user.getId() == contact.getUser().getId()) {
			model.addAttribute("title", "Contact Details");
			model.addAttribute("contact", contact);
		}
		return "normal/contact_details";
	}
	
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId,Principal principal,HttpSession session) {
		String name = principal.getName();
		User user = this.userRepository.getUserByName(name);
		Optional<Contact> optionalContact = this.contactRepository.findById(cId);
		Contact contact = optionalContact.get();
		
		if(user.getId() == contact.getUser().getId()) {
		this.contactRepository.delete(contact);
		session.setAttribute("message", new Message("Contact deleted successfully.....","success"));
		}
		return "redirect:/user/show-contacts/0";
	}
	
	@PostMapping("/update/{cId}")
	public String updateContact(@PathVariable("cId") Integer cId,
			Model model) {
		Contact contact = this.contactRepository.findById(cId).get();
		
		model.addAttribute("contact", contact);
		model.addAttribute("title", "Update Contact");
		return "normal/update_contact_form";
	}
	
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,
			Model model,HttpSession session,Principal principal){
		try {
			Contact oldContact = contactRepository.findById(contact.getCid()).get();
			if(!file.isEmpty()) {
				// delete old file
				File deleteFile = new ClassPathResource("static/images").getFile();
				File file1 = new File(deleteFile, oldContact.getImage());
				file1.delete();
				// update new file
				File saveFile = new ClassPathResource("static/images").getFile();
		    	Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
		    	
		    	Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		    	contact.setImage(file.getOriginalFilename());
			}else {
				contact.setImage(oldContact.getImage());
			}
			String name = principal.getName();
			User user = this.userRepository.getUserByName(name);
			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("Your contact is updated....","success"));
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return "redirect:/user/"+contact.getCid()+"/contacts";
	}
	
	// profile handler
	@GetMapping("/profile")
	public String profileHandler(Model model) {
		model.addAttribute("title", "Profile Page");
		return "normal/profile";
	}
	
	// open setting handler
	
	@GetMapping("/settings")
	public String openSettings(Model model) {
		
		model.addAttribute("title", "settings");
		
		return "normal/settings";
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword,Principal principal,
			HttpSession session) {
		
		String name = principal.getName();
		User user = this.userRepository.getUserByName(name);
		if(bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
			user.setPassword(bCryptPasswordEncoder.encode(newPassword));
			userRepository.save(user);
			session.setAttribute("message", new Message("Your Password is changed successfully.......","success"));
		}else {
			session.setAttribute("message", new Message("Wrong Password","danger"));
		}
	
		return "redirect:/user/settings";
	}
	
	
}










