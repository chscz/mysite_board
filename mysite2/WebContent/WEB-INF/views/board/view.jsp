<%@page import="com.bigdata2019.mysite.vo.UserVo"%>
<%@page import="com.bigdata2019.mysite.vo.BoardVo"%>
<%@page import="com.bigdata2019.mysite.repository.BoardDao"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<!DOCTYPE html>
<html>
<head>
<title>mysite</title>
<meta http-equiv="content-type" content="text/html; charset=utf-8">
<link href="<%=request.getContextPath()%>/assets/css/board.css" rel="stylesheet" type="text/css">
</head>
<body>
	<div id="container">
		<div id="header">
		<jsp:include page="/WEB-INF/views/includes/header.jsp"/>
		</div>
		<div id="content">
			<div id="board" class="board-form">
			
<%
		UserVo authUser = null;
		
		//현재 로그인상태 및 계정 체크
		HttpSession session1 = request.getSession();
		
		if(session != null) {
			authUser = (UserVo)session .getAttribute("authUser");
		}

		Long BoardNo = Long.parseLong(request.getParameter("no"));
		BoardVo vo = new BoardDao().findOne(BoardNo);		
%>			
				<table class="tbl-ex">
					<tr>
						<th colspan="2">글보기</th>
					</tr>
					<tr>
						<td class="label">제목</td>
						<td><%=vo.getTitle() %></td>
					</tr>
					<tr>
						<td class="label">내용</td>
						<td>					
							<div class="view-content">
								<%=vo.getContent().replace("\r\n", "<br>") %>
							<%--
								<%=vo.getContent() %>
							 --%>
							</div>
						</td>
					</tr>
				</table>
				<div class="bottom">
					<a href="<%=request.getContextPath() %>/board">글목록</a>
<%					
				if(authUser != null && authUser.getNo() == vo.getUserNo()) {
%>
					<a href="<%=request.getContextPath() %>/board?a=modify&no=<%=vo.getNo() %>">글수정</a>
<%
				} else {
					Boolean hit = new BoardDao().hitCount(BoardNo);					
				}
%>
				</div>
			</div>
		</div>
		<jsp:include page="/WEB-INF/views/includes/navigation.jsp"/>
		<jsp:include page="/WEB-INF/views/includes/footer.jsp"/>
	</div>
</body>
</html>