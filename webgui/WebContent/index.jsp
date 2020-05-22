<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.nabiki.think.webgui.DataAccess, com.nabiki.think.webgui.utils.YumiItem, com.nabiki.think.webgui.utils.TodayNotice" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<link type="text/css" rel="stylesheet" href="css/main.css">
</head>
<body>
<p align="center">
    <b>
        <span class="index_title"><%= (String)application.getAttribute("IndexTitle")%></span>
        <br /><br />
        <span class="index_update_time"><%= (String)application.getAttribute("UpdateTime")%></span>
    </b>
</p>
<hr/>
<div class="section_container">
<%
	TodayNotice today = (TodayNotice)application.getAttribute("Notice");
	DataAccess da = (DataAccess)application.getAttribute("DataAccess");
	for (String cat : da.category().categoryNames()) {
%>
    <div class="price_section">
        <p class="price_section_title"><%= cat%></p>
        <ul>
<%
        for (YumiItem item : da.category().category(cat)) {
%>
            <li><a href="timeChart.jsp?queryId=<%= item.queryId%>">
            <%= item.displayName%></a>
<%
			if (today.notice(item.queryId)) {
%>
			<span class="today_notice">&#8730;</span>
<%
			}
%>
            </li>
<%
        }
%>
        </ul>
    </div>
<%
	}
%>
</div>
</body>
</html>