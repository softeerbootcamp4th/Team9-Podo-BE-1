package com.softeer.podoarrival.test.service;

import com.softeer.podoarrival.security.jwt.TokenInfo;
import com.softeer.podoarrival.security.jwt.TokenProvider;
import com.softeer.podoarrival.test.model.dto.TestSignUpRequestDto;
import com.softeer.podoarrival.test.model.dto.TestSignUpResponseDto;
import com.softeer.podoarrival.test.repository.TestUserRepository;
import com.softeer.podoarrival.user.model.entity.Role;
import com.softeer.podoarrival.user.model.entity.TestUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestUserRepository testRepository;
    private final TokenProvider tokenProvider;

    @Transactional
    public TestSignUpResponseDto testSignUp(TestSignUpRequestDto dto) {

        TestUser newUser = TestUser.builder()
                .name(dto.getName())
                .phoneNum(dto.getPhoneNum())
                .role(Role.ROLE_USER)
                .build();

        testRepository.save(newUser);

        TokenInfo tokenInfo = tokenProvider.createAccessToken(dto.getName(), dto.getPhoneNum(), Role.ROLE_USER);

        return new TestSignUpResponseDto(
                newUser.getId(),
                newUser.getName(),
                newUser.getPhoneNum(),
                newUser.getRole(),
                tokenInfo.getToken(),
                tokenInfo.getExpireTime()
        );
    }
}
