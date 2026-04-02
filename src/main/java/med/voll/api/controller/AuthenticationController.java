package med.voll.api.controller;

import jakarta.validation.Valid;
import med.voll.api.dto.LoginDto;
import med.voll.api.dto.RegisterDto;
import med.voll.api.dto.TokenDto;
import med.voll.api.infra.security.TokenService;
import med.voll.api.model.User;
import med.voll.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginDto data) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var authentication = authenticationManager.authenticate(authenticationToken);
        var token = tokenService.generateToken((User) authentication.getPrincipal());
        return ResponseEntity.ok(new TokenDto(token));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterDto data) {
        if (userRepository.findByLogin(data.login()) != null) {
            return ResponseEntity.badRequest().build();
        }
        String encryptedPassword = passwordEncoder.encode(data.password());
        User newUser = new User(null, data.login(), encryptedPassword, data.role());
        userRepository.save(newUser);
        return ResponseEntity.ok().build();
    }
}
