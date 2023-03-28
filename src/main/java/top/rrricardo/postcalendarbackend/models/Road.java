package top.rrricardo.postcalendarbackend.models;

public class Road {

    private int id;
    private Place startPlace;
    private Place endPlace;
    private int length;

    public Road(int id, Place startPlace, Place endPlace, int length){
        this.id = id;
        this.startPlace = startPlace;
        this.endPlace = endPlace;
        this.length = length;
    }

    public int getId() { return id; }

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
