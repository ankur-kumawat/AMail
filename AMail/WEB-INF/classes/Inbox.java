import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Inbox extends HttpServlet
{
	public void doPost(HttpServletRequest req,HttpServletResponse res)throws IOException
	{
		PrintWriter out=res.getWriter();
		out.println("Inbox");
	}
}