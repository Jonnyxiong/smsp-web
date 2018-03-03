package com.ucpaas.smsp.controller.console;

import com.ucpaas.smsp.constant.Constant;
import com.ucpaas.smsp.controller.BaseController;
import com.ucpaas.smsp.model.SmsAccountModel;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

@Controller
@RequestMapping("console/help")
public class HelpController extends BaseController {

    //帮助中心：短信接入简介
	@RequestMapping(value = "/smsAccessIntroduce")
	public ModelAndView smsAccessIntroduce(ModelAndView view,HttpServletRequest request) throws Exception {
		SmsAccountModel accountModel = (SmsAccountModel) request.getSession()
				.getAttribute(Constant.LOGIN_USER_INFO);
		view.addObject("accountModel",accountModel);
		view.setViewName("console/help/smsAccessIntroduce");
		return view;
	}
	
	 
	
	//帮助中心：返回码
	@RequestMapping(value = "/FAQ")
	public ModelAndView FAQ(ModelAndView view,HttpServletRequest request) throws Exception {
		SmsAccountModel accountModel = (SmsAccountModel) request.getSession()
				.getAttribute(Constant.LOGIN_USER_INFO);
		view.addObject("accountModel",accountModel);
		view.setViewName("console/help/FAQ");
		return view;
	}
	
	@RequestMapping(value="/smsIntroduceDoc")
    public ResponseEntity<byte[]> download() throws IOException{
    	
    	String path = request.getSession().getServletContext().getRealPath("/template/sms-introduce.docx");
        String fileName = "云之讯新短信https接口文档V1.0.docx";
        fileName = URLEncoder.encode(fileName, "UTF-8");
        File downloadFile = new File(path);
        
        HttpHeaders headers = new HttpHeaders();  
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);  
        headers.setContentDispositionFormData("attachment", new String(fileName.getBytes(),"utf-8"));
        
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(downloadFile), headers, HttpStatus.CREATED);  
    }

	@RequestMapping(value = "/smsCmppIntroduceDoc")
	public ResponseEntity<byte[]> downloadCmppDoc(HttpServletRequest request, HttpSession session) throws IOException {

		String path = request.getSession().getServletContext().getRealPath("/template/sms-cmpp-api-v2.0.doc");
		String fileName = URLEncoder.encode("短信cmpp接口文档V2.0.doc","UTF-8"); //"SMS_CMPP_API_DOCUMENT_V2.0.doc";
		File downloadFile = new File(path);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment", new String(fileName.getBytes(), "utf-8"));

		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(downloadFile), headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/smsSgipIntroduceDoc")
	public ResponseEntity<byte[]> downloadSgipDoc(HttpServletRequest request, HttpSession session) throws IOException {

		String path = request.getSession().getServletContext().getRealPath("/template/sms-sgip-api-v1.2.doc");
//		String fileName = "SMS_SGIP_API_DOCUMENT_V1.0.doc";
		String fileName = URLEncoder.encode("短信sgip接口文档V1.2.doc","UTF-8");
		File downloadFile = new File(path);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment", new String(fileName.getBytes(), "utf-8"));

		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(downloadFile), headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/smsSmgpIntroduceDoc")
	public ResponseEntity<byte[]> downloadSmgpDoc(HttpServletRequest request, HttpSession session) throws IOException {

		String path = request.getSession().getServletContext().getRealPath("/template/sms-smgp-api-v3.0.3.doc");
		String fileName = URLEncoder.encode("短信smgp接口文档V3.0.doc","UTF-8"); //"SMS_SMGP_API_DOCUMENT_V3.0.doc";
		File downloadFile = new File(path);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment", new String(fileName.getBytes(), "utf-8"));

		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(downloadFile), headers, HttpStatus.CREATED);
	}
	@RequestMapping(value = "/smsSmppIntroduceDoc")
	public ResponseEntity<byte[]> downloadSmppDoc(HttpServletRequest request, HttpSession session) throws IOException {

		String path = request.getSession().getServletContext().getRealPath("/template/sms-smpp-api-v3.4.pdf");
		String fileName = URLEncoder.encode("短信smpp接口文档V3.4.pdf","UTF-8"); //"SMS_SMPP_API_DOCUMENT_V3.4.doc";
		File downloadFile = new File(path);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment", new String(fileName.getBytes(), "utf-8"));

		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(downloadFile), headers, HttpStatus.CREATED);
	}

    @RequestMapping(value="/smsCaiyinDoc")
    public ResponseEntity<byte[]> smsCaiyinDoc() throws IOException{
    	
    	String path = request.getSession().getServletContext().getRealPath("/template/sms-caiyin-introduce.docx");
    	String fileName = "云之讯彩印https接口文档V1.0.docx";
        fileName = URLEncoder.encode(fileName, "UTF-8");
    	File downloadFile = new File(path);
    	
    	HttpHeaders headers = new HttpHeaders();  
    	headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);  
    	headers.setContentDispositionFormData("attachment", new String(fileName.getBytes(),"utf-8"));
    	
    	return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(downloadFile), headers, HttpStatus.CREATED);  
    }
    
    @RequestMapping(value="/smsGuajiDoc")
    public ResponseEntity<byte[]> smsGuajiDoc() throws IOException{
    	
    	String path = request.getSession().getServletContext().getRealPath("/template/sms-guaji-introduce.docx");
    	String fileName = "云之讯挂机短信https接口文档V1.0.docx";
        fileName = URLEncoder.encode(fileName, "UTF-8");
    	File downloadFile = new File(path);
    	
    	HttpHeaders headers = new HttpHeaders();  
    	headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);  
    	headers.setContentDispositionFormData("attachment", new String(fileName.getBytes(),"utf-8"));
    	
    	return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(downloadFile), headers, HttpStatus.CREATED);  
    }
	
}
