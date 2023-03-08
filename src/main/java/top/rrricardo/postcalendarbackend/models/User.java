package top.rrricardo.postcalendarbackend.models;


public class User {
    private int id;
    private String username;
    private String password;
    private int permission;

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPermission() {
        return permission;
    }


}
