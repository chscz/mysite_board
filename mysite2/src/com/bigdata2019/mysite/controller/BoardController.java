package com.bigdata2019.mysite.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bigdata2019.mysite.repository.BoardDao;
import com.bigdata2019.mysite.vo.BoardVo;
import com.bigdata2019.mysite.vo.UserVo;
import com.bigdata2019.mysite.web.util.WebUtil;

public class BoardController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		String action = request.getParameter("a");	//log
		System.out.println(action);
		
		//글쓰기
		if("new-book".equals(action)) {
			
			//세션체크
			HttpSession session = request.getSession();
			if(session == null) {
				WebUtil.redirect(request, response, request.getContextPath());
				return;
			}
			//유저정보 체크
			UserVo authUser = (UserVo)session .getAttribute("authUser");
			if(authUser == null) {
				WebUtil.redirect(request, response, request.getContextPath());
				return;
			}

			WebUtil.forward(request, response, "/WEB-INF/views/board/write.jsp");
			
		//글삭제
		} else if("delete".equals(action)) {
			String BoardNo = request.getParameter("no");			
			new BoardDao().delete(BoardNo);			
			WebUtil.redirect(request, response, request.getContextPath()+"/board");
			
		//글쓰기-등록
		} else if("write".equals(action)) {
			//세션체크
			HttpSession session = request.getSession();
			if(session == null) {
				WebUtil.redirect(request, response, request.getContextPath());
				return;
			}
			//유저정보 체크
			UserVo authUser = (UserVo)session .getAttribute("authUser");
			if(authUser == null) {
				WebUtil.redirect(request, response, request.getContextPath());
				return;
			}
					
			String title = request.getParameter("title");
			String content = request.getParameter("content");
			Long userNo = authUser.getNo();
				
			BoardVo vo = new BoardVo();
			vo.setTitle(title);
			vo.setContent(content);			
			vo.setUserNo(userNo);

			new BoardDao().insert(vo);					
	
			WebUtil.redirect(request, response, request.getContextPath()+"/board");
			
		//게시글 내용보기	
		} else if("view".equals(action)) {
			WebUtil.forward(request, response, "/WEB-INF/views/board/view.jsp");

		//게시글 내용보기-글수정
		} else if("modify".equals(action)) {
			WebUtil.forward(request, response, "/WEB-INF/views/board/modify.jsp");
			
		//게시글 수정-수정완료
		} else if("update".equals(action)) {			
			//세션체크 -> 수정완료했을때 세션이 끊어진 상태라면 다시 리스트로

			HttpSession session = request.getSession();
			if(session == null) {
				WebUtil.redirect(request, response, request.getContextPath());
				return;
			}
//			//유저정보 체크 -> 유저정보를 이미 체크하고 modify로 들어오기 때문에 불필요
//			UserVo authUser = (UserVo)session .getAttribute("authUser");
//			if(authUser == null) {
//				WebUtil.redirect(request, response, request.getContextPath());
//				return;				
//			}

			String title = request.getParameter("title");
			String content = request.getParameter("content");
			Long no = Long.parseLong(request.getParameter("no"));
			
			BoardVo vo = new BoardVo();
			vo.setTitle(title);
			vo.setContent(content);			
			vo.setNo(no);
			
			new BoardDao().update(vo);

			
			//글 수정 후 목록 재 호출
			String pageNo = request.getParameter("pageNo");
			
			if(pageNo == null) {
				pageNo = "1";
			}
			
			List<BoardVo> list = new BoardDao().findList(Integer.parseInt(pageNo));
			int totalCount = new BoardDao().pageCount();
			int pageCount = (int)(Math.ceil((double)totalCount/10));			
			
			request.setAttribute("voList", list);
			request.setAttribute("voTotalCount", totalCount);
			request.setAttribute("voPageCount", pageCount);
			request.setAttribute("voPageNo", Integer.parseInt(pageNo));
			
			WebUtil.forward(request, response, "/WEB-INF/views/board/list.jsp");			

			
		//게시판 검색기능
		} else if("search".equals(action)) {
			String listMode = "sMode";
			String sType = request.getParameter("sType");
			String sWord = request.getParameter("sWord");
			String pageNo = request.getParameter("pageNo");
			int sType2 = 0;
			int totalCount = 0;
			if(sType.equals("title")) {
				sType2 = 1;
			} else {
				sType2 = 2;
			}
			if(pageNo == null) {
				pageNo = "1";
			}
			List<BoardVo> list = new BoardDao().search(sType2, sWord, Integer.parseInt(pageNo));
			totalCount = new BoardDao().pageCount_Search(sType2, sWord);
			int pageCount = (int)(Math.ceil((double)totalCount/10));

			request.setAttribute("Mode", listMode);
			request.setAttribute("sType", sType);
			request.setAttribute("sWord", sWord);
			request.setAttribute("voList", list);
			request.setAttribute("voTotalCount", totalCount);
			request.setAttribute("voPageCount", pageCount);
			request.setAttribute("voPageNo", Integer.parseInt(pageNo));
			
			WebUtil.forward(request, response, "/WEB-INF/views/board/list.jsp");			
			
		//전체리스트 뿌려주기(초기화면, 글쓰기->취소, 내용보기->글목록)
		//여기가 action = "page" 처리되는곳 
		} else {
			String pageNo = request.getParameter("pageNo");
			
			if(pageNo == null) {
				pageNo = "1";
			}
			
			List<BoardVo> list = new BoardDao().findList(Integer.parseInt(pageNo));
			int totalCount = new BoardDao().pageCount();
			int pageCount = (int)(Math.ceil((double)totalCount/10));			
			
			request.setAttribute("voList", list);
			request.setAttribute("voTotalCount", totalCount);
			request.setAttribute("voPageCount", pageCount);
			request.setAttribute("voPageNo", Integer.parseInt(pageNo));
			
			WebUtil.forward(request, response, "/WEB-INF/views/board/list.jsp");
		}
			
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
