import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.io.*;

public class SignUp extends HttpServlet
{
	Connection con;
	Statement stmt;
	public void init()throws ServletException
	{
		try
		{
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			con=DriverManager.getConnection("Jdbc:Odbc:Advanced");
			stmt=con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	{
		try
		{
			res.setContentType("text/html");
			PrintWriter out=res.getWriter();
			out.println("<html><head>");
			out.println("<style>"+
			".fnt"+
			"{"+
				"height:30px; font-family:'Comic Sans MS', cursive; color:#6699FF;"+
			"}"+
			".right"+
			"{"+
				"width:49%; text-align:right; font-size:14px; float:left; margin-right:3px;"+
			"}"+
			".left"+
			"{"+
				"width:49%; text-align:left; float:left; margin-left:3px;"+
			"}"+
			"</style>");
			String msg=req.getParameter("errMsg");
			if(msg!=null)
			{
				out.println("<div style=\"height:30px; width:100%; text-align:center; font-size:16px; font-family:'Comic Sans MS', cursive; color:#6699FF\">");
				out.println(msg+"</div>");
			}
			String uname=req.getParameter("uname");
			String upass=req.getParameter("upass");
			String ucpass=req.getParameter("ucpass");
			String create=req.getParameter("creat");
			if(create!=null && create.equals("AccountCreation"))
			{
				out.println("<title>Advanced Mail - Account Created</title></head>");
				out.println("<body bgcolor='#CAD2FB'>");
				ServletContext ctx=getServletContext();
				RequestDispatcher rd=ctx.getRequestDispatcher("/Mails?mails=Inbox");
				rd.forward(req,res);
			}
			if(uname!=null)
			{
				out.println("<title>Advanced Mail - Account Created</title></head>");
				out.println("<body bgcolor='#CAD2FB'>");
				ResultSet rs=stmt.executeQuery("select * from users where uid='"+uname+"'");
				if(rs.next())
				{
					res.sendRedirect("SignUp?errMsg=This User Name is Already Taken");
				}
				if(!upass.equals(ucpass))
				{
					res.sendRedirect("SignUp?errMsg=Password and Confirm Password Didn't Match");
				}
				HttpSession session=req.getSession();
				stmt.execute("insert into users values('"+uname+"','"+upass+"')");
				rs=stmt.executeQuery("select id from mails");
				int idg=(int)(Math.random()*1000);
				while(rs.next())
				{
					int id=Integer.parseInt(rs.getString("id"));					
					if(id==idg)
					{
						idg=(int)(Math.random()*1000);
						rs.first();
					}
				}
				stmt.execute("insert into mails values('Advanced Mail','"+uname+"','Welcome',"+idg+",'Welcome to Advanced Mail.<br> Your Account Has Been Created.<br> Enjoy Using Advanced Mail')");
				session.setAttribute("uname",uname);
				session.setAttribute("upass",upass);
				out.println("<a href=\"SignUp?creat=AccountCreation\"><button>Go to Inbox</button></a>");
			}
			else
			{
				out.println("<title>Advanced Mail - New User Please Sign up</title></head>");
				out.println("<body bgcolor='#CAD2FB'>");
				out.println("<div style=\"height:100%; width:100%\">");
				out.println("<div style=\"height:30px; width:100%; text-align:center; font-size:16px; font-family:'Comic Sans MS', cursive; color:#6699FF\">");
				out.println("Enter Login Id and Password to Continue</div>");
				out.println("<form action='SignUp' method='get'>");
				out.println("<div class=\"fnt\">"+
					"<div class=\"right\">User Name : </div>"+
					"<div class=\"left\"><input type=\"text\" name=\"uname\"/></div>"+
				"</div>");
				out.println("<div class=\"fnt\">"+
						"<div class=\"right\">Password : </div>"+
						"<div class=\"left\"><input type=\"password\" name=\"upass\"/></div>"+
				"</div>");
				out.println("<div class=\"fnt\">"+
						"<div class=\"right\">Confirm Password : </div>"+
						"<div class=\"left\"><input type=\"password\" name=\"ucpass\"/></div>"+
				"</div>");
				out.println("<div class=\"fnt\">"+
						"<div class=\"right\"><input type=\"submit\" value=\"Sign Up\"/></div>"+
						"<div class=\"left\"><input type=\"reset\" value=\"Cancel\"/></div>"+
				"</div>");
			}
			out.println("</form></div></body></html>");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}