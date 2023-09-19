package easyRent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory; 
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONObject;

/**
 * Servlet implementation class UploadServlet
 */
@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		//设置回显
		response.setCharacterEncoding("UTF-8");
		PrintWriter pw = response.getWriter();
		JSONObject json = new JSONObject();
		//获取用户信息
		HttpSession session = request.getSession();
		//获取tomcat下的headPic目录的路径
		String path = getServletContext().getRealPath("/headPic");
		//临时文件的目录
		String tmpPath = getServletContext().getRealPath("/tmp");
		//检查我们是否有文件上传请求
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if(!isMultipart) {
			json.put("status", "failure");
			json.put("msg", "上传请求方式有误！");
			pw.print(json.toString());
			return;
		}
		//1.声明DiskFileItemFactory工厂类，用于在指定磁盘上设置一个临时目录
		DiskFileItemFactory disk = new DiskFileItemFactory(1024 * 10, new File(tmpPath));
		//2.声明ServletFileUpload，接收上边的临时文件，也可以默认值
		ServletFileUpload up = new ServletFileUpload(disk);
		//设置单个文件不能超过15M
		up.setFileSizeMax(15*1024*1024);
		//设置总文件不能超过90M
		up.setSizeMax(90*1024*1024);
		//3.解析request
		try {
			List<FileItem> list = up.parseRequest(request);
			//如果就一个文件
			FileItem file = list.get(0);
			//获取表单字段名称,可根据此将文件放置不同文件夹下
			String formName = file.getFieldName();
			//获取文件名
			String fileName = file.getName();
			//获取文件类型
			String fileType = file.getContentType();
			//获取文件的字节码
			InputStream in = file.getInputStream();
			//文件大小
			int size = file.getInputStream().available();
			//声明输出字节流以及新的文件名
			String newName = UUID.randomUUID() + fileName;//这同样是headPath的值
			String newPath = null;
			//修改数据库成功标志
			boolean result = false;
			//修改数据库信息
			if(formName.equals("headPic")) {
				//根据表单名称决定文件夹
				newPath = path + "/" + newName;
				System.out.println(session.getAttribute("ID"));
				result = DBModel.updateHeadPic(newName, session.getAttribute("ID").toString());
			}
			
			if (result || !formName.equals("headPic")) {
				// 声明输出字节流
				OutputStream out = new FileOutputStream(newPath);
				// 文件赋值
				byte[] b = new byte[1024];
				int len = 0;
				while ((len = in.read(b)) != -1) {
					out.write(b, 0, len);
				}
				out.flush();
				out.close();

				// 删除上传生成的临时文件
				file.delete();

				// 显示数据
				json.put("status", "success");
				json.put("msg", "成功上传！");
				json.put("headPath", newName);
				json.put("fileType", fileType);
				json.put("formName", formName);
				json.put("fileName", fileName);
			}
			else {
				json.put("status", "failure");
				json.put("msg", "数据库写入失败");
			}
			pw.print(json.toString());
		}
		catch (FileUploadException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

}
