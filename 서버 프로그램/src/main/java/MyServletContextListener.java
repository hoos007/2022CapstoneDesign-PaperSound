
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class MyServletContextListener
 *
 */
@WebListener
public class MyServletContextListener implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public MyServletContextListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce)  { 
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce)  { 
    	
    	ServletContext context = sce.getServletContext();
    	
    	DBConnectionInfo dbInfo = new DBConnectionInfo();
    	dbInfo.setJdbcDriverName(context.getInitParameter("maria_jdbc_driver"));
    	dbInfo.setUrl(context.getInitParameter("db_url"));
    	dbInfo.setUserid(context.getInitParameter("db_userid"));
    	dbInfo.setPassword(context.getInitParameter("db_password"));
    	
    	context.setAttribute("db_info", dbInfo);
    }
	
}
