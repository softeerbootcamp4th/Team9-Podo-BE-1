package com.softeer.podoarrival.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.softeer.podoarrival.security.jwt.exception.InvalidTokenException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Base64;
import java.util.Date;

@Component
public class TokenProvider {

    @Value("${secret.jwt}")
    private String baseSecretKey;

    /**
     * 암호화된 토큰을 복호화하기
     */
    public JWTClaimsSet validateTokenAndGetClaimsSet(String token) {
        byte[] secretKey = getSecretKey();

        try {
            JWEObject jweObject = JWEObject.parse(token);
            jweObject.decrypt(new DirectDecrypter(secretKey));
            // payload 추출
            SignedJWT signedJWT = jweObject.getPayload().toSignedJWT();

            JWSVerifier verifier = new MACVerifier(secretKey);
            if (!signedJWT.verify(verifier)) {
                throw new InvalidTokenException("Token signature is invalid");
            }

            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            // expirationTime 검증
            Date expirationTime = claimsSet.getExpirationTime();
            if (expirationTime == null || expirationTime.before(new Date())) {
                throw new InvalidTokenException("이미 만료된 토큰입니다.");
            }

            return claimsSet;

        } catch (JOSEException | ParseException e) {
            throw new InvalidTokenException("JWE Token Decoding Error - 토큰 검증과정에서 오류 발생");
        }
    }

    public void setAttributesFromClaim(HttpServletRequest request, JWTClaimsSet claimsSet) {
        request.setAttribute("name", claimsSet.getClaim("name"));
        request.setAttribute("number", claimsSet.getClaim("number"));
        request.setAttribute("ROLE_", claimsSet.getClaim("ROLE_"));
    }

    /**
     * Bearer String 제외
     */
    public String resolveToken(String token) {
        if(token.startsWith("Bearer ")) {
            return token.replace("Bearer ", "");
        }
        return null;
    }

    private byte[] getSecretKey() {
        return Base64.getDecoder().decode(baseSecretKey);
    }
}
