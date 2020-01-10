<%@page import="java.util.List"%>
<%@page import="com.bigdata2019.mysite.vo.BoardVo"%>
<%@page import="com.bigdata2019.mysite.vo.UserVo"%>
<%@page import="com.bigdata2019.mysite.repository.BoardDao"%>

<%--
<%@page import="com.bigdata2019.mysite.web.util.WebUtil"%>
<%@page import="javax.servlet.ServletException"%>
<%@page import="javax.servlet.http.HttpServlet"%>
<%@page import="javax.servlet.http.HttpSession"%>
<%@page import="javax.servlet.http.HttpServletRequest"%>
<%@page import="javax.servlet.http.HttpServletResponse"%>
 --%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
		UserVo authUser = null;

		//현재 로그인상태 및 계정 체크
		HttpSession session1 = request.getSession();

		if(session != null) {
			authUser = (UserVo)session .getAttribute("authUser");
		}
%>
<!DOCTYPE html>
<html>
<head>
<title>mysite</title>
<meta http-equiv="content-type" content="text/html; charset=utf-8">
<link href="<%=request.getContextPath()%>/assets/css/board.css" rel="stylesheet" type="text/css">
</head>
<body>
	<div id="container">
		<jsp:include page="/WEB-INF/views/includes/header.jsp"/>
		<div id="content">
			<div id="board">
				<form id="search_form" action="<%=request.getContextPath() %>/board?a=search" method="post">
					<select name="sType">
						<option value="title">제목</option>
						<option value="content">내용</option>
					</select>
					<input type="text" id="kwd" name="sWord" value="">
					<input type="submit" value="찾기">					
				</form>				
<%
		String Mode = "";
		String sType = "";
		String sWord = "";

		if(request.getAttribute("Mode") != null){
			Mode  = (String)request.getAttribute("Mode");
			sType = (String)request.getAttribute("sType");
			sWord = (String)request.getAttribute("sWord");
		}
		
		List<BoardVo> list = (List)request.getAttribute("voList");
		int totalCount	   = ((Integer)request.getAttribute("voTotalCount")).intValue();
		int pageCount 	   = ((Integer)request.getAttribute("voPageCount")).intValue();
		int pageNo 		   = ((Integer)request.getAttribute("voPageNo")).intValue();
%>
		
			<table class="tbl-ex">
					<tr>
						<th>번호</th>
						<th>제목</th>
						<th>글쓴이</th>
						<th>조회수</th>
						<th>작성일</th>
						<th>&nbsp;</th>
					</tr>

<%					
			for(BoardVo vo : list){
%>
					<tr>
						<td>[<%=vo.getNo() %>]</td>
<%
				if(vo.getTitle().length() >= 20){
%>
					<td><a href="<%=request.getContextPath() %>/board?a=view&no=<%=vo.getNo() %>" id="view" class="view"><%=vo.getTitle().substring(0, 20) %>...</a></td>
<%				
				} else {
%>
						<td><a href="<%=request.getContextPath() %>/board?a=view&no=<%=vo.getNo() %>" id="view" class="view"><%=vo.getTitle() %></a></td>
<%
				}
%>				
						<td><%=vo.getUserName() %></td>
						<td><%=vo.getHit() %></td>
						<td><%=vo.getRegDate() %></td>
						
<%
				if(authUser != null && authUser.getNo() == vo.getUserNo()) { 				
%>
						<td><a href="<%=request.getContextPath() %>/board?a=delete&no=<%=vo.getNo() %>" id="delete" class="del">삭제</a></td>
<%
				}
%>					

<%
			}
%>
					</tr>

				</table>
<%
		if(Mode.equals("sMode")){
%>								
				<div class="pager">
					<ul>
<%
					if(pageNo==1){
%>
						<li><a style="text-decoration: none;" >◀</a></li>
<%						
					} else {
%>					
						<li><a style="text-decoration: none;" href ="<%=request.getContextPath() %>/board?a=search&pageNo=<%=pageNo-1 %>&sType=<%=sType %>&sWord=<%=sWord %>">◀</a></li>						
<%
					}

					for(int i=1; i<=pageCount; i++){
						if(i==pageNo){
%>
							<li><a style="font-weight:bold;font-size:15px;" href ="<%=request.getContextPath() %>/board?a=search&pageNo=<%=i %>&sType=<%=sType %>&sWord=<%=sWord %>"><%=i %></a></li>
<%												
						} else {
%>	
	 						<li><a style="text-decoration: none;" href ="<%=request.getContextPath() %>/board?a=search&pageNo=<%=i %>&sType=<%=sType %>&sWord=<%=sWord %>"><%=i %></a></li>  						
<%
						}
					}
					
					if(pageNo==pageCount){
%>				
						<li><a style="text-decoration: none;" >▶</a></li>
<%
					} else {
%>						
						<li><a style="text-decoration: none;" href ="<%=request.getContextPath() %>/board?a=search&pageNo=<%=pageNo+1 %>&sType=<%=sType %>&sWord=<%=sWord %>">▶</a></li>
<%
					}
%>				
					</ul>
				</div>
<%
		} else {
%>					
				<div class="pager">
					<ul>
<%
					if(pageNo==1){
%>
						<li><a style="text-decoration: none;" >◀</a></li>
<%						
					} else {
%>					
						<li><a style="text-decoration: none;" href ="<%=request.getContextPath() %>/board?a=page&pageNo=<%=pageNo-1 %>">◀</a></li>						
<%
					}

					for(int i=1; i<=pageCount; i++){
						if(i==pageNo){
%>
							<li><a style="font-weight:bold;font-size:15px;" href ="<%=request.getContextPath() %>/board?a=page&pageNo=<%=i %>"><%=i %></a></li>
<%												
						} else {
%>	
	 						<li><a style="text-decoration: none;" href ="<%=request.getContextPath() %>/board?a=page&pageNo=<%=i %>"><%=i %></a></li>
<%
						}
					}
					
					if(pageNo==pageCount){
%>				
						<li><a style="text-decoration: none;" >▶</a></li>
<%
					} else {
%>						
						<li><a style="text-decoration: none;" href ="<%=request.getContextPath() %>/board?a=page&pageNo=<%=pageNo+1 %>">▶</a></li>
<%
					}
%>				
					</ul>
				</div>					
<%
		}
%>									
				<!--  pager 추가 -->
							
				<div class="bottom">
				<a style="text-decoration: none;" href="<%=request.getContextPath() %>/board?a=new-book" id="new-book">글쓰기</a>	
				</div>					
			</div>	
		</div>
		<jsp:include page="/WEB-INF/views/includes/navigation.jsp"/>
		<jsp:include page="/WEB-INF/views/includes/footer.jsp"/>			
	</div>	
</body>
</html>