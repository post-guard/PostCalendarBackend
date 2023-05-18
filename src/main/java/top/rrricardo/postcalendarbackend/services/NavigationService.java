package top.rrricardo.postcalendarbackend.services;

import top.rrricardo.postcalendarbackend.models.Place;
import top.rrricardo.postcalendarbackend.models.Road;
import top.rrricardo.postcalendarbackend.utils.generic.CustomList;

import java.util.List;

public interface NavigationService {
    /**
     * 设置地图状态为已更新
     */
    void setMapUpdated();

    /**
     * 寻找最短路径（一个起点、一个终点）
     * @param Source 起点id
     * @param Destination 终点id
     * @return 起点到终点的最短路径
     */
    CustomList<Place> findPathOneDestination(int Source, int Destination);



    /**
     * 寻找途径多点的最短路径
     * @param middlePoints 需要途径的点列表(第一个为起点Source)
     * @return 途径多点的最短路径
     */
    CustomList<Place> findPathManyDestination(CustomList<Integer> middlePoints);

    //通过地点序列找到道路序列
    CustomList<Road> getRoadsByPlace(CustomList<Place> places);
}
