package de.mg.tt.rest

import java.io.IOException

import javax.servlet._
import javax.servlet.http.HttpServletResponse

class CORSResponseFilter extends Filter {

  @throws[ServletException]
  override def init(filterConfig: FilterConfig): Unit = {
  }

  @throws[IOException]
  @throws[ServletException]
  override def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain): Unit = {
    response match {
      case http: HttpServletResponse =>
        http.addHeader("Access-Control-Allow-Origin", "*")
        http.addHeader("Access-Control-Allow-Credentials", "true")
        http.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
        http.addHeader("Access-Control-Allow-Headers", "content-type, accept, Authorization")
      case _ =>
    }
    chain.doFilter(request, response)
  }

  override def destroy(): Unit = {
  }
}