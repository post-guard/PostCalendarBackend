package top.rrricardo.postcalendarbackend.models;

public class Road {

    private int id;
    private String name;
    private Place startPlace;
    private Place endPlace;
    private int length;

    public Road(int id, String name, Place startPlace, Place endPlace, int length){
        this.id = id;
        this.name = name;
        this.startPlace = startPlace;
        this.endPlace = endPlace;
        this.length = length;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Place getStartPlace() {
        return startPlace;
    }

    public void setStartPlace(Place startPlace) {
        this.startPlace = startPlace;
    }

    public Place getEndPlace() {
        return endPlace;
    }

    public void setEndPlace(Place endPlace) {
        this.endPlace = endPlace;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
