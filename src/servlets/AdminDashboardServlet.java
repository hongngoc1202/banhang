package servlets;

import dao.ProductDAO;
import dao.CategoryDAO;
import dao.OrderDAO;
import dao.WarehouseDAO;
import dao.RevenueDAO;
import models.Product;
import models.Category;
import models.Order;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {
    private ProductDAO productDAO = new ProductDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private RevenueDAO revenueDAO = new RevenueDAO();
    private WarehouseDAO warehouseDAO = new WarehouseDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Get statistics
        List<Product> products = productDAO.getAllProducts();
        List<Order> allOrders = orderDAO.getAllOrders();
        
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        int currentYear = cal.get(Calendar.YEAR);
        
        double monthlyRevenue = revenueDAO.getTotalRevenueByMonth(currentMonth, currentYear);
        List<Product> lowStockProducts = productDAO.getProductsLowStock(10);
        
        request.setAttribute("totalProducts", products.size());
        request.setAttribute("totalOrders", allOrders.size());
        request.setAttribute("monthlyRevenue", monthlyRevenue);
        request.setAttribute("lowStockCount", lowStockProducts.size());
        
        request.getRequestDispatcher("/jsp/admin/dashboard.jsp").forward(request, response);
    }
}
