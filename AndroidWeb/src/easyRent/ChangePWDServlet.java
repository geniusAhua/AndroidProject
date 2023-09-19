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
 * Servlet implementation class ChangePWDServlet
 */
@WebServlet("/ChangePWDServlet")
public class ChangePWDServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChangePWDServlet() {
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
		boolean result = false;
		JSONObject json = new JSONObject();
		HttpSession session = request.getSession();
		//��ȡ������
		String newPWD = request.getParameter("newPassword");
		//����������
		result = DBModel.changePWD(newPWD, session.getAttribute("ID").toString());
		System.out.println(session.getAttribute("ID").toString()+"change pwd to"+newPWD+" "+result);
		if(result) {
			json.put("status", "success");
			json.put("msg", "�޸ĳɹ�");
		}
		else {
			json.put("status", "failure");
			json.put("msg", "�������ݿ�ʧ��");
		}
		pw.write(json.toString());
	}

}
