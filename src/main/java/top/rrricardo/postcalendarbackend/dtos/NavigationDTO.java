package top.rrricardo.postcalendarbackend.dtos;

import top.rrricardo.postcalendarbackend.models.Place;
import top.rrricardo.postcalendarbackend.models.Road;

import java.util.List;

public class NavigationDTO {
    private List<Place> places;
    private List<Road> roads;

    public NavigationDTO(List<Place> places, List<Road> roads) {
        this.places = places;
        this.roads = roads;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public List<Road> getRoads() {
        return roads;
    }
}
