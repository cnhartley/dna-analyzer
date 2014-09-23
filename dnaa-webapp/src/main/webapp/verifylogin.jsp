<%@page import="org.json.JSONObject"%>
<%@page import="com.dnaa.common.dbi.DatabaseInterface"%>
<%
    final String username = request.getParameter("username");    
    final String password = request.getParameter("password");
    
    JSONObject userInfo = DatabaseInterface.getUserInfo(username, password);
    
    if (userInfo != null && userInfo.getInt("id") > 0) {
        session.setAttribute("userid", userInfo.getInt("id"));
        response.sendRedirect("index.jsp");
    }
    else {
    	response.sendRedirect("signin.jsp");
    }
%>