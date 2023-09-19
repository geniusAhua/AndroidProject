package easyRent;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.json.*;

import com.google.gson.Gson;

public class DBModel {
	private static String DBUNAME = "root";
	private static String DBUPWD = "";
	private static String DRIVER = "com.mysql.jdbc.Driver";
	//后面的编码必须要有，否则写入数据库出现乱码
	private static String URL = "jdbc:mysql://localhost:3306/android?useUnicode=true&characterEncoding=utf8";
	private static Connection con = null;
	//连接数据库
	private static void ConnectDB() {
		try {
			//加载驱动
			Class.forName(DRIVER);
			con = DriverManager.getConnection(URL, DBUNAME, DBUPWD);
			System.out.println("Succefull");
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("finally")
	public static String getInfo(String account, int type) {//0是管理员查询，无需获取头像；1是用户登录查询需要获取头像
		JSONObject json = new JSONObject();
		String jsonData = "";
		

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		// 连接数据库
		ConnectDB();
		
		try {
			//在学生信息表中查询用户信息
			if (account.equals("admin")) {
				// 生成json
				json.put("id", account);
				json.put("name", "admin");
				json.put("type", 0);
				json.put("college", "无");
				json.put("department", "无");
				json.put("class", "无");
				json.put("sex", "男");

				jsonData = json.toString();
				
			} else {// 虽然上面的if里返回了jsonData但是这里的else是必须的否则上面的返回无效
				pstmt = con.prepareStatement("select * from studentinfo where stuId = ?");
				pstmt.setString(1, account);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					// 生成json
					json.put("id", account);
					json.put("name", rs.getString("stuName"));
					json.put("type", 1);
					json.put("college", rs.getString("colleget"));
					json.put("department", rs.getString("department"));
					json.put("class", rs.getString("class"));
					json.put("sex", rs.getInt("sex") == 1 ? "男" : "女");
				}
			}
			
			if (type == 1) {
				// 在用户信息表中拿取头像信息
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				pstmt = con.prepareStatement("select headPic from userinfo where account = ?");
				pstmt.setString(1, account);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					// 获取头像
					json.put("headPath", rs.getString(1));
				}
			}
			// 返回json字符串
			jsonData = json.toString();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			return jsonData;
		}
	}
	
