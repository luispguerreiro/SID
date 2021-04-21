import java.sql.Connection;

public class SqlDispatcher implements Runnable {
	
	Connection connect;

	
	public SqlDispatcher(Connection connect) {
		this.connect=connect;
		
	}

	public void getParametros() {
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
