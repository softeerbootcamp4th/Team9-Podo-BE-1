package com.softeer.podoarrival.security;

import com.softeer.podoarrival.user.model.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Data
public class AuthInfo {
    private String name;
    private String phoneNum;
    private Role role;
}
