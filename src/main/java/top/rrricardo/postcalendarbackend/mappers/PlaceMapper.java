package top.rrricardo.postcalendarbackend.mappers;

import org.apache.ibatis.annotations.Mapper;
import top.rrricardo.postcalendarbackend.models.Place;

import java.util.List;

@Mapper
public interface PlaceMapper {

    //获得所有地点列表
    List<Place> getPlaces();

    //通过id获取地点
    Place getPlaceById(int id);

    //通过名字获取地点
    List<Place> getPlaceByName(String name);

    //添加地点
    void createPlace(Place place);

    //修改地点
    void updatePlace(Place place);

    //删除地点
    void deletePlace(int id);
}