	@SuppressWarnings("finally")
	public static boolean login(User user) {
		String loginAccount = user.getLoginAccount();
		String loginPassword = user.getLoginPassword();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = 0;
		
		ConnectDB();
		
		try {
			pstmt = con.prepareStatement("select count(*)from userinfo where account=? and password=?");
			pstmt.setString(1, loginAccount);
			pstmt.setString(2, loginPassword);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				count = rs.getInt(1);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally {
			try {
				if(rs != null) {
					rs.close();
				}
				if(pstmt != null) {
					pstmt.close();
				}
				if(con != null) {
					con.close();
				}
			}catch(SQLException e) {
				e.printStackTrace();
			}
			
			if(count == 1) {
				return true;
			}else {
				return false;
			}
		}
	}
	
	@SuppressWarnings("finally")
	public static String register(User user) {
		//json用于提供注册信息
		JSONObject json = new JSONObject();
		String loginAccount = user.getLoginAccount();
		String loginPassword = user.getLoginPassword();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = 0;
		int isExist = 0;
		
		ConnectDB();
		
		try {
			pstmt = con.prepareStatement("select count(*) from studentinfo where stuId = ?");
			pstmt.setString(1, loginAccount);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				isExist = rs.getInt(1);
			}
			
			if(isExist > 0) {
				//先关闭准备第二次查询
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				//查询是否注册过
				pstmt = con.prepareStatement("select count(*) from userinfo where account = ?");
				pstmt.setString(1, loginAccount);
				rs = pstmt.executeQuery();
				if(rs.next() && rs.getInt(1) != 0) {
					json.put("status", "failure");
					json.put("msg", "你已经注册 ");
				}
				else {
					if (pstmt != null)
						pstmt.close();
					// 注册信息并提供默认头像
					pstmt = con.prepareStatement("insert into userinfo values (?, ?, 'timg.jpg')");
					pstmt.setString(1, loginAccount);
					pstmt.setString(2, loginPassword);

					count = pstmt.executeUpdate();
				}
			}
			else {
				json.put("status", "failure");
				json.put("msg", "你没有注册权限");
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally {
			try {
				if(rs != null) {
					rs.close();
				}
				if(pstmt != null) {
					pstmt.close();
				}
				if(con != null) {
					con.close();
				}
			}catch(SQLException e) {
				e.printStackTrace();
			}
			
			if(count == 1) {
				json.put("status", "success");
				json.put("msg", "注册成功");
				return json.toString();
			}else {
				return json.toString();
			}
		}
	}
	
	@SuppressWarnings("finally")
	public static boolean updateHeadPic(String newName, String accountID) {
		PreparedStatement pstmt = null;
		int count = 0;
		
		//连接数据库
		ConnectDB();
		
		try {
			pstmt = con.prepareStatement("UPDATE userinfo SET headPic = ? WHERE account = ?");
			pstmt.setString(1, newName);
			pstmt.setString(2, accountID);
			count = pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if(pstmt != null)
					pstmt.close();
				if(con != null)
					con.close();
			}catch (SQLException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			if(count > 0)
				return true;
			else
				return false;
		}
		
	}
	
	@SuppressWarnings("finally")
	public static boolean checkPWD(String pwd, String account) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = 0;
		//连接数据库
		ConnectDB();
		try {
			pstmt = con.prepareStatement("select count(*) from userinfo where account = ? and password = ?");
			pstmt.setString(1, account);
			pstmt.setString(2, pwd);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				if (rs != null)
					rs.close();
				if(pstmt != null)
					pstmt.close();
				if(con != null)
					con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(count == 1) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	@SuppressWarnings("finally")
	public static boolean changePWD(String newPwd, String account) {
		PreparedStatement pstmt = null;
		int count = 0;
		//连接数据库
		ConnectDB();
		try {
			pstmt = con.prepareStatement("UPDATE userinfo SET password = ? where account = ?");
			pstmt.setString(1, newPwd);
			pstmt.setString(2, account);
			
			count = pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				if(pstmt != null)
					pstmt.close();
				if(con != null)
					con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(count > 0) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	@SuppressWarnings("finally")
	public static String queryClassRoom() {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<ClassRoom> list = new ArrayList<>();
		//连接数据库
		ConnectDB();
		try {
			pstmt = con.prepareStatement("select * from classroominfo");
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				ClassRoom cr = new ClassRoom(rs.getString("pic"), rs.getString("classRoom"), rs.getString("seatNum"));
				list.add(cr);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if(con != null)
					con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JSONArray jsonA = new JSONArray(list);
			return jsonA.toString();
		}
	}
	
	@SuppressWarnings("finally")
	public static String queryClassRoom(String str) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<ClassRoom> list = new ArrayList<>();
		//连接数据库
		ConnectDB();
		try {
			pstmt = con.prepareStatement("select * from classroominfo where classRoom like ? or seatNum like ?");
			pstmt.setString(1, "%" + str + "%");
			pstmt.setString(2, "%" + str + "%");
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				ClassRoom cr = new ClassRoom(rs.getString("pic"), rs.getString("classRoom"), rs.getString("seatNum"));
				list.add(cr);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if(con != null)
					con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JSONArray jsonA = new JSONArray(list);
			return jsonA.toString();
		}
	}
	
	public static boolean InsertOrderInfo(String userId, String classRoom, String orderDate, String choices, String reason) {
		PreparedStatement pstmt = null;
		int count = 0;
		
		ConnectDB();
		
		try {
			pstmt = con.prepareStatement("insert into orderinfo values (DEFAULT, ?, ?, ?, ?, ?, NOW(), -1)");
			pstmt.setString(1, classRoom);
			pstmt.setString(2, userId);
			pstmt.setString(3, orderDate);
			pstmt.setString(4, reason);
			pstmt.setString(5, choices);
			count = pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if(con != null)
					con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(count == 1) {
			return true;
		}
		else
			return false;
	}
	
	/**
	 * 根据教室查询申请表
	 * @param classroom
	 * @return
	 */
	public static String queryOrderInfoByClassroom(String classroom) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<OrderMsg> list = new ArrayList<>();
		JSONObject json = new JSONObject();
		
		ConnectDB();
		
		try {
			pstmt = con.prepareStatement("select * from orderinfo where classRoom = ?");
			pstmt.setString(1, classroom);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				String dateDetail = "";
				String choices = rs.getString("choices");
				for (int i = 0; i < 4; i++) {
					if (choices.charAt(i) == '1') {
						switch (i) {
							case 0:
								dateDetail += "8:30-11:30";
								break;
							case 1:
								dateDetail += "11:30-14:30";
								break;
							case 2:
								dateDetail += "14:30-17:30";
								break;
							case 3:
								dateDetail += "17:30-20:30";
								break;
						}
						if(i != 3) {
							dateDetail += "\n";
						}
					}
				}
				Timestamp time = rs.getTimestamp("submitTime");
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd\n HH:mm");
				String submitTime = dateFormat.format(time);
				OrderMsg om = new OrderMsg(rs.getString("orderID"), rs.getString("classRoom"),
						rs.getString("stuID"), rs.getString("date"),
						rs.getString("reason"), dateDetail,
						submitTime, rs.getInt("status"));
				
				System.out.println(submitTime);
				list.add(om);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if(rs != null)
					rs.close();
				if(con != null)
					con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(list.size() != 0) {
			JSONArray jsonA = new JSONArray(list);
			json.put("data", jsonA.toString());
			json.put("info", "get");
		}
		else {
			json.put("info", "null");
		}
		return json.toString();
	}
	
	/**
	 * 根据用户类型查询申请表
	 * @param user
	 * @return
	 */
	public static String queryOrderInfo(OrderUserInfo user) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<OrderMsg> list = new ArrayList<>();
		JSONObject json = new JSONObject();
		
		ConnectDB();
		
		try {
			/**
			 * 根据用户类型判断如何查询
			 */
			if (user.type == 1) {
				pstmt = con.prepareStatement("select * from orderinfo where stuID = ?");
				pstmt.setString(1, user.stuId);
			}
			else {
				pstmt = con.prepareStatement("select * from orderinfo");
			}
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				String dateDetail = "";
				String choices = rs.getString("choices");
				for (int i = 0; i < 4; i++) {
					if (choices.charAt(i) == '1') {
						switch (i) {
							case 0:
								dateDetail += "8:30-11:30";
								break;
							case 1:
								dateDetail += "11:30-14:30";
								break;
							case 2:
								dateDetail += "14:30-17:30";
								break;
							case 3:
								dateDetail += "17:30-20:30";
								break;
						}
						if(i != 3) {
							dateDetail += "\n";
						}
					}
				}
				Timestamp time = rs.getTimestamp("submitTime");
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd\n HH:mm");
				String submitTime = dateFormat.format(time);
				OrderMsg om = new OrderMsg(rs.getString("orderID"), rs.getString("classRoom"),
						rs.getString("stuID"), rs.getString("date"),
						rs.getString("reason"), dateDetail,
						submitTime, rs.getInt("status"));
				
				System.out.println(submitTime);
				list.add(om);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if(rs != null)
					rs.close();
				if(con != null)
					con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(list.size() != 0) {
			JSONArray jsonA = new JSONArray(list);
			json.put("data", jsonA.toString());
			json.put("info", "get");
		}
		else {
			json.put("info", "null");
		}
		return json.toString();
	}
	
	/**
	 * 根据申请编码来更新对应的申请的状态
	 * @param orderId
	 * @param status
	 * @return
	 */
	public static boolean updateOrderInfo(int orderId, int status) {
		PreparedStatement pstmt = null;
		int count = 0;
		
		ConnectDB();
		
		try {
			pstmt = con.prepareStatement("UPDATE orderinfo SET status = ?, submitTime = NOW() WHERE orderID = ?");
			pstmt.setInt(1, status);
			pstmt.setInt(2, orderId);
			
			count = pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if(con != null)
					con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(count > 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public static String queryClassroomChoices(String classroom, String date) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int choices = 0;
		JSONObject json = new JSONObject();
		
		ConnectDB();
		
		try {
			pstmt = con.prepareStatement("select choices from orderinfo where classRoom = ? and date = ?");
			pstmt.setString(1, classroom);
			pstmt.setString(2, date);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				//先将二进制字符串转化为十进制数字再进行运算
				int bit = Integer.valueOf(rs.getString(1), 2);
				choices = choices | bit;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				if(rs != null)
					rs.close();
				if(pstmt != null)
					pstmt.close();
				if(con != null)
					con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		json.put("choices", choices);
		return json.toString();
	}
}