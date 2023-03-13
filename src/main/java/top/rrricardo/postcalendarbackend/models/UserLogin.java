package top.rrricardo.postcalendarbackend.models;

/**
 * 用户登录时的JSON模型
 */
public class UserLogin {
    private String emailAddress;

    private String password;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
