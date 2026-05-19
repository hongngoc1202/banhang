package servlets;

import dao.CartDAO;
import dao.OrderDAO;
import dao.OrderDetailDAO;
import dao.ProductDAO;
import dao.RevenueDAO;
import models.CartItem;
import models.Order;
import models.OrderDetail;
import models.Product;
import models.Revenue;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {
    private CartDAO cartDAO = new CartDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
    private RevenueDAO revenueDAO = new RevenueDAO();
    private ProductDAO productDAO = new ProductDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        int userId = (int) session.getAttribute("userId");
        List<CartItem> cartItems = cartDAO.getCartByUserId(userId);
        
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        
        request.setAttribute("cartItems", cartItems);
        request.setAttribute("total", total);
        request.getRequestDispatcher("/jsp/checkout.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        int userId = (int) session.getAttribute("userId");
        String shippingAddress = request.getParameter("shippingAddress");
        String paymentMethod = request.getParameter("paymentMethod");
        String notes = request.getParameter("notes");
        
        List<CartItem> cartItems = cartDAO.getCartByUserId(userId);
        
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        
        // Create order
        Order order = new Order(userId, total);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setNotes(notes);
        order.setStatus("pending");
        
        int orderId = orderDAO.addOrder(order);
        
        if (orderId > 0) {
            // Add order details
            for (CartItem item : cartItems) {
                OrderDetail detail = new OrderDetail(orderId, item.getProductId(), item.getQuantity(), item.getPrice());
                orderDetailDAO.addOrderDetail(detail);
                
                // Update product stock
                Product product = productDAO.getProductById(item.getProductId());
                product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
                productDAO.updateProduct(product);
            }
            
            // Add revenue
            Revenue revenue = new Revenue(orderId, total, new Date(System.currentTimeMillis()));
            revenueDAO.addRevenue(revenue);
            
            // Clear cart
            cartDAO.clearCart(userId);
            
            response.sendRedirect(request.getContextPath() + "/invoice?orderId=" + orderId);
        } else {
            response.sendRedirect(request.getContextPath() + "/checkout");
        }
    }
}
