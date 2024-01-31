package hg.userservice.infrastructure.security;

import com.example.commonproject.exception.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        LoginUser loginUser = (LoginUser) userDetailsService.loadUserByUsername((String) authentication.getPrincipal());
        String rawPassword = (String) authentication.getCredentials();

        if(!passwordEncoder.matches(rawPassword, loginUser.getPassword())) {
            throw new BadCredentialsException("Password is not matched.");
        }
        
        return loginUser;
    }
}
