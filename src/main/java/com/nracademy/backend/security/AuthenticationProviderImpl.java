package com.nracademy.backend.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.nracademy.backend.exception.common.UserEmailNotFoundException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthenticationProviderImpl implements AuthenticationProvider {
    
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetailsImpl userDetailsImpl;
        
        try {
            userDetailsImpl = (UserDetailsImpl) userDetailsServiceImpl.loadUserByUsername(email);
        } catch (UserEmailNotFoundException e) {
            throw new BadCredentialsException("Email Not Found");
        }

        if (!userDetailsImpl.isEnabled())
            throw new DisabledException("User is banned");

        if (!userDetailsImpl.isAccountNonLocked())
            throw new LockedException("The Account is Locked");

        if (!passwordEncoder.matches(password, userDetailsImpl.getPassword()))
            throw new BadCredentialsException("Invalid Password");

        return new UsernamePasswordAuthenticationToken(
            userDetailsImpl, 
            password, 
            userDetailsImpl.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}