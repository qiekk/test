import java.util.*;


public class ListTest {

	public static final int NUM = 100000;
	
	public static void main(String[] args) {
		
		List<String> ll = new LinkedList<String>();
		List<String> al = new ArrayList<String>();
		List<String> vl = new Vector<String>();
		
		// 追加数据
		long tS = System.currentTimeMillis();
		for(int i = 0; i < NUM; i ++) {
			ll.add("lalalalalala");
		}
		System.out.println("linked cost :" + (System.currentTimeMillis() - tS));

		long tS1 = System.currentTimeMillis();
		for(int i = 0; i < NUM; i ++) {
			al.add("lalalalalala");
		}
		System.out.println("arrayList cost :" + (System.currentTimeMillis() - tS1));
		
		long tS2 = System.currentTimeMillis();
		for(int i = 0; i < NUM; i ++) {
			vl.add("lalalalalala");
		}
		System.out.println("vector cost :" + (System.currentTimeMillis() - tS2));
		
		
		// 插入数据
		long tS4 = System.currentTimeMillis();
		for(int i = 0; i < NUM; i ++) {
			al.add(100, "llllll");
		}
		System.out.println("插入：arrayList cost :" + (System.currentTimeMillis() - tS4));
		
		
		long tS5 = System.currentTimeMillis();
		for(int i = 0; i < NUM; i ++) {
			vl.add(100, "llllll");
		}
		System.out.println("插入：vactor cost :" + (System.currentTimeMillis() - tS5));
		
		long tS3 = System.currentTimeMillis();
		for(int i = 0; i < NUM; i ++) {
			ll.add(100, "llllll");
		}
		System.out.println("插入：linked cost :" + (System.currentTimeMillis() - tS3));
		
		long tS6 = System.currentTimeMillis();
		for(String s : al) {
			continue;
		}
		System.out.println("遍历： array cost :" + (System.currentTimeMillis() - tS6));
		
		long tS7 = System.currentTimeMillis();
		for(String s : ll) {
			continue;
		}
		System.out.println("遍历： linked cost :" + (System.currentTimeMillis() - tS7));
	}
}
