package easyRent;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.*;

/**
 * Servlet implementation class AndroidServlet
 */
@WebServlet(name = "LoginServlet", value = "/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String loginAccount = request.getParameter("loginAccount");
		String loginPassword = request.getParameter("loginPassword");
		boolean result = false;
		HttpSession session = request.getSession();
		JSONObject json = new JSONObject();
		
		if(session.isNew()) {
			System.out.println("Session is new. The sessionID is" + session.getId());
		}
		else
			System.out.println("Session has already been created");
		
		if (!loginAccount.isEmpty() && !loginPassword.isEmpty()) {
			User user = new User(loginAccount, loginPassword);
			result = DBModel.login(user);

			System.out.println("登录账号:" + loginAccount + ",登录密码:" + loginPassword + ",登录结果:" + result);
		}
		
		if(result) {
			json = new JSONObject(DBModel.getInfo(loginAccount, 1));
			json.put("status", "success");
			session.setAttribute("ID", json.get("id").toString());
			session.setAttribute("NAME", json.get("name").toString());
			session.setAttribute("TYPE", (int)json.get("type"));
			session.setAttribute("COLLEGE", json.get("college").toString());
			session.setAttribute("DEPARTMENT", json.get("department").toString());
			session.setAttribute("CLASS", json.get("class").toString());
			session.setAttribute("SEX", json.get("sex").toString());
		}
		else json.put("status", "failure");

		response.setCharacterEncoding("UTF-8");
		//通过PrintWriter返回给客户端操作结果
		PrintWriter writer = response.getWriter();
		writer.print(json.toString());
		System.out.println("用户信息" + json.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
