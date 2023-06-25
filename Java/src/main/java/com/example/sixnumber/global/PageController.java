package com.example.sixnumber.global;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/page")
public class PageController {

	@RequestMapping("/signup")
	public ModelAndView showSignUpPage() {
		return new ModelAndView("signup");
	}

	@RequestMapping("/signin")
	public ModelAndView showLoginPage() {
		return new ModelAndView("signin");
	}

	@GetMapping("/index")
	public ModelAndView showIndexPage() {
		return new ModelAndView("index");
	}
}
