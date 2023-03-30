package top.rrricardo.postcalendarbackend.mappers;

import org.apache.ibatis.annotations.Mapper;
import top.rrricardo.postcalendarbackend.models.Road;

import java.util.List;

@Mapper
public interface RoadMapper {

    List<Road> getRoads();

    Road getRoadById(int id);

    void createRoad(Road road);

    void updateRoad(Road road);

    void deleteRoad(int id);
}
