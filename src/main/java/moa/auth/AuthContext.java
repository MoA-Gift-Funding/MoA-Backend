package moa.auth;


import static moa.auth.AuthExceptionType.UNAUTHORIZED;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class AuthContext {

    private Long memberId;

    public boolean unAuthenticated() {
        return memberId == null;
    }

    public Long getMemberId() {
        if (unAuthenticated()) {
            throw new AuthException(UNAUTHORIZED);
        }
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
}
