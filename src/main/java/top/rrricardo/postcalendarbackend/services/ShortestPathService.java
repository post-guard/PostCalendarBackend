package top.rrricardo.postcalendarbackend.services;

import top.rrricardo.postcalendarbackend.models.Place;

import java.util.List;

/**
 * 提供最短路径算法（迪杰斯特拉算法）的接口
 */
public interface ShortestPathService {

    /**
     * 传入参数为：起点的id、终点的id
     * 返回最短路径的List
    */
    public List<Place> Dijkstra(int startId, int endId);
}
