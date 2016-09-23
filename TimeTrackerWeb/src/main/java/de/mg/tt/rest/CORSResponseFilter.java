package de.mg.tt.rest;


import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CORSResponseFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            HttpServletResponse http = (HttpServletResponse) response;
            http.addHeader("Access-Control-Allow-Origin", "*");
            http.addHeader("Access-Control-Allow-Credentials", "true");
            http.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
            http.addHeader("Access-Control-Allow-Headers", "content-type, accept");
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }


}
