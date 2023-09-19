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
		//���û���
		response.setCharacterEncoding("UTF-8");
		PrintWriter pw = response.getWriter();
		JSONObject json = new JSONObject();
		//��ȡ�û���Ϣ
		HttpSession session = request.getSession();
		//��ȡtomcat�µ�headPicĿ¼��·��
		String path = getServletContext().getRealPath("/headPic");
		//��ʱ�ļ���Ŀ¼
		String tmpPath = getServletContext().getRealPath("/tmp");
		//��������Ƿ����ļ��ϴ�����
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if(!isMultipart) {
			json.put("status", "failure");
			json.put("msg", "�ϴ�����ʽ����");
			pw.print(json.toString());
			return;
		}
		//1.����DiskFileItemFactory�����࣬������ָ������������һ����ʱĿ¼
		DiskFileItemFactory disk = new DiskFileItemFactory(1024 * 10, new File(tmpPath));
		//2.����ServletFileUpload�������ϱߵ���ʱ�ļ���Ҳ����Ĭ��ֵ
		ServletFileUpload up = new ServletFileUpload(disk);
		//���õ����ļ����ܳ���15M
		up.setFileSizeMax(15*1024*1024);
		//�������ļ����ܳ���90M
		up.setSizeMax(90*1024*1024);
		//3.����request
		try {
			List<FileItem> list = up.parseRequest(request);
			//�����һ���ļ�
			FileItem file = list.get(0);
			//��ȡ���ֶ�����,�ɸ��ݴ˽��ļ����ò�ͬ�ļ�����
			String formName = file.getFieldName();
			//��ȡ�ļ���
			String fileName = file.getName();
			//��ȡ�ļ�����
			String fileType = file.getContentType();
			//��ȡ�ļ����ֽ���
			InputStream in = file.getInputStream();
			//�ļ���С
			int size = file.getInputStream().available();
			//��������ֽ����Լ��µ��ļ���
			String newName = UUID.randomUUID() + fileName;//��ͬ����headPath��ֵ
			String newPath = null;
			//�޸����ݿ�ɹ���־
			boolean result = false;
			//�޸����ݿ���Ϣ
			if(formName.equals("headPic")) {
				//���ݱ����ƾ����ļ���
				newPath = path + "/" + newName;
				System.out.println(session.getAttribute("ID"));
				result = DBModel.updateHeadPic(newName, session.getAttribute("ID").toString());
			}
			
			if (result || !formName.equals("headPic")) {
				// ��������ֽ���
				OutputStream out = new FileOutputStream(newPath);
				// �ļ���ֵ
				byte[] b = new byte[1024];
				int len = 0;
				while ((len = in.read(b)) != -1) {
					out.write(b, 0, len);
				}
				out.flush();
				out.close();

				// ɾ���ϴ����ɵ���ʱ�ļ�
				file.delete();

				// ��ʾ����
				json.put("status", "success");
				json.put("msg", "�ɹ��ϴ���");
				json.put("headPath", newName);
				json.put("fileType", fileType);
				json.put("formName", formName);
				json.put("fileName", fileName);
			}
			else {
				json.put("status", "failure");
				json.put("msg", "���ݿ�д��ʧ��");
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
