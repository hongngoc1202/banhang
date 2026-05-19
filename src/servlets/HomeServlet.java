package servlets;

import dao.ProductDAO;
import dao.CategoryDAO;
import dao.CartDAO;
import models.Product;
import models.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private ProductDAO productDAO = new ProductDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private CartDAO cartDAO = new CartDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Product> products = productDAO.getAllProducts();
        List<Category> categories = categoryDAO.getAllCategories();
        
        request.setAttribute("products", products);
        request.setAttribute("categories", categories);
        
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            int userId = (int) session.getAttribute("userId");
            int cartCount = cartDAO.getCartItemCount(userId);
            request.setAttribute("cartCount", cartCount);
        }
        
        request.getRequestDispatcher("/jsp/index.jsp").forward(request, response);
    }
}
