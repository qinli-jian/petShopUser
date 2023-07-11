package com.example.petshopuser.utils;

import com.example.petshopuser.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Utils {
    static public String generateRandomCode(int digitalNumber){
        String code = "";
        Random random = new Random();
        for(int i = 0;i<digitalNumber;i++){
            code+=String.valueOf(random.nextInt(9)+1);
        }
        return code;
    }

    static private String secretKey = "i&FFr%QeV2v&q352G2w2NARUoluM+(&iu_nkC6g)1armeCi6VLwSQaHIalp8dQPGk";

    static private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    static private byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
    static private Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

    static public String generateToken(User user,String role) {



        Map<String, Object> claims = new HashMap<>();
        claims.put("username",user.getName());
        claims.put("role",role);
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getAccount())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // 1000 * 60 * 60 * 24 * 7 一周
                .signWith(signingKey, signatureAlgorithm)
                .compact();
        return token;
    }

    // 校验的方法
    public static boolean checkToken(String token){
        if(token==null){
            return false;
        }else {
            try {
                Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
                System.out.println("=========");
                System.out.println(claimsJws);
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }

    // 获取token加密的信息
    public static int checkTokenClaims(String token,HashMap<String,String> info){
        if(token==null){
            return 0;
        }else {
            try {
                Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
                System.out.println("=========");
                System.out.println(claimsJws.getBody().get("role"));
                for (Map.Entry<String, String> entry : info.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if(claimsJws.getBody().get(key)!=value){// 信息对不上
                        return 0;
                    }
                }
                return 1;
            } catch (Exception e) {
                return 0;
            }
        }
    }


}
