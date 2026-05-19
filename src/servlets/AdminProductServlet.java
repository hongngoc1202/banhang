package servlets;

import dao.ProductDAO;
import dao.CategoryDAO;
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

@WebServlet("/admin/product")
public class AdminProductServlet extends HttpServlet {
    private ProductDAO productDAO = new ProductDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("edit".equals(action)) {
            int productId = Integer.parseInt(request.getParameter("id"));
            Product product = productDAO.getProductById(productId);
            List<Category> categories = categoryDAO.getAllCategories();
            
            request.setAttribute("product", product);
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/jsp/admin/product-form.jsp").forward(request, response);
        } else if ("delete".equals(action)) {
            int productId = Integer.parseInt(request.getParameter("id"));
            productDAO.deleteProduct(productId);
            response.sendRedirect(request.getContextPath() + "/admin/product");
        } else if ("add".equals(action)) {
            List<Category> categories = categoryDAO.getAllCategories();
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/jsp/admin/product-form.jsp").forward(request, response);
        } else {
            List<Product> products = productDAO.getAllProducts();
            List<Category> categories = categoryDAO.getAllCategories();
            
            request.setAttribute("products", products);
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/jsp/admin/product-list.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String action = request.getParameter("action");
        Product product = new Product();
        
        product.setProductName(request.getParameter("productName"));
        product.setCategoryId(Integer.parseInt(request.getParameter("categoryId")));
        product.setDescription(request.getParameter("description"));
        product.setPrice(Double.parseDouble(request.getParameter("price")));
        product.setStockQuantity(Integer.parseInt(request.getParameter("stockQuantity")));
        product.setImageUrl(request.getParameter("imageUrl"));
        product.setStatus(request.getParameter("status"));
        
        if ("add".equals(action)) {
            productDAO.addProduct(product);
        } else if ("edit".equals(action)) {
            product.setProductId(Integer.parseInt(request.getParameter("productId")));
            productDAO.updateProduct(product);
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/product");
    }
}
