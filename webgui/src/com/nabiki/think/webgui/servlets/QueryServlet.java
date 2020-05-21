package com.nabiki.think.webgui.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import javax.json.bind.Jsonb;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nabiki.think.crawler.yumi.data.QueryResult;
import com.nabiki.think.webgui.DataAccess;

@WebServlet(urlPatterns = {"/yumi"})
public class QueryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Set response header.
		resp.setHeader("Content-Type", "application/json; charset=UTF-8");
		resp.setHeader("Transfer-Encoding", "chunked");
		
		var da = (DataAccess)req.getServletContext().getAttribute("DataAccess");
		var jsonb = (Jsonb)req.getServletContext().getAttribute("Jsonb");
		
		if (jsonb == null || da == null) {
			writeError(resp.getWriter());
			System.err.println("Attribute null pointer.");
			return;
		}
		
		QueryResult res = da.yumi().get(Integer.valueOf(req.getParameter("queryId")));
		if (res == null) {
			writeError(resp.getWriter());
			System.err.println("Query result null pointer.");
			return;
		}
		
		var str = jsonb.toJson(res);
		resp.getOutputStream().write(str.getBytes(Charset.forName("UTF-8")));
		resp.getOutputStream().flush();
	}
	
	private void writeError(PrintWriter pw) {
		pw.write("{\"name\":\"error\", \"type\": null, \"list\":[]}");
	}
}
