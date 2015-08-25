package consistentHash;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * ConsistentHash algorithm
 * @author buptmiao
 *
 * @param <Node>  节点类型，类似ip，主机名等
 */
public class HashRing<Node> {
	
    private final SortedMap<Long, Node> Ring;   
    private final int Replicas; 
    private final Hash hash;
    
    /**
     * Constructor
     * @param rep    虚拟节点数
     * @param nodes  输入节点列表
     * @param hash   hash算法，例如MD5等，如果不提供此参数默认使用MurMurHash.
     */
    public HashRing(int rep,Collection<Node> nodes,Hash hash) {
    	this.Replicas = rep;
        this.hash = hash;  
        Ring = new TreeMap<Long,Node>();
        if(nodes != null){
	        for(Node n : nodes){
	        	this.add(n);
	        }
        }
    }  
    /**
     * Constructor  使用MurMurHash算法
     * @param rep   虚拟节点数
     * @param nodes 输入节点列表
     */
    public HashRing(int rep,Collection<Node> nodes){
    	this(rep, nodes, new MurMurHash());
    }
    /**
     * Constructor 使用MurMurHash算法，不提供输入节点
     * @param rep  虚拟节点数
     */
    public HashRing(int rep){
    	this(rep, null);
    }
    /**
     * Constructor 使用MurMurHash算法，虚拟节点书为100，不提供输入节点
     */
    public HashRing(){
    	this(100);
    }
    
    /**
     * 添加节点n，添加 Replicas 个虚拟节点
     * @param n 节点名称
     */
    public void add(Node n){
    	for(int i = 0; i < Replicas; i++){
    		Ring.put(hash.hash(n.toString() + i), n);
    	}
    }
    /**
     * 删除节点n，删除 Replicas 个虚拟节点
     * @param n 节点名称
     */
    public void remove(Node n){
    	for(int i = 0; i < Replicas; i++){
    		Ring.remove(hash.hash(n.toString() + i));
    	}
    }
    /**
     * 获得key顺时针方向的第一个节点，如果Hash环为空返回 null，如果没有找到节点，返回第一个节点
     * @param key 给定键值
     * @return 返回节点对象，如果Hash环为空返回 null
     */
    public Node get(String key){
    	
    	if(Ring.isEmpty()){
    		return null;
    	}
    	//得到该键值之后的部分Map
    	SortedMap<Long, Node> tail = Ring.tailMap(hash.hash(key)); 
    	if(tail.size() == 0){
    		//没有找到节点，返回第一个节点
    		return Ring.get(Ring.firstKey());
    	}
    	return tail.get(tail.firstKey());
    }
}

