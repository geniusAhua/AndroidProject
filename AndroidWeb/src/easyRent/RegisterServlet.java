package easyRent;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.sun.corba.se.pept.transport.Connection;


/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet(name = "RegisterServlet", value = "/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String registerAccount = request.getParameter("registerAccount");
		String registerPassword = request.getParameter("registerPassword");
		JSONObject json = new JSONObject();
		
		if (!registerAccount.isEmpty() && !registerPassword.isEmpty()) {
			User RegisterUser = new User(registerAccount, registerPassword);
			json = new JSONObject(DBModel.register(RegisterUser));

			System.out.println("注册账号:" + registerAccount + ",注册密码:" + registerPassword + ",注册结果:" + json.get("status"));
		}
		else {
			json.put("status", "failure");
		}
		//通过PrintWriter返回给客户端操作结果
		PrintWriter writer = response.getWriter();
		writer.print(json.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
