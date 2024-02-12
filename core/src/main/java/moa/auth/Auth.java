package moa.auth;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import io.swagger.v3.oas.annotations.Hidden;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import moa.member.domain.MemberStatus;

// TODO api 모듈로 이동
@Hidden
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Auth {

    MemberStatus[] permit();
}
