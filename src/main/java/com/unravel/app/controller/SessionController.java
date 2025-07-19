package com.unravel.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.unravel.app.manager.SessionManager;

@RestController
@RequestMapping("/sessions")
public class SessionController {
    private final SessionManager sessionManager;

    public SessionController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @PostMapping("/login/{userId}")
    public ResponseEntity<String> login(@PathVariable String userId) {
        return ResponseEntity.ok(sessionManager.login(userId));
    }

    @PostMapping("/logout/{userId}")
    public ResponseEntity<String> logout(@PathVariable String userId) {
        return ResponseEntity.ok(sessionManager.logout(userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<String> getSession(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(sessionManager.getSessionDetails(userId));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}
