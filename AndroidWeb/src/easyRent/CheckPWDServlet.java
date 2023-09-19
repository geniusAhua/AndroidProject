package easyRent;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

/**
 * Servlet implementation class CheckPWDServlet
 */
@WebServlet("/CheckPWDServlet")
public class CheckPWDServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckPWDServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("UTF-8");
		PrintWriter pw = response.getWriter();
		String originalPWD = request.getParameter("password");
		boolean result = false;
		HttpSession session = request.getSession();
		JSONObject json = new JSONObject();
		
		result = DBModel.checkPWD(originalPWD, session.getAttribute("ID").toString());
		System.out.println(session.getAttribute("ID").toString()+"checkPWD, pwd: "+originalPWD + ", result:" + result);
		if(result) {
			json.put("status", "success");
			json.put("msg", "√‹¬Î’˝»∑");
		}
		else {
			json.put("status", "failure");
			json.put("msg", "√‹¬Î¥ÌŒÛ");
		}
		pw.write(json.toString());
	}

}
