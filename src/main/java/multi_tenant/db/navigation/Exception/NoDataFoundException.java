package multi_tenant.db.navigation.Exception;

public class NoDataFoundException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	// NoDataFoundException("No data found for this request");
	public NoDataFoundException (String message) {
		super(message);
	}
	
}
