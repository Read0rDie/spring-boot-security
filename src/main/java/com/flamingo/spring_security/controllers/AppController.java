package com.flamingo.spring_security.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.flamingo.spring_security.models.User;
import com.flamingo.spring_security.utils.TokenUtils;

@Controller
public class AppController {

	@Autowired
	OAuth2AuthorizedClientService authorizedClientService;

	@Autowired
	private TokenUtils tokenUtils;

	@GetMapping({ "/", "/home" })
	public String getLandingPage(Model model) {
		User authUser = tokenUtils.getUser();
		model.addAttribute("user", authUser);
		return "home";
	}

	@GetMapping("/user")
	public String getUserDetails(Model model) {
		User authUser = tokenUtils.getUser();
		model.addAttribute("first_name", authUser != null ? authUser.getFirstName() : "");
		model.addAttribute("last_name", authUser != null ? authUser.getLastName() : "");
		model.addAttribute("email", authUser != null ? authUser.getEmail() : "");
		model.addAttribute("avatar", authUser != null ? authUser.getAvatarURL() : "");
		return "userDetails";
	}

	@GetMapping("/success")
	public String loginSuccess(Model model) {
		return "redirect:user";
	}
}
