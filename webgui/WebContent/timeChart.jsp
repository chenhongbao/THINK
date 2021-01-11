<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page
	import="com.nabiki.think.webgui.DataAccess, com.nabiki.think.crawler.yumi.data.QueryResult, com.nabiki.think.crawler.yumi.data.ValuePair, java.time.format.DateTimeFormatter, java.util.regex.Pattern, java.util.regex.Matcher"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%
	int queryId = 2075; // Default value.
try {
	queryId = Integer.valueOf(request.getParameter("queryId"));
} catch (NumberFormatException e) {
}

DataAccess da = (DataAccess) application.getAttribute("DataAccess");
QueryResult result = da.yumi().get(queryId);
%>
<script type="text/javascript">
	var data = [
<%if (result.list.size() > 0) {
	Pattern p = Pattern.compile("[0-9]+(\\.[0-9]+)?");
	Double prev = null;
	int outCnt = 0;
	for (int idx = 0; idx < result.list.size(); ++idx) {
		Double value = null;
		String dateStr = null;
		try {
			String text = result.list.get(idx).data;
			Matcher m = p.matcher(text);
			if (m.find()) {
				value = Double.valueOf(text.substring(m.start(), m.end()));
				prev = value;
			} else {
				value = prev;
			}
			dateStr = result.list.get(idx).date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		} catch (Throwable ignored) {
		}
		if (value == 0.0D) {
			continue;
		}
		// First element has no prefix comma.
		if (outCnt > 0) {
			out.print(",");
		}
		out.print("{date:Date.parse(\"" + dateStr + "\"), value:" + value + "}");
		++outCnt;
	}
}%>
	];
</script>
<link type="text/css" rel="stylesheet" href="css/main.css">
<script src="https://www.amcharts.com/lib/4/core.js"></script>
<script src="https://www.amcharts.com/lib/4/charts.js"></script>
<script src="https://www.amcharts.com/lib/4/themes/animated.js"></script>
<script type="text/javascript" src="js/timeChart.js"></script>
</head>
<body>
	<p align="center">
		<b> <span class="chart_title"><%=result.name%></span> <br /> <span
			class="chart_subtitle">(<%=result.unit%>)
		</span>
		</b>
	</p>
	<div class="chart" id="chart_div"></div>
</body>
</html>