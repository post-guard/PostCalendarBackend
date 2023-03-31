package top.rrricardo.postcalendarbackend.models;

import top.rrricardo.postcalendarbackend.enums.PlaceType;

public class Place {

    private int id;
    private String name;

    private int x; //横坐标
    private int y; //纵坐标
    private int placeType;

    public Place(String name, int x, int y, PlaceType placeType){
        this.name = name;
        this.x = x;
        this.y = y;
        this.placeType = placeType.ordinal();
    }

    public Place(int id, String name, int x, int y, int placeType) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.placeType = placeType;
    }

    public Place(){

    }
    public int getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getX() { return x; }

    public void setX(int x) {this.x = x; }

    public int getY() { return y; }

    public void setY(int y) { this.y = y; }

    public int getPlaceType() { return placeType; }

    public void setPlaceType(int placeType) { this.placeType = placeType; }
}
