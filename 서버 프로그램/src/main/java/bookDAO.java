import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class bookDAO {
	
	private DBConnectionInfo dbInfo;
	
	private Connection conn = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	
	public bookDAO(DBConnectionInfo dbInfo) {
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
	
	public List<Map<String, String>> selectAllBooks()
	{
		List<Map<String, String>> list = null;
		Map<String, String> map = null;
		
		try {
			connect();
			
			if(conn != null)
			{
				stmt = conn.createStatement();
				
				String sql = String.format("select * from book");
				rs = stmt.executeQuery(sql);
				
				list = new ArrayList<>();
				if (rs.isBeforeFirst())
				{
					while(rs.next())
					{
						map = new HashMap<>();
						map.put("id", rs.getString("id"));
						map.put("title", rs.getString("title"));
						map.put("writer", rs.getString("writer"));
						map.put("publisher", rs.getString("publisher"));
						map.put("publication_date", rs.getString("publication_date"));
						map.put("image", rs.getString("image"));
						list.add(map);
					}
				}
			}
			disconnect();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}
	
	public bookDO selectImage(String id)
	{
		bookDO image = null;
		
		try {
			connect();
			
			if(conn != null)
			{
				stmt = conn.createStatement();
				
				String sql = String.format("select * from book where id='%s'", id);
				rs = stmt.executeQuery(sql);
				
				if (rs.isBeforeFirst())
				{
					rs.next();
					
					image = new bookDO();
					image.setImage(rs.getString("image"));
				}
			}
			disconnect();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return image;
	}

}
