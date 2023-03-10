package top.rrricardo.postcalendarbackend.models;


public class User {
    private int id;
    private String username;
    private String password;

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

    /**
     * 获得用户密码
     */
    public String getPassword() {
        return password;
    }


}
