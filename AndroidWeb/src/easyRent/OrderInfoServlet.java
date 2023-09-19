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

import com.google.gson.JsonObject;
import com.sun.corba.se.spi.activation.Repository;


/**
 * Servlet implementation class OrderInfoServlet
 */
@WebServlet("/OrderInfoServlet")
public class OrderInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OrderInfoServlet() {
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
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter pw = response.getWriter();

		if (request.getParameter("type").equals("insert")) {//增
			String userId = request.getParameter("userId");
			String classRoom = request.getParameter("classRoom");
			String orderDate = request.getParameter("orderDate");
			String reason = request.getParameter("orderReason");
			String choices = request.getParameter("orderChoices");

			System.out.println(userId + " " + classRoom + " " + reason);

			boolean result = false;

			result = DBModel.InsertOrderInfo(userId, classRoom, orderDate, choices, reason);
			System.out.println("插入申请数据" + result + "  " + userId);
			if (result)
				pw.write("true");
			else
				pw.write("false");
		}
		else if(request.getParameter("type").equals("update")) {//改
			int orderId = Integer.parseInt(request.getParameter("orderId"));
			int orderStatus = Integer.parseInt(request.getParameter("orderStatus"));
			boolean uResult = DBModel.updateOrderInfo(orderId, orderStatus);
			
			System.out.println("修改申请数据：" + uResult);
			if(uResult) {
				pw.write("true");
			}
			else {
				pw.write("false");
			}
		}
		else if(request.getParameter("type").equals("classroomChoices")) {
			String classroom = request.getParameter("classroom");
			String date = request.getParameter("date");
			
			JSONObject json = new JSONObject(DBModel.queryClassroomChoices(classroom, date));
			pw.write(json.toString());
		}
		else if(request.getParameter("type").equals("byClassroom")) {
			String classroom = request.getParameter("classroom");
			JSONObject json = new JSONObject(DBModel.queryOrderInfoByClassroom(classroom));
			System.out.println("根据教室查申请:" + classroom);
			pw.write(json.toString());
		}
		else {
			HttpSession session = request.getSession();
			int type = Integer.parseInt(request.getParameter("type"));
			
			System.out.println("userType:" + type + " session中的:" + session.getAttribute("TYPE"));
			
			JSONObject json = new JSONObject(
					DBModel.queryOrderInfo(
							new OrderUserInfo(type,
									session.getAttribute("NAME").toString(),
									session.getAttribute("ID").toString())));
			System.out.println("查申请：" + json.toString());
			
			pw.write(json.toString());
		}
	}

}
