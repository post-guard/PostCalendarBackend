package top.rrricardo.postcalendarbackend.annotations;

import top.rrricardo.postcalendarbackend.enums.UserPermission;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Inherited
public @interface Authorize {
    UserPermission permission() default UserPermission.USER;
}
