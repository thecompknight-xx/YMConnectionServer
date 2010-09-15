package ymconnectionserver.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.util.ServletContextAware;

import com.opensymphony.xwork2.ActionSupport;

public abstract class AbstractActionBase extends ActionSupport implements SessionAware, ServletRequestAware, ServletResponseAware, ServletContextAware
{
	protected static String OAUTH_CONSUMER_KEY="dj0yJmk9RWsxQjk4cDBickdxJmQ9WVdrOVlXMW1lRE5ETjJNbWNHbzlOamt5TkRJME9EWXkmcz1jb25zdW1lcnNlY3JldCZ4PTJj";
	protected static String OAUTH_CONSUMER_SECRET="de75359591a2481eca60dd934ce28bdb80f8404a";
	

	private Map<String, Object> session;
	private HttpServletRequest request;	
	private HttpServletResponse response;
	private ServletContext servletContext;
			
	@Override
	public void setSession(Map<String, Object> session)
	{
		this.session = session;
	}
	
	public Map<String, Object> getSession()
	{
		return session;
	}

	@Override
	public void setServletRequest(HttpServletRequest request)
	{
		this.request = request;
	}

	public HttpServletRequest getServletRequest()
	{
		return request;
	}
	
	@Override
	public void setServletResponse(HttpServletResponse response)
	{
		this.response = response;
	}

	public HttpServletResponse getServletResponse()
	{
		return response;
	}

	@Override
	public void setServletContext(ServletContext servletContext)
	{
		this.servletContext = servletContext;
	}
	
	public ServletContext getServletContext()
	{
		return servletContext;
	}
	
	protected String getStringFromStream(InputStream stream) throws IOException
	{
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String line;
        String str="";
        while ((line = br.readLine()) != null) {
        	str = str+line;
        }
        
        br.close();
        
        return str;

	}
	
	protected Map<String,String> getMapOfKeyValuePairs(String str)
	{
		Map<String, String> map = new HashMap<String, String>();
		
        String[] keyValuePairsArr = str.split("&");
        
        for(String keyValuePair : keyValuePairsArr)
        {        	
        	String[] keyValuePairArr = keyValuePair.split("=");
        	map.put(keyValuePairArr[0],keyValuePairArr[1]);
        }
        
        return map;
		
	}

	protected void persistKeyValuePairsInCookie(Map<String,String> map)
	{        
        for(Entry<String, String> entry : map.entrySet())
        {        	        	
        	getServletResponse().addCookie(new Cookie(entry.getKey(),entry.getValue()));
        }
	}

	protected void persistKeyValuePairsInCookie(String str)
	{        
        String[] keyValuePairsArr = str.split("&");
        
        for(String keyValuePair : keyValuePairsArr)
        {        	
        	String[] keyValuePairArr = keyValuePair.split("=");
        	getServletResponse().addCookie(new Cookie(keyValuePairArr[0],keyValuePairArr[1]));
        }
	}	
}
