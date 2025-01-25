package com.maxiflexy.tickethelpdeskapp.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class AppUtil {

    @Value("${app.privateKey}")
    private String privKey;

    @Value("${app.publicKey}")
    String publicKey;

    @Value("${token.exp_time}")
    String tokenExpTime;


    @Value("${refresh_token.exp_time}")
    String refreshTokenExpTime;

    public static String createRandomString(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }
    public static String createRandomInteger(int length) {
        return RandomStringUtils.randomNumeric(length);
    }

    public BaseBean generateToken(String email, String roleName) {
        BaseBean requestBean = new BaseBean();
        try {
            // Your secret key for signing (for production, store this securely)
            RSAPrivateKey privateKey = (RSAPrivateKey) getPrivateKey(privKey);
            String tokenId = UUID.randomUUID().toString();
            // Claims to include in the JWT payload
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject("Auth")  // Subject of the token (typically the user ID)
                    .issuer("app")  // Issuer of the token
                    .expirationTime(new Date(new Date().getTime() + Long.parseLong(tokenExpTime)))  // Token validity period (1 hour)
                    .claim("role", roleName)
                    .claim("email", email)
                    .jwtID(tokenId)  // Unique identifier for the JWT
                    .build();

            JWTClaimsSet refreshClaimSet = new JWTClaimsSet.Builder()
                    .expirationTime(new Date(new Date().getTime() + Long.parseLong(refreshTokenExpTime)))  // Token validity period (1 hour)
                    .jwtID(tokenId)  // Unique identifier for the JWT
                    .build();

            // Create the signed JWT using RSASSA-PKCS-v1_5 SHA-256 signing algorithm
            JWSSigner signer = new RSASSASigner(privateKey);
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256).build();
            SignedJWT signedJWT = new SignedJWT(header, claimsSet);
            SignedJWT refreshTokenJWT = new SignedJWT(header, refreshClaimSet);
            signedJWT.sign(signer);
            refreshTokenJWT.sign(signer);

            // Serialize the signed JWT to a string representation
            String jwtString = signedJWT.serialize();
            String refreshToken = refreshTokenJWT.serialize();
            requestBean.setString("jwt", jwtString);
            requestBean.setString("refresh-token", refreshToken);
            requestBean.setString("token-expiration", String.valueOf(claimsSet.getExpirationTime().getTime()));
            requestBean.setString("token-id", tokenId);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return requestBean;
    }

     public static PrivateKey getPrivateKey(String base64PrivateKey) {
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(
                Base64.getDecoder().decode(
                        base64PrivateKey.getBytes()));
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
        }
        try {
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
        }
        return privateKey;
    }

    public static PublicKey getPublicKey(String base64PublicKey) {
        PublicKey publicKey = null;

        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    public String getPrivKey() {
        return privKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public boolean verifyToken(String token, BaseBean tokenBean) {
        boolean isValid = false;
        try {
            RSAPublicKey publickey = (RSAPublicKey) getPublicKey(getPublicKey());
            RSAPrivateKey privateKey = (RSAPrivateKey) getPrivateKey(getPrivKey());

            JWTVerifier jwtV = JWT.require(Algorithm.RSA256(publickey, privateKey)).build();
            DecodedJWT jwtD = jwtV.verify(token);
            tokenBean.setString("refresh-id", jwtD.getId());
            isValid = true;
        } catch (JWTVerificationException e) {
            log.warn("Some exception : {} failed : {}", token, e.getMessage());
        }
        return isValid;

    }
}
