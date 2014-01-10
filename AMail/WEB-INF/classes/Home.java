import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

public class Home extends HttpServlet
{
	Connection con;
	Statement stmt;
	
	public void init()throws ServletException
	{
		try
		{
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			con=DriverManager.getConnection("Jdbc:Odbc:Advanced");
			stmt=con.createStatement();
	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void doPost(HttpServletRequest req, HttpServletResponse res)throws IOException,ServletException
	{
		res.setContentType("text/html");
		PrintWriter out=res.getWriter();
		String uname=req.getParameter("uname");
		String upass=req.getParameter("upass");
		HttpSession session=req.getSession();
		session.setAttribute("uname",uname);
		session.setAttribute("upass",upass);
		try
		{
			ResultSet rs=stmt.executeQuery("select * from users where uid='"+uname+"' and upass='"+upass+"'");
			if(!rs.next())
				res.sendRedirect("Login?errMsg=Wrong Login Id or Password please enter correct ID and Password");
			out.println("<html><head><title>Advanced Mail</title>");
			out.println("<style>"+
			".colour{color:#6699FF; background:#CAD2FB; font-size:30px ;text-align:center}"+
			"</style>");
			out.println("</head><body class='colour'>");
			out.println("<div style=\"height:8%; width:100%\">Welcome "+uname+"</div>");
			out.println("<div style=\"height:8%; width:100%\">");
			out.println("<form method='get' action='Mails'>");
			out.println("<input type='submit' name='mails' value='New'>");
			out.println("<input type='submit' name='mails' value='Inbox'>");
			out.println("<input type='submit' name='mails' value='Sent Items'>");
			out.println("<input type='submit' name='mails' value='Drafts'>");
			out.println("</form></div>");
			out.println("<div style=\"height:90%; width:100%\" id=switch></div>");
			out.println("</body></html>");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
//		out.println("HOME");
	}
}