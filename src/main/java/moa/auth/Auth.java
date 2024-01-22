package moa.auth;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import moa.member.domain.MemberStatus;

@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Auth {

    MemberStatus[] permit();
}
