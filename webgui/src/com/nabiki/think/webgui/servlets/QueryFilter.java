package com.nabiki.think.webgui.servlets;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import com.nabiki.think.crawler.yumi.Utils;

@WebFilter(urlPatterns = {"/yumi"})
public class QueryFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		var param = request.getParameter("queryId");
		
		try {
			var queryId = Integer.valueOf(param);
			if (!Utils.ids.contains(queryId)) {
				System.err.println("Unknown query ID: " + param);
				return;
			}
			
			chain.doFilter(request, response);
		} catch (NumberFormatException e) {
			System.err.println("Bad query ID format: " + param + ". " + e.getMessage());
		}

	}
}
