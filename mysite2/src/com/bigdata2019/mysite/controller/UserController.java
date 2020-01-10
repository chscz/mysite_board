package com.bigdata2019.mysite.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bigdata2019.mysite.repository.UserDao;
import com.bigdata2019.mysite.vo.UserVo;
import com.bigdata2019.mysite.web.util.WebUtil;

public class UserController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		String action = request.getParameter("a");
		
		System.out.println(action);
		if("joinform".equals(action)) {
			WebUtil.forward(request, response, "/WEB-INF/views/user/joinform.jsp");
		} else if("join".equals(action)){
			String name = request.getParameter("name");
			String email = request.getParameter("email");
			String password = request.getParameter("password");
			String gender = request.getParameter("gender");
			
			UserVo vo = new UserVo();
			vo.setName(name);
			vo.setEmail(email);
			vo.setPassword(password);
			vo.setGender(gender);
			
			new UserDao().insert(vo);
			
			WebUtil.redirect(request, response, request.getContextPath() + "/user?a=joinsuccess");
		
		} else if("joinsuccess".equals(action)){
			WebUtil.forward(request, response, "/WEB-INF/views/user/joinsuccess.jsp");
		} else if("loginform".equals(action)){
			WebUtil.forward(request, response, "/WEB-INF/views/user/loginform.jsp");
		} else if("updateform".equals(action)){
			/* 접근 제어(ACL) */
			HttpSession session = request.getSession();
			if(session == null) {
				WebUtil.redirect(request, response, request.getContextPath());
				return;
			}
			
			UserVo authUser = (UserVo)session.getAttribute("authUser");
			if(authUser == null) {
				WebUtil.redirect(request, response, request.getContextPath());
				return;
			}
			
			Long no = authUser.getNo();
			UserVo userVo = new UserDao().find(no);
			
			request.setAttribute("userVo", userVo);
			WebUtil.forward(request, response, "/WEB-INF/views/user/updateform.jsp");
			
		} else if("login".equals(action)){
			String email = request.getParameter("email");
			String password = request.getParameter("password");
		
			UserVo vo = new UserDao().find(email, password);
			if(vo == null) {
				WebUtil.redirect(request, response, request.getContextPath() + "/user?a=loginform&result=fail");
				return;
			}
			
			// 로그인 처리
			HttpSession session = request.getSession(true);
			session.setAttribute("authUser", vo);
			
			// main으로 리다이렉트
			WebUtil.redirect(request, response, request.getContextPath());
			
		} else if("logout".equals(action)){
			HttpSession session = request.getSession();
			if(session == null) {
				WebUtil.redirect(request, response, request.getContextPath());
				return;
			}
			
			UserVo authUser = (UserVo)session.getAttribute("authUser");
			if(authUser == null) {
				WebUtil.redirect(request, response, request.getContextPath());
				return;
			}
			
			// logout 처리
			session.removeAttribute("authUser");
			session.invalidate();
			
			WebUtil.redirect(request, response, request.getContextPath());
		} else {
			WebUtil.redirect(request, response, request.getContextPath());
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
