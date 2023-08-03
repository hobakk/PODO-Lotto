// package com.example.sixnumber.fixture;
//
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;
//
// @Service
// public class MyUserDetailsService implements UserDetailsService {
//
// 	@Override
// 	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
// 		GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
//
// 		return User.builder()
// 			.username(username)
// 			.password("password")
// 			.roles("USER")
// 			.authorities(authority)
// 			.build();
// 	}
// }
