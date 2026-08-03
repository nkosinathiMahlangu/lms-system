package team2.lmssystem.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import team2.lmssystem.entity.User;
import team2.lmssystem.repository.UserRepository;

import java.util.stream.Collectors;

/**
 * Loads a user from the database for Spring Security authentication.
 * Role names are stored as plain strings (e.g. "ADMIN") — Spring's hasRole()
 * prepends "ROLE_" automatically, so we must NOT store "ROLE_ADMIN" here.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Generic error message — avoids leaking whether the username exists
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> "ROLE_" + role.getName().name())
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );
    }
}
