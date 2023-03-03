package com.example.userservice.security;


import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    // 2가지 메소드 재정의

    private UserService userService;
    private Environment env;

    public AuthenticationFilter(AuthenticationManager authenticationManager, UserService userService, Environment env) {
        super(authenticationManager);
        this.userService = userService;
        this.env = env;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("AuthenticationFilter.attemptAuthentication 들어옴" + request);
        try {
            // post형태이기에 inputstream을 통해 받아옴
            RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);
            // token으로 변경 후 토큰 리턴
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>())
            );
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // login의 최종 도착지 (클라이언트에게 토큰 발행)
    @Override
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // User : spring security에서 제공하는 user
        String userName = ((User) authResult.getPrincipal()).getUsername();
        log.debug(((User) authResult.getPrincipal()).getUsername());

        UserDto userDetails = userService.getUserDetailsByEmail(userName);

        System.out.println("userDetails + : " + userDetails);

        System.out.println(env.getProperty("token.secret"));

        // payload암호화용 KEY
        String KEY = "0123456789abcdef0123456789abcdef";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String expDate = sdf.format(new Date(System.currentTimeMillis() + 86400000));

//        String json = "{\"id\":\"0033116651\", \"exp\":\""+expDate+"\"}"; // JSON 객체
        String json = "{\"exp\":\""+expDate+"\", \"id\":\"0033116651\"}"; // JSON 객체

        String encrypted = encrypt(json, KEY);
        System.out.println("Encrypted text: " + encrypted);

        String decrypted = decrypt(encrypted, KEY);
        System.out.println("Decrypted text: " + decrypted);

        String token = Jwts.builder()
                .setSubject(userDetails.getName())
                .setHeaderParam("ch", 1)
                .setHeaderParam("tokenType", 1)
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(SignatureAlgorithm.HS512, "secret")
                .claim("body", encrypted)
                .compact();

        response.addHeader("token", token);
        response.addHeader("userId", userDetails.getName());
        response.addHeader("service-type", "GO");
        response.addHeader("encrypted", encrypted);
        response.addHeader("decrypted", decrypted);
        response.addHeader("plain", json);

    }


    /**
     * payload json 암호화용 메소드
     * @param plainText
     * @param key
     * @return
     */
    private static String encrypt(String plainText, String key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"));
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(cipherText);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * payload json 복호화용 메소드
     * @param cipherText
     * @param key
     * @return
     */
    private static String decrypt(String cipherText, String key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"));
            byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(plainText, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
