package top.rrricardo.postcalendarbackend.annotations;

import top.rrricardo.postcalendarbackend.enums.AuthorizePolicy;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Inherited
public @interface Authorize {
    AuthorizePolicy policy() default AuthorizePolicy.ONLY_LOGIN;
}
