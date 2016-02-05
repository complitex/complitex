package org.complitex.template.web.pages.login;

import org.apache.commons.codec.binary.Hex;
import org.complitex.template.web.security.SecurityWebListener;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Artem
 */
@WebServlet(LoginSuccessServlet.PATH)
public class LoginSuccessServlet extends HttpServlet {

    static final String PATH = "/login_success";

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse responce) throws ServletException {
        try {
            String context = request.getServletContext().getContextPath();
            String login = new String(Hex.decodeHex(request.getParameter("login").toCharArray()));
            String password = new String(Hex.decodeHex(request.getParameter("password").toCharArray()));

            request.login(login, password);
            request.getSession().setAttribute(SecurityWebListener.LOGGED_IN, login);

            String url = context + "/";
            responce.encodeRedirectURL(url);
            responce.sendRedirect(url);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
