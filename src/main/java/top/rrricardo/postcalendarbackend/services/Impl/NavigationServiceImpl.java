package top.rrricardo.postcalendarbackend.services.Impl;

import org.springframework.stereotype.Service;
import top.rrricardo.postcalendarbackend.mappers.PlaceMapper;
import top.rrricardo.postcalendarbackend.mappers.RoadMapper;
import top.rrricardo.postcalendarbackend.models.Place;
import top.rrricardo.postcalendarbackend.models.Road;
import top.rrricardo.postcalendarbackend.services.NavigationService;
import top.rrricardo.postcalendarbackend.utils.generic.CustomList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.PriorityQueue;

@Service
public class NavigationServiceImpl implements NavigationService {

    PlaceMapper placeMapper;
    RoadMapper roadMapper;
    HashMap<Integer, Integer> map = new HashMap<>();
    float [][] matrix;  //邻接矩阵
    int MAX;            //节点数
    CustomList<Place> allPlaces; //所有地点
    CustomList <Road> allRoads; //所有道路

    public NavigationServiceImpl(PlaceMapper placeMapper, RoadMapper roadMapper) {
        this.placeMapper = placeMapper;
        this.roadMapper = roadMapper;
        getMatrix();
    }

    static class Node implements Comparable<Node>{
        int id;
        float distance;  //起点到这个点的距离

        public Node(int id, float distance) {
            this.id = id;
            this.distance = distance;
        }


        @Override
        public int compareTo(Node o) {
            if(this.distance > o.distance){
                return 1;
            }
            else if(this.distance < o.distance){
                return -1;
            }
            else
            {
                return 0;
            }
        }

    }

    //得到邻接矩阵（同时得到地点id到matrix数组下标索引的映射）
    public void getMatrix(){
        allPlaces = new CustomList<>(placeMapper.getPlaces());
        MAX = allPlaces.getSize();
        matrix = new float[MAX][MAX];

        //通过map将地点id映射为数组下标:0~MAX-1
        int i;
        for(i = 0; i < MAX; i++){
            map.put(allPlaces.get(i).getId(), i);
        }

        //初始化邻接矩阵
        for(i = 0; i < MAX; i++){
            Arrays.fill(matrix[i], 10000.0f);  //用一个较大的值来表示 ∞ （不可达）
        }

        //（更新）生成邻接矩阵
        allRoads = new CustomList<>(roadMapper.getRoads());
        for(var road: allRoads){
            int start = map.get(road.getStartPlaceId());
            int end = map.get(road.getEndPlaceId());
            float length = road.getLength();
            matrix[start][end] = length;
            matrix[end][start] = length;
        }
    }


    public CustomList<Place> findPathOneDestination(int Source, int Destination){
        boolean [] visited= new boolean [MAX]; //标记节点是否已找到最短路径
        Arrays.fill(visited, false);
        float [] distance = new float [MAX];  //节点到起点的距离
        StringBuilder [] path = new StringBuilder[MAX]; //保存最短路径
        PriorityQueue <Node> queue = new PriorityQueue<>();

        int start = map.get(Source);
        int end = map.get(Destination);
        distance[start] = 0.0f;

        //初始化路径
        int i;
        for(i = 0; i < MAX; i++){

            path[i] = new StringBuilder(String.valueOf(start));
            if(i == start)
            {
                continue;
            }
            path[i].append("-");
            path[i].append(i);
            distance[i] = matrix[start][i];
            if(distance[i] < 10000.0f)
            {
                queue.add(new Node(i, distance[i]));
            }
        }

        while(queue.size() != 0 && !visited[end]){
            Node node = queue.poll();

            if(visited[node.id])        //如果节点的最短路径已经找到，则跳至下一次循环
            {
                continue;
            }

            visited[node.id] = true;

            //更新路径
            for(int j = 0; j < MAX; j++){
                //还没走过的当前节点的邻居
                if(matrix[node.id][j] < 10000.0f && !visited[j]){
                    //更新节点信息
                    if(distance[node.id] + matrix[node.id][j] < distance[j]){
                        distance[j] = distance[node.id] + matrix[node.id][j];
                        path[j] = path[node.id].append("-").append(j);
                        queue.add(new Node(j, distance[j]));
                    }
                }
            }
        }


        //得到的路径是字符串形式，需要处理
        CustomList <Place> list = new CustomList<>();
        String str = String.valueOf(path[end]);
        String [] array = str.split("-");
        for(i = 0; i < array.length; i++){
            list.add(allPlaces.get(Integer.parseInt(array[i])));
        }


        return list;
    }


    //这部分还没写......
    public CustomList<Place> findPathManyDestination(int Source, CustomList<Integer> middlePoints){
        return null;
    }

    @Override
    public CustomList<Road> getRoadsByPlace(CustomList<Place> places) {
        CustomList <Road> roads = new CustomList<>();

        int i, j = 0;
        int startId = places.get(j).getId();
        int endId = places.get(j+1).getId();

        outLoop:
        while(true){

            //遍历所有道路，找到以这两点为端点的道路
            for(i = 0; i < allRoads.getSize(); i++){
                Road road1 = allRoads.get(i);
                if(road1.getStartPlaceId() == startId && road1.getEndPlaceId() == endId || road1.getStartPlaceId() == endId && road1.getEndPlaceId() == startId){
                    roads.add(road1);
                    break;
                }
            }

            try {
                places.get(j + 2);
            } catch (ArrayIndexOutOfBoundsException ignored) {
                break;
            }

            j++;
            startId = places.get(j).getId();
            endId = places.get(j+1).getId();
        }

        return roads;
    }

}


