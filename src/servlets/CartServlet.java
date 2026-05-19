package servlets;

import dao.CartDAO;
import dao.ProductDAO;
import models.CartItem;
import models.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    private CartDAO cartDAO = new CartDAO();
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
        request.getRequestDispatcher("/jsp/cart.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String action = request.getParameter("action");
        int userId = (int) session.getAttribute("userId");
        
        if ("add".equals(action)) {
            int productId = Integer.parseInt(request.getParameter("productId"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            
            CartItem item = new CartItem(userId, productId, quantity);
            cartDAO.addToCart(item);
            response.sendRedirect(request.getContextPath() + "/cart");
            
        } else if ("update".equals(action)) {
            int cartId = Integer.parseInt(request.getParameter("cartId"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            
            if (quantity > 0) {
                cartDAO.updateCartItem(cartId, quantity);
            } else {
                cartDAO.removeCartItem(cartId);
            }
            response.sendRedirect(request.getContextPath() + "/cart");
            
        } else if ("remove".equals(action)) {
            int cartId = Integer.parseInt(request.getParameter("cartId"));
            cartDAO.removeCartItem(cartId);
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }
}
