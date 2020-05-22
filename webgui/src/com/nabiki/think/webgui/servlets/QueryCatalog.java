package com.nabiki.think.webgui.servlets;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.json.bind.Jsonb;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nabiki.think.webgui.DataAccess;

@WebServlet(urlPatterns = {"/catalog"})
public class QueryCatalog extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Set response header.
		resp.setHeader("Content-Type", "application/json; charset=UTF-8");
		resp.setHeader("Transfer-Encoding", "chunked");
		
		var da = (DataAccess)req.getServletContext().getAttribute("DataAccess");
		var jsonb = (Jsonb)req.getServletContext().getAttribute("Jsonb");
		
		if (da == null || jsonb == null) {
			System.err.println("Attribute null pointer.");
			// Set error info.
			req.setAttribute("name", "data object null pointer");
			req.setAttribute("type", "internal error");
			// Forward.
			req.getRequestDispatcher("/error").forward(req, resp);
		} else {
			var c = da.catalog();
			// Send catalog as JSON.
			resp.getOutputStream().write(jsonb.toJson(c).getBytes(Charset.forName("UTF-8")));
			resp.getOutputStream().flush();
		}
	}
}
