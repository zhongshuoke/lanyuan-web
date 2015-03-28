package com.lanyuan.controller.common;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class BaseCommonController {
	protected Logger LOG = Logger.getLogger(this.getClass());
	
	/**
	 * 发送Ajax请求结果json
	 *
	 * @throws ServletException
	 * @throws IOException
	 */
	public void sendAjaxMsg(HttpServletResponse response, String json) {
		response.setContentType("application/json;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter out = response.getWriter();
			out.write(json);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
