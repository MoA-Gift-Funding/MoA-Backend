package moa.global.auth;

import static java.util.Objects.requireNonNull;
import static moa.global.auth.AuthExceptionType.FORBIDDEN;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.member.domain.MemberStatus;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthArgumentResolver implements HandlerMethodArgumentResolver {

    private final MemberRepository memberRepository;
    private final AuthContext authContext;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Auth.class)
                && parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        Auth authAt = parameter.getParameterAnnotation(Auth.class);
        requireNonNull(authAt);
        List<MemberStatus> permitStatus = Arrays.asList(authAt.permit());
        Member member = memberRepository.getById(authContext.getMemberId());
        if (permitStatus.contains(member.getStatus())) {
            return member.getId();
        }
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        log.info("{} 상태의 회원의 [{} {}] 접근 차단.", member.getStatus(), request.getMethod(), request.getRequestURI());
        throw new AuthException(FORBIDDEN);
    }
}
