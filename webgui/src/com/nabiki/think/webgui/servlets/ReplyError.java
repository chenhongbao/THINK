package com.nabiki.think.webgui.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/error"})
public class ReplyError extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		writeError(resp.getWriter(), (String)req.getAttribute("name"), (String)req.getAttribute("type"));
	}

	private void writeError(PrintWriter pw, String name, String type) {
		pw.write("{\"name\":\"" + name + "\", \"type\":\"" + type + "\", \"list\":[]}");
	}
}
