package top.rrricardo.postcalendarbackend.utils;

import top.rrricardo.postcalendarbackend.models.TimePointEvent;
import top.rrricardo.postcalendarbackend.utils.generic.CustomHashTable;
import top.rrricardo.postcalendarbackend.utils.generic.CustomList;

import java.util.Map;

public class DictionaryTree {

    public TreeNode root;

    public DictionaryTree(){
        this.root = new TreeNode();
    }


    static class TreeNode{
        String value;   //当前节点的内容（只允许存一个字）
        CustomHashTable<String, TreeNode> Children;//子节点：下一个字作为key，下一个字对应的节点作为value
        boolean isEnd;  //标记是否是某个存入的String的最终字符
        CustomList<TimePointEvent> events;//如果isEnd为true，则该节点存入一个时间点事件

        //给根节点root用的构造方法
        public TreeNode(){
            this.Children = new CustomHashTable<>();
            this.isEnd = false;
        }

        public TreeNode(String value){
            this.value = value;
            this.Children = new CustomHashTable<>();
            this.isEnd = false;
            this.events = new CustomList<>();
        }

    }


    /**
     * 往字典树中插入一个事件
     * @param root 字典树的根节点
     * @param timePointEvent 待插入的事件
     * @return 插入结果，如果插入成功，返回则true,失败（事件已存在）则返回false
     */
    public static boolean insert(TreeNode root, TimePointEvent timePointEvent){
        String str = timePointEvent.getName();
        //将字符串分割成单个的字
        String [] array = str.split("");

        //遍历字典树，找到前缀匹配结束的位置
        TreeNode currentNode = root;
        CustomHashTable<String, TreeNode> map = root.Children;
        int i;
        for(i = 0; i < array.length; i++){
            if(map.get(array[i]) != null){
                currentNode = map.get(array[i]);
                map = currentNode.Children;
            }
            else {
                break;
            }
        }

        //字典树里有以这个事件名为前缀的字符串
        if(i == array.length){
            //存在一个同名事件
            if(currentNode.isEnd){
                //如果已经存在这个事件
                if(currentNode.events.contains(timePointEvent)){
                    return false;
                }
                //如果不存在这个事件（仅仅只是同名）
                else {
                    currentNode.events.add(timePointEvent);
                    return true;
                }
            }
            //当前节点没有任何事件
            else {
                currentNode.events.add(timePointEvent);
                currentNode.isEnd = true;   //添加标记
                return true;
            }

        }


        //如果只匹配了一部分，则将剩下的部分插入字典树中
        for(; i < array.length; i++){
            TreeNode newNode = new TreeNode(array[i]);
            currentNode.Children.put(array[i], newNode);
            currentNode = newNode;
        }

        //在终结点存入事件
        currentNode.events.add(timePointEvent);
        //添加标记
        currentNode.isEnd = true;

        return true;

    }


    /**
     * 查询以传入字符串为前缀的所有事件
     * @param root 字典树的根节点
     * @param str 输入的字符串(以此为前缀进行搜索)
     * @return 搜索结果的列表（没有搜到会返回一个空列表）
     */
    public static CustomList<TimePointEvent> search(TreeNode root, String str){
        String [] array = str.split("");

        //遍历字典树
        TreeNode currentNode = root;
        CustomHashTable<String, TreeNode> map = root.Children;
        int i;
        for(i = 0; i < array.length; i++){
            if(map.get(array[i]) != null){
                currentNode = map.get(array[i]);
                map = currentNode.Children;
            }
            //没有查到结果，返回空列表
            else {
                return new CustomList<>();
            }
        }

        CustomList<TimePointEvent> list0 = new CustomList<>();
        if(currentNode.isEnd){
            //把当前节点所有事件加入list0
            for(var event: currentNode.events){
                list0.add(event);
            }
        }

        //深度优先搜索(先序遍历)
        class DFS{
            static final CustomList<TimePointEvent> list = new CustomList<>();
            public static void dfs(TreeNode root){

                for (Map.Entry<String, TreeNode> tempNode : root.Children) {
                    if (tempNode.getValue().isEnd) {
                        for(var event: tempNode.getValue().events){
                            list.add(event);
                        }
                    }
                    //遍历以当前节点为root的子树
                    if(tempNode.getValue().Children.getSize() > 0){
                        dfs(tempNode.getValue());
                    }
                }

            }

        }

        DFS.dfs(currentNode);

        for(var event: DFS.list){
            list0.add(event);
        }

        //清空DFS类里的list(因为这个list是static的)
        int length = DFS.list.getSize();
        for(i = 0; i < length; i++){
            DFS.list.remove();
        }

        return list0;

    }


    /**
     * 从字典树中删除一个事件
     * @param root 字典树的根节点
     * @param timePointEvent 待删除的事件
     * @return 删除结果，成功则返回true,失败返回false
     */
    public static boolean remove(TreeNode root, TimePointEvent timePointEvent){
        String str = timePointEvent.getName();
        String [] array = str.split("");
        //在字典树里去查这个字符串
        int i, j = 0;
        TreeNode currentNode = root;
        TreeNode lastCrossNode = root;      //最后一个分叉节点
        CustomHashTable<String, TreeNode> map = root.Children;
        for(i = 0; i < array.length; i++){
            if(currentNode.Children.get(array[i]) != null){
                if(currentNode.Children.getSize() > 1){
                    lastCrossNode = currentNode;
                    j = i;
                }
                currentNode = map.get(array[i]);
                map = currentNode.Children;
            }
            else{
                break;
            }
        }

        //如果传入的字符串不存在或者不完整（只有前缀），删除失败
        if(i != array.length || !currentNode.isEnd){
            return false;
        }

        //找到那个事件，将它删除
        int max = currentNode.events.getSize();
        for(i = 0; i < max; i++){
            if(currentNode.events.get(i) == timePointEvent){
                currentNode.events.remove(i);
                break;
            }
        }
        //如果事件不存在，删除失败
        if(i == max){
            return false;
        }


        //如果删除的事件位于中间节点
        if(currentNode.Children.getSize() > 0){
            if(currentNode.events.getSize() == 0){
                currentNode.isEnd = false;
            }
            return true;
        }

        //如果删除的事件位于叶子节点
        //删除后，叶子节点还有事件，则无需再处理，直接返回删除成功
        if(currentNode.events.getSize() > 0){
            return true;
        }

        //删除后，叶子节点没有事件了，则需要做“剪枝”处理
        int k = j - 1;
        TreeNode nodeToDelete = lastCrossNode;
        TreeNode tempNode = lastCrossNode;
        for(i = j; i < array.length - 1; i++){
            TreeNode node1 = tempNode.Children.get(array[i]);
            if(node1.isEnd){
                nodeToDelete = node1;
                k = i;
            }

            tempNode = node1;
        }


        nodeToDelete.Children.remove(array[k+1]);

        return true;
    }


}
