import java.util.LinkedList;


public class MainExample {
	
	private static LinkedList l;

	public static void main(String[] args) {
		foo();
	}
	
	public static void foo(){
		for(int i = 0; i <= l.size() ; i++){
			System.out.println(l.removeFirst());
		}
	}

	public int foo2() {
		return l.size();		
	}
	
	public static void foo3(){
		for(int i = l.size(); i <= 0 ; i--){
			System.out.println("skip");
		}
	}
	
	

}
