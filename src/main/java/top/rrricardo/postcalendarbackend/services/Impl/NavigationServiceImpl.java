package top.rrricardo.postcalendarbackend.services.Impl;

import org.springframework.stereotype.Service;
import top.rrricardo.postcalendarbackend.mappers.PlaceMapper;
import top.rrricardo.postcalendarbackend.mappers.RoadMapper;
import top.rrricardo.postcalendarbackend.models.Place;
import top.rrricardo.postcalendarbackend.models.Road;
import top.rrricardo.postcalendarbackend.services.NavigationService;
import top.rrricardo.postcalendarbackend.utils.generic.CustomHashTable;
import top.rrricardo.postcalendarbackend.utils.generic.CustomList;
import top.rrricardo.postcalendarbackend.utils.generic.Heap;

import java.util.Arrays;


@Service
public class NavigationServiceImpl implements NavigationService {

    PlaceMapper placeMapper;
    RoadMapper roadMapper;
    CustomHashTable<Integer, Integer> map = new CustomHashTable<>();
    float [][] matrix;  //邻接矩阵
    int MAX;            //节点数
    CustomList<Place> allPlaces; //所有地点
    CustomList <Road> allRoads; //所有道路

    private boolean mapUpdated = false;

    public NavigationServiceImpl(PlaceMapper placeMapper, RoadMapper roadMapper) {
        this.placeMapper = placeMapper;
        this.roadMapper = roadMapper;
        getMatrix();
    }



    /**
     * 得到邻接矩阵（同时得到地点id到matrix数组下标索引的映射）
     */
    private void getMatrix(){
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
        if (mapUpdated) {
            getMatrix();
            mapUpdated = false;
        }


        boolean [] visited= new boolean [MAX]; //标记节点是否已找到最短路径
        Arrays.fill(visited, false);
        float [] distance = new float [MAX];  //节点到起点的距离
        String[] path = new String[MAX]; //保存最短路径

        //辅助类
        class Node implements Comparable<Node>{
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

        Heap <Node> queue = new Heap<>();

        int start = map.get(Source);
        int end = map.get(Destination);
        distance[start] = 0.0f;

        //初始化路径
        int i;
        for(i = 0; i < MAX; i++){

            path[i] = start + "-" + i;
            if(i == start)
            {
                continue;
            }
            distance[i] = matrix[start][i];
            if(distance[i] < 10000.0f)
            {
                queue.add(new Node(i, distance[i]));
            }
        }

        while(queue.getSize() != 0 && !visited[end]){
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
                        path[j] = path[node.id] + "-" + j;
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


    @Override
    public CustomList<Place> findPathManyDestination(CustomList<Integer> middlePoints){
        //指定的多点映射到 0~n上
        CustomHashTable <Integer, Integer> map2 = new CustomHashTable<>();
        int j, i = 0;
        for(var value: middlePoints){
            map2.put(value, i);
            i++;
        }

        int N = middlePoints.getSize();
        float [][] matrix2 = new float[N][N];  //定义一个“小的”邻接矩阵，作为middlePoints之间的邻接矩阵
        //用嵌套哈希表map3将matrix2与对应路径关联起来(注意map3里面只存了上三角)
        CustomHashTable<Integer, CustomHashTable<Integer, CustomList<Place>>> map3 = new CustomHashTable<>();

        //调用findPathOneDestination找到任意两个middlePoints之间的最短路径
        for(i = 0; i < N - 1; i++){
            matrix2[i][i] = 0.0f;
            CustomHashTable<Integer, CustomList<Place>> temp1 = new CustomHashTable<>();
            for(j = i + 1; j < N; j++){
                CustomList<Place> temp = findPathOneDestination(middlePoints.get(i), middlePoints.get(j));
                temp1.put(j, temp);
                //计算temp中最短路径的长度
                float sum = 0;
                for(int k = 0; k + 1 < temp.getSize(); k++){
                    sum = sum + matrix[map.get(temp.get(k).getId())][map.get(temp.get(k+1).getId())];
                }
                matrix2[i][j] = sum;
                matrix2[j][i] = sum;
            }

            map3.put(i, temp1);
        }

        //辅助类（为了得到全排列）
        class fullArrange {
            static int N = 30;  //途径点的数量限定为不超过30
            static int n;
            static int[] pos = new int[N];
            static boolean[] flag = new boolean[N];

            static CustomList<CustomList<Integer>> list = new CustomList<>();

            public static void dfs(int u) {
                if (u == n) {
                    CustomList<Integer> list1 = new CustomList<>();
                    for (int i = 0; i < n; i++) {
                        list1.add(pos[i]);
                    }
                    list.add(list1);

                    return;
                }

                for (int j = 1; j <= n; j++) {
                    if (!flag[j]) {
                        pos[u] = j;
                        flag[j] = true;
                        dfs(u + 1);
                        flag[j] = false;
                    }
                }
            }
        }

        //获取1到n-1的全排列
        fullArrange.n = N - 1;
        fullArrange.dfs(0);
        CustomList<CustomList<Integer>> list2 = fullArrange.list;

        //按全排列遍历所有可能，找到距离最短的环路
        CustomList<Integer> minList = list2.get(0);
        float min = Float.MAX_VALUE;
        for(var list3: list2){
            float sum = matrix2[0][list3.get(0)];  //第一步必定是从起点开始走出去
            i = 0;
            while(i + 1 < list3.getSize()){
                sum = sum + matrix2[list3.get(i)][list3.get(i+1)];
                i++;

                if(sum > min){
                    break;
                }
            }


            sum = sum + matrix2[list3.get(i)][0];  //最后一步必定是回到起点

            //更新最短路径
            if(sum < min){
                minList = list3;
                min = sum;
            }
        }

        //根据得到的最短环路的索引的全排列，还原出整个路径
        CustomList<Place> reList = map3.get(0).get(minList.get(0));
        CustomList <Place> tempList = new CustomList<>();
        for(i = 0; i + 1 < minList.getSize(); i++){
            //因为map3只存了上三角，所以要分类讨论
            if(map3.get(minList.get(i)).get(minList.get(i+1)) == null){
                tempList = map3.get(minList.get(i+1)).get(minList.get(i));
                tempList.remove(tempList.getSize() - 1); //移除中间重复节点(最后一个)
                //将tempList“倒着”并入reList
                for(i = tempList.getSize() - 1; i >= 0; i--){
                    reList.add(tempList.get(i));
                }
            }
            else{

                tempList = map3.get(minList.get(i)).get(minList.get(i+1));
                tempList.remove(0); //移除中间重复节点(第一个)
                //将tempList直接并入reList
                for(var value: tempList){
                    reList.add(value);
                }
            }



        }

        //最后要回到起点
        tempList = map3.get(0).get(minList.get(i));
        tempList.remove(tempList.getSize() - 1);
        for(i = tempList.getSize() - 1; i >= 0; i--){
            reList.add(tempList.get(i));
        }

        return reList;

    }



    @Override
    public CustomList<Road> getRoadsByPlace(CustomList<Place> places) {
        CustomList <Road> roads = new CustomList<>();

        int i, j = 0;
        int startId = places.get(j).getId();
        int endId = places.get(j+1).getId();

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

    @Override
    public void setMapUpdated() {
        mapUpdated = true;
    }
}


