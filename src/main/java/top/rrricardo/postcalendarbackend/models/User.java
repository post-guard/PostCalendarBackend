package top.rrricardo.postcalendarbackend.models;

public class User {
    private int id;
    private String emailAddress;
    private String username;
    private String password;

    public User(String emailAddress, String username, String password) {
        this.emailAddress = emailAddress;
        this.username = username;
        this.password = password;
    }

    /**
     * 获得用户id
     */
    public int getId() {
        return id;
    }

    /**
     * 获得用户名
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * 获得用户密码
     */
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
