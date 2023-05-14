package top.rrricardo.postcalendarbackend.models;

public class Road {

    private int id;
    private String name;
    private int startPlaceId;
    private int endPlaceId;
    private float length;

    public Road(int id, String name, int startPlaceId, int endPlaceId, float length){
        this.id = id;
        this.name = name;
        this.startPlaceId = startPlaceId;
        this.endPlaceId = endPlaceId;
        this.length = length;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public int getStartPlaceId() {
        return startPlaceId;
    }

    public void setStartPlaceId(int startPlaceId) {
        this.startPlaceId = startPlaceId;
    }

    public int getEndPlaceId() {
        return endPlaceId;
    }

    public void setEndPlaceId(int endPlaceId) {
        this.endPlaceId = endPlaceId;
    }
}
