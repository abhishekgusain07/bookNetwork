package com.gusain.book.auth;

import com.gusain.book.email.EmailService;
import com.gusain.book.email.EmailTemplateName;
import com.gusain.book.role.RoleRepository;
import com.gusain.book.user.Token;
import com.gusain.book.user.TokenRepository;
import com.gusain.book.user.User;
import com.gusain.book.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;

    @Value("${application.security.mailing.frontend.activation-url}")
    private  String activationUrl;

    public void register(RegistrationRequest request) throws MessagingException {
             var userRole = roleRepository.findByName("USER").orElseThrow(() -> new IllegalStateException("Role USER not found"));
             var user = User.builder()
                     .firstname(request.getFirstname())
                     .lastname(request.getLastname())
                     .email(request.getEmail())
                     .password(passwordEncoder.encode(request.getPassword()))
                     .accountLocked(false)
                     .enabled(false)
                     .roles(List.of(userRole))
                     .build();

             userRepository.save(user);

             sendValidationEmail(user);

    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        System.out.println(newToken);
        //send mail
        emailService.sendEmail(
                user.getEmail(),
                user.fullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account Activation"
        );
    }

    private String generateAndSaveActivationToken(User user) {
        //generate token
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int length) {
        String chars = "0123456789";
        StringBuilder activationCode = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for(int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(chars.length());
            activationCode.append(chars.charAt(randomIndex));
        }
        return activationCode.toString();
    }
}
