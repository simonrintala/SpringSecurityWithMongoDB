package se.java.security.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import se.java.security.dto.AuthRequest;
import se.java.security.dto.AuthResponse;
import se.java.security.dto.RegisterRequest;
import se.java.security.dto.RegisterResponse;
import se.java.security.models.Role;
import se.java.security.models.User;
import se.java.security.services.UserService;
import se.java.security.util.JwtUtil;

import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        // check if username already exists
        if(userService.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Username already exists.");
        }

        // map the AuthRequest to a User entity
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(registerRequest.getPassword());

        // assign roles
        if(registerRequest.getRoles() == null || registerRequest.getRoles().isEmpty()) {
            user.setRoles(Set.of(Role.USER));
        } else {
            user.setRoles(registerRequest.getRoles());
        }

        // register the user using UserService
        userService.registerUser(user);

        // create respons object
        RegisterResponse response = new RegisterResponse(
                "User registered successfully",
                user.getUsername(),
                user.getRoles()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login (@Valid @RequestBody AuthRequest authRequest, HttpServletResponse response) {

        try {
            // authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );

            // set authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // get UserDetails
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // generate JWT token
            String jwt = jwtUtil.generateToken(userDetails);
            ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwt)
                    .httpOnly(true) // prevents javascript to get cookie
                    .secure(false) //IMPORTANT TO CHANGE IN PRODUCTION TO TRUE
                    .path("/")  // cookies is available in all application
                    .maxAge(10 * 60 * 60) // valid for 10h
                    .sameSite("Strict") // Lax & None
                    .build();

            // create response object
            AuthResponse authResponse = new AuthResponse(
                    "Login successful",
                    userDetails.getUsername(),
                    userService.findByUsername(userDetails.getUsername()).getRoles()
            );

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(authResponse);

            // return response with cookie-header and body
        } catch (AuthenticationException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Incorrect username or password");
        }
    }

    @PostMapping ("/logout")
    public ResponseEntity<?> logout (HttpServletRequest request, HttpServletResponse response) {
        // skapa en utgången cookie för att ersätta den befintliga jwt cookien
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false) // VIKTIGT!! ändra i production
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        //response.addHeader

        //rensa security kontexten
        SecurityContextHolder.clearContext();

        //returnera svar med utgången cookie
        return ResponseEntity.ok()
                // ta bort denna rraden och använda rad 135
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body("Logout successful!");
    }

    // kolla om en user är authenticated
    @GetMapping ("/check")
    public ResponseEntity<?> checkAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // kontrollera om användaren är authenticated
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        // returnera user info om authentication
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());

        return ResponseEntity.ok(new AuthResponse(
                "Authenticated",
                user.getUsername(),
                user.getRoles()
        ));
    }





















}