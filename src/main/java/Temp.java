import java.util.*;


public class Temp {

	public static void main(String[] args) {
		User u = new User();
		
		List<String> a = new ArrayList<String>();
		u.setAl(a);
		
		List<String> b = new ArrayList<String>();
		b.add("1212");
		a = b;
		
		
		System.out.println(a);
//		u.setAl(b);
		System.out.println(u.getAl());
		
	}
	
}
