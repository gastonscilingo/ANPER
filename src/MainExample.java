import java.util.LinkedList;


public class MainExample {

	public static void main(String[] args) {
		foo();
	}
	
	public static void foo(){
		LinkedList<String> l = null;
		for(int i = 0; i < l.size() ; i++){
			System.out.println(l.removeFirst());
		}
	}

}
