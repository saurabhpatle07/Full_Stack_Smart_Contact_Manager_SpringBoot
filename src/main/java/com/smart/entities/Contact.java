package com.smart.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "CONTACT")
public class Contact {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cid;
	@NotEmpty(message="name can not be empty")
	@Size(min = 2,max = 20, message = "name size mut be in between 2 - 20 characters")
	private String name;
	private String secondName;
	@NotEmpty(message="name can not be empty")
	@Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message="Invalid email !!")
	private String email;
	private String phone;
	private String work;
	@Column(length = 50000)
	private String description;
	private String image;
	@ManyToOne
	@JsonIgnore
	private User user;
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSecondName() {
		return secondName;
	}
	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getWork() {
		return work;
	}
	public void setWork(String work) {
		this.work = work;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Contact(int cid, String name, String secondName, String email, String phone, String work, String description,
			String image, User user) {
		super();
		this.cid = cid;
		this.name = name;
		this.secondName = secondName;
		this.email = email;
		this.phone = phone;
		this.work = work;
		this.description = description;
		this.image = image;
		this.user = user;
	}
//	@Override
//	public String toString() {
//		return "Contact [cid=" + cid + ", name=" + name + ", secondName=" + secondName + ", email=" + email + ", phone="
//				+ phone + ", work=" + work + ", description=" + description + ", image=" + image + ", user=" + user
//				+ "]";
//	}
	public Contact() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
