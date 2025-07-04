package com.example.webcarproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import com.example.webcarproject.mapper.AuthMapper;
import com.example.webcarproject.entity.Auth;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthMapper authMapper;

    public enum PasswordStrength {
        WEAK, MEDIUM, STRONG
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        String username = params.get("username");
        String password = params.get("password");
        String confirmPassword = params.get("confirmPassword");

        if (username == null || username.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "用户名不能为空");
            return result;
        }

        if (!password.equals(confirmPassword)) {
            result.put("success", false);
            result.put("message", "两次输入的密码不一致");
            return result;
        }

        PasswordStrength strength = checkPasswordStrength(password);
        if (strength == PasswordStrength.WEAK) {
            result.put("success", false);
            result.put("message", "密码强度不足，请使用字母+数字组合");
            return result;
        }

        if (authMapper.countByUsername(username) > 0) {
            result.put("success", false);
            result.put("message", "用户名已存在");
            return result;
        }

        String salt = generateSalt();
        String encryptedPwd = DigestUtils.md5DigestAsHex((password + salt).getBytes());

        Auth auth = new Auth();
        auth.setUsername(username);
        auth.setPassword(encryptedPwd);

        auth.setSalt(salt);

        int rows = authMapper.insert(auth);
        if (rows > 0) {
            result.put("success", true);
            result.put("message", "注册成功");
        } else {
            result.put("success", false);
            result.put("message", "注册失败");
        }

        return result;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        String username = params.get("username");
        String password = params.get("password");

        Auth auth = authMapper.findByUsername(username);
        if (auth == null) {
            result.put("success", false);
            result.put("message", "用户名或密码错误");
            return result;
        }

        String encryptedPwd = DigestUtils.md5DigestAsHex((password + auth.getSalt()).getBytes());
        if (!encryptedPwd.equals(auth.getPassword())) {
            result.put("success", false);
            result.put("message", "用户名或密码错误");
            return result;
        }

        result.put("success", true);
        result.put("message", "登录成功");
        result.put("userInfo", Map.of(
                "username", auth.getUsername(),
                "createTime", auth.getCreateTime(),
                "updateTime", auth.getUpdateTime()
        ));

        return result;
    }

    @GetMapping("/checkUsername")
    public Map<String, Object> checkUsername(@RequestParam String username) {
        Map<String, Object> result = new HashMap<>();
        boolean exists = authMapper.countByUsername(username) > 0;
        result.put("exists", exists);
        return result;
    }

    private PasswordStrength checkPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return PasswordStrength.WEAK;
        }

        boolean hasLetter = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else {
                hasSpecial = true;
            }
        }

        if (hasLetter && hasDigit && hasSpecial) {
            return PasswordStrength.STRONG;
        } else if (hasLetter && hasDigit) {
            return PasswordStrength.MEDIUM;
        } else {
            return PasswordStrength.WEAK;
        }
    }

    private String generateSalt() {
        return Long.toHexString(Double.doubleToLongBits(Math.random()));
    }
}
