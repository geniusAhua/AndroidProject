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
 * Servlet implementation class ClassRoomServlet
 */
@WebServlet("/ClassRoomServlet")
public class ClassRoomServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ClassRoomServlet() {
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
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter pw = response.getWriter();
		String info = request.getParameter("info");
		JSONObject json = new JSONObject();
		String result;
		
		if (info.equals("all")) {
			result = DBModel.queryClassRoom();
			System.out.println("roomJson:" + result);
			if(!result.isEmpty()) {
				json.put("status", "success");
				json.put("data", result);
			}
			else {
				json.put("status", "failure");
			}
		}
		else {
			result = DBModel.queryClassRoom(info);
			System.out.println("roomJson:" + result);
			json.put("status", "success");
			json.put("data", result);
		}
		

		pw.write(json.toString());
	}

}
