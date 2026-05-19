package servlets;

import dao.WarehouseDAO;
import dao.ProductDAO;
import models.WarehouseLog;
import models.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/warehouse")
public class AdminWarehouseServlet extends HttpServlet {
    private WarehouseDAO warehouseDAO = new WarehouseDAO();
    private ProductDAO productDAO = new ProductDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("import".equals(action)) {
            List<Product> products = productDAO.getAllProducts();
            request.setAttribute("products", products);
            request.getRequestDispatcher("/jsp/admin/warehouse-import.jsp").forward(request, response);
        } else if ("export".equals(action)) {
            List<Product> products = productDAO.getAllProducts();
            request.setAttribute("products", products);
            request.getRequestDispatcher("/jsp/admin/warehouse-export.jsp").forward(request, response);
        } else if ("logs".equals(action)) {
            List<WarehouseLog> logs = warehouseDAO.getAllWarehouseLogs();
            request.setAttribute("logs", logs);
            request.getRequestDispatcher("/jsp/admin/warehouse-logs.jsp").forward(request, response);
        } else {
            List<Product> products = productDAO.getAllProducts();
            List<Product> lowStockProducts = productDAO.getProductsLowStock(10);
            
            request.setAttribute("products", products);
            request.setAttribute("lowStockProducts", lowStockProducts);
            request.getRequestDispatcher("/jsp/admin/warehouse.jsp").forward(request, response);
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
        
        int userId = (int) session.getAttribute("userId");
        String action = request.getParameter("action");
        int productId = Integer.parseInt(request.getParameter("productId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        String reason = request.getParameter("reason");
        
        if ("import".equals(action)) {
            warehouseDAO.importStock(productId, quantity, userId);
            response.sendRedirect(request.getContextPath() + "/admin/warehouse");
        } else if ("export".equals(action)) {
            boolean success = warehouseDAO.exportStock(productId, quantity, userId);
            if (!success) {
                request.setAttribute("error", "Không đủ hàng để xuất");
            }
            response.sendRedirect(request.getContextPath() + "/admin/warehouse");
        }
    }
}
