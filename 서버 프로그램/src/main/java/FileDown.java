import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/FileDown/*")
public class FileDown  extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4357653489057931185L;
	
	public FileDown() {
		
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		DBConnectionInfo dbInfo = (DBConnectionInfo)getServletContext().getAttribute("db_info");
		
		String pathInfo = request.getPathInfo();
		
		if(pathInfo != null && pathInfo.length() > 0)
		{
			if (pathInfo.equals("/sound"))
			{
				soundDAO dao = new soundDAO(dbInfo);
				String bookid = request.getParameter("bookid");
				int chapter = Integer.parseInt(request.getParameter("chapter"));
				int track = Integer.parseInt(request.getParameter("track"));
				
				soundDO sound =  new soundDO();
				
				sound = dao.selectsound(bookid, chapter, track);
				
				// 서버에 올라간 경로를 가져옴
		//		ServletContext context = getServletContext();
		//		String uploadFilePath = context.getRealPath("filefolder");
		//		String filePath = uploadFilePath + File.separator + fileName;
		//		
		//		System.out.println(" LOG [업로드된 파일 경로] :: " + uploadFilePath);
		//		System.out.println(" LOG [파일 전체 경로] :: " + filePath);
				
				String fileName = sound.getFileName();
				String filePath = sound.getUrl();
				
				byte[] b = new byte[4096];
				FileInputStream fileInputStream = new FileInputStream(filePath);
				
				String mimeType = getServletContext().getMimeType(filePath);
				if(mimeType == null) {
					mimeType = "application/octet-stream";
				}
				response.setContentType(mimeType);
				
		        // 파일명 UTF-8로 인코딩(한글일 경우를 대비)
		        String sEncoding = new String(fileName.getBytes("UTF-8"));
		        response.setHeader("Content-Disposition", "attachment; fileName= " + sEncoding);
		        
		        // 파일 쓰기 OutputStream
		        ServletOutputStream servletOutStream = response.getOutputStream();
		        
		        int read;
		        while((read = fileInputStream.read(b,0,b.length))!= -1){
		            servletOutStream.write(b,0,read);
		        }
		        
		        servletOutStream.flush();
		        servletOutStream.close();
		        fileInputStream.close();
			}
			if (pathInfo.equals("/image"))
			{
				bookDAO dao = new bookDAO(dbInfo);
				String bookid = request.getParameter("bookid");
				
				bookDO image =  new bookDO();
				
				image = dao.selectImage(bookid);
				
				// 서버에 올라간 경로를 가져옴
		//		ServletContext context = getServletContext();
		//		String uploadFilePath = context.getRealPath("filefolder");
		//		String filePath = uploadFilePath + File.separator + fileName;
		//		
		//		System.out.println(" LOG [업로드된 파일 경로] :: " + uploadFilePath);
		//		System.out.println(" LOG [파일 전체 경로] :: " + filePath);
				
				String filePath = image.getImage();
				
				byte[] b = new byte[4096];
				FileInputStream fileInputStream = new FileInputStream(filePath);
				
				String mimeType = getServletContext().getMimeType(filePath);
				if(mimeType == null) {
					mimeType = "application/octet-stream";
				}
				response.setContentType(mimeType);
		        
		        // 파일 쓰기 OutputStream
		        ServletOutputStream servletOutStream = response.getOutputStream();
		        
		        int read;
		        while((read = fileInputStream.read(b,0,b.length))!= -1){
		            servletOutStream.write(b,0,read);
		        }
		        
		        servletOutStream.flush();
		        servletOutStream.close();
		        fileInputStream.close();
			}
		}
		
		
	}
}