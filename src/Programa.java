import java.io.IOException;


public class Programa {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Servidor server = new Servidor(2003);
		try {
			server.listen();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
