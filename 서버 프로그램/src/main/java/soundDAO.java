import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class soundDAO {
	
	private DBConnectionInfo dbInfo;
	
	private Connection conn = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	
	public soundDAO(DBConnectionInfo dbInfo) {
		super();
		this.dbInfo = dbInfo;
	}
	
	protected void connect() throws SQLException, ClassNotFoundException
	{
		// load jdbc driver class
		Class.forName(dbInfo.getJdbcDriverName());
		
		// connect to DB server
		conn = DriverManager.getConnection(dbInfo.getUrl(), dbInfo.getUserid(), dbInfo.getPassword());
	}
	
	protected void disconnect() throws SQLException
	{
		// disconnect from DB server
		if(rs != null)
		{
			rs.close();
		}
		if(stmt != null)
		{
			stmt.close();
		}
		if(conn != null)
		{
			conn.close();
		}
	}
	
	public soundDO selectsound(String id, int chapter, int track)
	{
		soundDO sound = null;
		
		try {
			connect();
			
			if(conn != null)
			{
				stmt = conn.createStatement();
				
				String sql = String.format("select * from sound where book_id='%s' and chapter=%d and track=%d", id, chapter, track);
				rs = stmt.executeQuery(sql);
				
				if (rs.isBeforeFirst())
				{
					rs.next();
					
					sound = new soundDO();
					sound.setUrl(rs.getString("url"));
					sound.setFileName(rs.getString("filename"));
				}
			}
			disconnect();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sound;
	}

}
