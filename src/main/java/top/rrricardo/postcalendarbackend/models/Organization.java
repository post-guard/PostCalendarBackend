package top.rrricardo.postcalendarbackend.models;

/**
 * 组织
 */
public class Organization {
    private int id;
    private String name;
    private String details;

    public Organization(String name, String details) {
        this.name = name;
        this.details = details;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
