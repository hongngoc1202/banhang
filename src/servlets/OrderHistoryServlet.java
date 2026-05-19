package servlets;

import dao.OrderDAO;
import dao.CartDAO;
import models.Order;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/order-history")
public class OrderHistoryServlet extends HttpServlet {
    private OrderDAO orderDAO = new OrderDAO();
    private CartDAO cartDAO = new CartDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        int userId = (int) session.getAttribute("userId");
        List<Order> orders = orderDAO.getOrdersByUserId(userId);
        
        int cartCount = cartDAO.getCartItemCount(userId);
        
        request.setAttribute("orders", orders);
        request.setAttribute("cartCount", cartCount);
        request.getRequestDispatcher("/jsp/order-history.jsp").forward(request, response);
    }
}
