package consistentHash;

import java.util.LinkedList;
import java.util.List;

public class ConsistentHashTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		class Nod{
			String name;
			public Nod(String name){
				this.name = name;
			}
			public String toString(){
				return name;
			}
		}
		
		List<Nod> nodes = new LinkedList<Nod>();
		nodes.add(new Nod("miao"));
		nodes.add(new Nod("bupt"));
		
		HashRing<Nod> h = new HashRing<Nod>(100,nodes);
		for(int i=0;i<2;i++){
			System.out.println(h.get("1b3"));
			System.out.println(h.get("1d3"));
			System.out.println(h.get("1d4"));
			System.out.println(h.get("1d5"));
			System.out.println(h.get("1d6"));
			System.out.println(h.get("2d3"));
			
			h.add(new Nod("hello,world"));
			System.out.println();
		}
	}

}

