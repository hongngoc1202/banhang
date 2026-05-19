package servlets;

import dao.RevenueDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

@WebServlet("/admin/revenue")
public class AdminRevenueServlet extends HttpServlet {
    private RevenueDAO revenueDAO = new RevenueDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        int currentYear = cal.get(Calendar.YEAR);
        
        String monthParam = request.getParameter("month");
        String yearParam = request.getParameter("year");
        
        if (monthParam != null && !monthParam.isEmpty()) {
            currentMonth = Integer.parseInt(monthParam);
        }
        if (yearParam != null && !yearParam.isEmpty()) {
            currentYear = Integer.parseInt(yearParam);
        }
        
        double monthlyRevenue = revenueDAO.getTotalRevenueByMonth(currentMonth, currentYear);
        List<Object[]> dailyRevenue = revenueDAO.getDailyRevenueByMonth(currentMonth, currentYear);
        List<Object[]> monthlyData = revenueDAO.getMonthlyRevenueByYear(currentYear);
        
        request.setAttribute("currentMonth", currentMonth);
        request.setAttribute("currentYear", currentYear);
        request.setAttribute("monthlyRevenue", monthlyRevenue);
        request.setAttribute("dailyRevenue", dailyRevenue);
        request.setAttribute("monthlyData", monthlyData);
        
        request.getRequestDispatcher("/jsp/admin/revenue.jsp").forward(request, response);
    }
}
