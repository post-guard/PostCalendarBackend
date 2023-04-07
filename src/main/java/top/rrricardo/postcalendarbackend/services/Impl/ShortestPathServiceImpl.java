package top.rrricardo.postcalendarbackend.services.Impl;

import org.springframework.stereotype.Service;
import top.rrricardo.postcalendarbackend.mappers.PlaceMapper;
import top.rrricardo.postcalendarbackend.mappers.RoadMapper;
import top.rrricardo.postcalendarbackend.models.Place;
import top.rrricardo.postcalendarbackend.models.Road;
import top.rrricardo.postcalendarbackend.services.ShortestPathService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service
public class ShortestPathServiceImpl implements ShortestPathService {

    PlaceMapper placeMapper;
    RoadMapper roadMapper;
    public ShortestPathServiceImpl(PlaceMapper placeMapper, RoadMapper roadMapper){
        this.placeMapper = placeMapper;
        this.roadMapper = roadMapper;
    }

    @Override
    public List<Place> Dijkstra(int startId, int endId) {
        //传入的起点id或终点id不存在，返回null
        if(placeMapper.getPlaceById(startId) == null || placeMapper.getPlaceById(endId) == null){
            return null;
        }

        var places = placeMapper.getPlaces();
        HashMap<Integer, Integer> idToIndex = new HashMap<>(); //将数据库中地点的id映射到0 ~ N-1上
        int i = 0;
        for(i = 0; i < places.size(); i++){
            idToIndex.put(places.get(i).getId(), i);
        }
        int source = idToIndex.get(startId);        //起点的索引
        int destination = idToIndex.get(endId);     //终点的索引

        int N = places.size();
        int [][] matrix = new int [N][N];             //邻接矩阵
        int [] visited = new int [N];                 //用于标记当前节点的最短路径是否已经
        StringBuilder [] path = new StringBuilder[N]; //用于保存路径

        //初始化路径
        for(i = 0; i < places.size(); i++){
            path[i] = new StringBuilder(String.valueOf(source));
            path[i].append("-").append(i);
        }
        visited[source] = 1;

        //初始化邻接矩阵
        int maxValue = 100000;         //为避免加法溢出，定义为一个较大的值即可
        for(i = 0; i < N; i++){
            Arrays.fill(matrix[i], maxValue);
        }

        //读取所有道路，修改邻接矩阵
        var roads = roadMapper.getRoads();
        for(Road road : roads){
            int r = idToIndex.get(road.getStartPlaceId());
            int c = idToIndex.get(road.getEndPlaceId());
            matrix[r][c] = road.getLength();
        }

        //如果还没走到终点，就每次循环选一个距离最近的点
        for(i = 1; i < matrix.length && visited[destination] == 0; i++)
        {
            int min = Integer.MAX_VALUE;
            int index = -1;

            for(int j = 0; j < matrix.length; j++){
                if(visited[j] == 0 && matrix[source][j] < min)
                {
                    min = matrix[source][j];
                    index = j;
                }
            }

            visited[index] = 1;

            //更新路径
            for(int m = 0; m < matrix.length; m++)
            {
                if(visited[m] == 0 && matrix[source][index] + matrix[index][m] < matrix[source][m])
                {
                    matrix[source][m] = matrix[source][index] + matrix[index][m];
                    path[m] = path[index].append("-").append(m);
                }
            }
        }

        /*
          由于路径是字符串
          所以要对其进行解析
         */
        List <Place> list = new ArrayList<>();
        String str = String.valueOf(path[destination]);
        String [] array = str.split("-");
        for(i = 0; i < array.length; i++){
            list.add(placeMapper.getPlaceById(places.get(Integer.parseInt(array[i])).getId()));
        }

        return list;
    }
}
