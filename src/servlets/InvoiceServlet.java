package servlets;

import dao.OrderDAO;
import dao.OrderDetailDAO;
import models.Order;
import models.OrderDetail;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/invoice")
public class InvoiceServlet extends HttpServlet {
    private OrderDAO orderDAO = new OrderDAO();
    private OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int orderId = Integer.parseInt(request.getParameter("orderId"));
        
        Order order = orderDAO.getOrderById(orderId);
        List<OrderDetail> orderDetails = orderDetailDAO.getOrderDetailsByOrderId(orderId);
        
        request.setAttribute("order", order);
        request.setAttribute("orderDetails", orderDetails);
        request.getRequestDispatcher("/jsp/invoice.jsp").forward(request, response);
    }
}
