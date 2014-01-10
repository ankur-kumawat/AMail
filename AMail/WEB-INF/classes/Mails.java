import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
public class Mails extends HttpServlet
{
	Connection con;
	Statement stmt;
	PrintWriter out;
	String uname;
	String upass;
	String table="inbox";
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
	public void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException
	{
		res.setContentType("text/html");
		out=res.getWriter();
		HttpSession session=req.getSession();
		uname=(String)session.getAttribute("uname");
		upass=(String)session.getAttribute("upass");
		//out.println("Inbox");
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
			String sending=req.getParameter("send");
			if(sending!=null)
			{
				if(sending.equals("Send"))
				{
					String rec=req.getParameter("receiver");
					if(rec.equals(""))
					{
						ServletContext ctx=getServletContext();
						RequestDispatcher rd=ctx.getRequestDispatcher("/Mails?mails=New");
						rd.forward(req,res);
					}
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
					rs=stmt.executeQuery("select * from users where uid='"+rec+"'");
					if(!rs.next())
					{
						stmt.execute("insert into inbox values('Advanced Mail','"+uname+"','Sent Failed',"+idg+",'Requested User Does not exist,<br>Please Check the receipents id')");
					}
					else
					{
						String sub=req.getParameter("sub");
						String msg=req.getParameter("msg");
						stmt.execute("insert into inbox values('"+uname+"','"+rec+"','"+sub+"',"+idg+",'"+msg+"')");
						stmt.execute("insert into sent values('"+uname+"','"+rec+"','"+sub+"',"+idg+",'"+msg+"')");
					}
					out.println("<div>Your Mail Have Been Sent</div>");
				}
				if(sending.equals("Save As Draft"))
				{
					String rec=req.getParameter("receiver");
					String sub=req.getParameter("sub");
					String msg=req.getParameter("msg");
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
					stmt.execute("insert into drafts values('"+uname+"','"+rec+"','"+sub+"',"+idg+",'"+msg+"')");
					out.println("<div>Your Mail Have Been Saved</div>");
				}
				out.println("<div><a href='Mails?mails=Inbox'><button>Go Back to your Inbox</button></a></div>");
			}
			String param1=req.getParameter("mails");
			if(param1!=null)
			{
				comman();
				out.println("<div style=\"height:80%; width:100%\">");
				if(param1.equals("Inbox"))
				{
					table="inbox";
					session.setAttribute("table",table);
					out.println("<table height='100%' width='100%' border='1'>");
					out.println("<tr style=\"height:30px\"><th width='20%'>Sender</th><th>Subject</th></tr>");
					rs=stmt.executeQuery("select * from inbox where receiver='"+uname+"'");
					while(rs.next())
					{
						String id="mail="+rs.getString("id");
						out.println("<tr style=\"height:30px\"><td width='20%'><a href=\"Mails?"+id+"&mails=Inbox\">"+rs.getString("sender")+"</a></td><td><a href=\"Mails?"+id+"&mails=Inbox\">"+rs.getString("subject")+"</a></td></tr></a>");
					}
					out.println("<tr style=\"height:100%\"></tr>");
					out.println("</table>");
				}
				if(param1.equals("New"))
				{
					String err=req.getParameter("error");
					newMail("","","",err);
				}
				if(param1.equals("Sent Items"))
				{
					table="sent";
					session.setAttribute("table",table);
					out.println("<table height='100%' width='100%' border='1'>");
					out.println("<tr style=\"height:30px\"><th width='20%'>Receiver</th><th>Subject</th></tr>");
					rs=stmt.executeQuery("select * from sent where sender='"+uname+"'");
					while(rs.next())
					{
						String id="mail="+rs.getString("id");
						out.println("<tr style=\"height:30px\"><td width='20%'><a href=\"Mails?"+id+"&mails=Sent Items\">"+rs.getString("receiver")+"</a></td><td><a href=\"Mails?"+id+"&mails=Sent Items\">"+rs.getString("subject")+"</a></td></tr></a>");
					}
					out.println("<tr style=\"height:100%\"></tr>");
					out.println("</table>");
				}
				if(param1.equals("Drafts"))
				{
					table="drafts";
					session.setAttribute("table",table);
					out.println("<table height='100%' width='100%' border='1'>");
					out.println("<tr style=\"height:30px\"><th width='20%'>Receiver</th><th>Subject</th></tr>");
					rs=stmt.executeQuery("select * from drafts where sender='"+uname+"'");
					while(rs.next())
					{
						String id="mail="+rs.getString("id");
						out.println("<tr style=\"height:30px\"><td width='20%'><a href=\"Mails?"+id+"&mails=Drafts\">"+rs.getString("receiver")+"</a></td><td><a href=\"Mails?"+id+"&mails=Drafts\">"+rs.getString("subject")+"</a></td></tr></a>");
					}
					out.println("<tr style=\"height:100%\"></tr>");
					out.println("</table>");
				}
				out.println("</div>");
			}
			String param2=req.getParameter("mail");
			if(param2!=null)
			{
				try
				{
					/*comman();
					out.println("</div>");
					*/
					String n="receiver";
					rs=stmt.executeQuery("select * from "+table+" where id="+param2);
					if(param1.equals("Inbox"))
						n="sender";
					else
						if(param1.equals("Sent Items"))
							n="receiver";
						else
					session.setAttribute("fd",param2);
					if(rs.next())
					{
						out.println("<div style=\"height:600px; width:100%;\"><center>");
						out.println("<table border='2' height='100%' width='70%'><tr height='30px'><td width='20%'>"+n+"&nbsp;&nbsp;&nbsp;:</td><td>");
						out.println(rs.getString(n)+"</td></tr>");
						out.println("<tr height='30px'><td width='20%'>Subject&nbsp;&nbsp;&nbsp;:</td><td>");
						out.println(rs.getString("subject")+"</td></tr>");
						out.println("<tr height='90%'><td width='20%'>Message&nbsp;&nbsp;&nbsp;:</td><td>");
						out.println(rs.getString("msg")+"</td></tr></table></center>");
						out.println("<div height='100%'><form action='Mails'>");
						if(table.equals("drafts"))
							out.println("<input type='submit' name='fd' value='Edit'>");
						else
							out.println("<input type='submit' name='fd' value='Forward'>");
						out.println("<input type='submit' name='fd' value='Delete'></form></div>");
						out.println("</div>");
					}
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
				
			}
			String param3=req.getParameter("fd");
			if(param3!=null)
			{
				comman();
				out.println("</div>");
				
				table=(String)session.getAttribute("table");
				if(param3.equals("Delete"))
				{
					String id=(String)session.getAttribute("fd");
					session.removeAttribute("fd");
					stmt.execute("delete * from "+table+" where id="+id);
					out.println("<div> Mail Has Been Deleted</div>");
					out.println("<a href=\"Mails?mails=Inbox\"><button>Back</button></a>");
				}
				if(param3.equals("Forward")||param3.equals("Edit"))
				{
					String id=(String)session.getAttribute("fd");
					session.removeAttribute("fd");
					rs=stmt.executeQuery("select * from "+table+" where id="+id);
					String sub="",msg="";
					if(rs.next())
					{
						sub=rs.getString("subject");
						msg=rs.getString("msg");
					}
					newMail("",sub,msg,null);
				}
			}
			out.println("</body></html>");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.getMessage();
		}
	}
	public void newMail(String receiver, String sub, String msg, String err)
	{
		out.println("<div style=\"height:100%; width:100%; font-size:14px\">");
		if(err!=null)
			out.println("<div>"+err+"</div>");
		out.println("<form action=Mails><div width='100%' height='10px'>");
		out.println("To : <input type='text' name='receiver' width='100%' value='"+receiver+"'></div>");
		out.println("<div width='100%' height='10px'>");
		out.println("Subject : <input type='text' name='sub' width='100%' value='"+sub+"'></div>");
		out.println("<div width='100%' height='400px'>");
		out.println("Message : <textarea name='msg' width='100%'>"+msg+"</textarea></div>");
		out.println("<div width='100%' height='10px'>");
		out.println("<input type='submit' name='send' value='Send'><input type='reset' value='Reset'>");
		if(msg.equals("")&&sub.equals(""))
			out.println("<input type='submit' name='send' value='Save As Draft'>");
		out.println("/<div></form></div>");
	}
	public void comman()
	{
		out.println("<div style=\"height:8%; width:100%\">Welcome "+uname+"</div>");
		out.println("<div style=\"height:8%; width:100%\">");
		out.println("<form method='get' action='Mails'>");
		out.println("<input type='submit' name='mails' value='New'>");
		out.println("<input type='submit' name='mails' value='Inbox'>");
		out.println("<input type='submit' name='mails' value='Sent Items'>");
		out.println("<input type='submit' name='mails' value='Drafts'>");
		out.println("</form></div>");
	}
}