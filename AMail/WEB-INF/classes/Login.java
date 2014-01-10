import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Login extends HttpServlet
{
	public void doGet(HttpServletRequest req, HttpServletResponse res)throws IOException
	{
		res.setContentType("text/html");
		PrintWriter out=res.getWriter();
		out.println("<html><head><title>Advanced Mail - Login</title>");
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
		out.println("</head><body bgcolor='#CAD2FB'>");
		out.println("<div style=\"height:100%; width:100%\">");
		out.println("<div style=\"height:30px; width:100%; text-align:center; font-size:16px; font-family:'Comic Sans MS', cursive; color:#6699FF\">");
		out.println("Enter Login Id and Password to Continue</div>");
		String msg=req.getParameter("errMsg");
		if(msg!=null)
		{
			out.println("<div style=\"height:30px; width:100%; text-align:center; font-size:16px; font-family:'Comic Sans MS', cursive; color:#6699FF\">");
			out.println(msg+"</div>");
		}
		out.println("<form action='Home' method='post'>");
		out.println("<div class=\"fnt\">"+
            "<div class=\"right\">User Name : </div>"+
            "<div class=\"left\"><input type=\"text\" name=\"uname\"/></div>"+
        "</div>");
		out.println("<div class=\"fnt\">"+
                "<div class=\"right\">Password : </div>"+
	            "<div class=\"left\"><input type=\"password\" name=\"upass\"/></div>"+
        "</div>");
		out.println("<div class=\"fnt\">"+
                "<div class=\"right\"><input type=\"submit\" value=\"Login\"/></div>"+
	            "<div class=\"left\"><input type=\"reset\" value=\"Cancel\"/></div>"+
        "</div>");
		out.println("</form></div></body></html>");
	}
}