package top.rrricardo.postcalendarbackend.enums;

public enum UserPermission {
    USER(0, "user"),
    ADMIN(1, "administrator"),
    SUPER(2, "superman");

    private final int code;
    private final String name;

    UserPermission(int code, String name) {
        this.code = code;
        this.name = name;
    }


    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
