package hg.userservice.infrastructure.security;

import com.example.commonproject.exception.BadRequestException;
import hg.userservice.core.entity.UserEntity;
import hg.userservice.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(
                () -> new BadRequestException("Not exists email.")
        );

        return new LoginUser(userEntity);
    }
}
