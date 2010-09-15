package ymconnectionserver.exceptions;

public class NotOkResponseException extends Exception
{
	public NotOkResponseException()
	{
		super();
	}
	
	public NotOkResponseException(String msg)
	{
		super(msg);
	}
	
	public NotOkResponseException(Exception e)
	{
		super(e);
	}

}
