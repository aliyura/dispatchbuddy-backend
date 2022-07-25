package com.decagon.dispatchbuddy.security;
import com.decagon.dispatchbuddy.entities.User;
import com.decagon.dispatchbuddy.repositories.UserRepository;
import com.decagon.dispatchbuddy.util.App;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FutureDAOUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;
	@Autowired
	private App app;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmailOrPhoneNumber(username,username).orElseThrow(() -> new BadCredentialsException("Bad credentials"));
		return new FutureDAOUserDetails(user);
	}

}
