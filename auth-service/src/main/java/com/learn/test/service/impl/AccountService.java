package com.learn.test.service.impl;

import com.learn.test.entity.Account;
import com.learn.test.entity.User;
import com.learn.test.exception.BadCredentialsException;
import com.learn.test.exception.ConflictException;
import com.learn.test.exception.ResourceNotFoundException;
import com.learn.test.repository.AccountRepository;
import com.learn.test.service.IAccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService implements IAccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    @Value("${app.frontend.reset-password-url}")
    private String resetPasswordUrl;

    @Override
    public void createAccount(User user, String email, String rawPassword) {
        if(accountRepository.findByEmail(email).isPresent()) {
            throw new ConflictException("Email exists");
        }

        Account account = Account.builder()
                .email(email)
                .pass(passwordEncoder.encode(rawPassword))
                .user(user)
                .build();
        user.setAccount(account);
        accountRepository.save(account);
    }

    public Account verifyLogin(String email, String rawPassword) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Wrong login info"));
        if(!passwordEncoder.matches(rawPassword, account.getPass())) {
            throw new BadCredentialsException("Wrong password");
        }
        return account;
    }

    public void changePassword(Account account, String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, account.getPass())) {
            throw new BadCredentialsException("Old password is incorrect");
        }
        account.setPass(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    @Override
    public String forgetPassword(String email) {
        String token = UUID.randomUUID().toString();
        Optional<Account> optionalAccount = accountRepository.findByEmail(email);

        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            account.setResetToken(token);
            accountRepository.save(account);

            String resetLink = resetPasswordUrl + "/reset-password" + "?token=" + token;

            String content = "Click this link to reset your password: " + resetLink;
            emailService.sendEmail(account.getEmail(), "Reset your password", content);
        }

        return "A password reset link has been sent.";
    }


    @Override
    @Transactional
    public String resetPassword(String token, String newPassword) {
        Account account = accountRepository.findByResetToken(token)
                .orElseThrow(() -> new BadCredentialsException("Token not valid"));

        account.setPass(passwordEncoder.encode(newPassword));
        account.setResetToken(null);
        accountRepository.save(account);

        return "Password change successfully";
    }
}

