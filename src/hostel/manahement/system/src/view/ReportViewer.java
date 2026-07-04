package hostel.manahement.system.src.view;

import hostel.manahement.system.src.dao.DBConnection;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * ReportViewer allows the user to generate and view Jasper Reports.
 *
 * HOW TO USE:
 *  1. Design your report in Jasper Studio and export it as a compiled .jasper file.
 *  2. Place the .jasper file(s) inside the "reports/" folder of your project.
 *  3. Run the project — click a report button to preview it.
 *
 * Two reports are wired up:
 *   - Revenue Report  (reports/revenue_report.jasper)
 *   - Occupancy Report (reports/occupancy_report.jasper)
 */
public class ReportViewer extends JFrame {

    public ReportViewer() {
        initUI();
    }

    private void initUI() {
        setTitle("Reports");
        setSize(500, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(0x16A085));
        JLabel title = new JLabel("REPORTS");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Report buttons
        JPanel center = new JPanel(new GridLayout(3, 1, 15, 15));
        center.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));
        center.setBackground(Color.WHITE);

        JButton btnRevenue   = makeBtn("📊 Revenue Report",   new Color(0x16A085));
        JButton btnOccupancy = makeBtn("🛏 Occupancy Report", new Color(0x2980B9));
        JButton btnGuest     = makeBtn("👤 Guest History Report", new Color(0x8E44AD));

        center.add(btnRevenue);
        center.add(btnOccupancy);
        center.add(btnGuest);
        add(center, BorderLayout.CENTER);

        btnRevenue.addActionListener(e   -> generateReport("/hostel/manahement/system/reports/revenue_report.jrxml",   new HashMap<>()));
        btnOccupancy.addActionListener(e -> generateReport("/hostel/manahement/system/reports/occupancy_report.jrxml", new HashMap<>()));
        btnGuest.addActionListener(e     -> generateReport("/hostel/manahement/system/reports/guest_report.jrxml",     new HashMap<>()));

        setVisible(true);
    }

    private void generateReport(String reportPath, Map<String, Object> params) {
    try {

        Connection conn = DBConnection.getInstance().getConnection();

        InputStream input = getClass().getResourceAsStream(reportPath);

        JasperReport report = JasperCompileManager.compileReport(input);

        JasperPrint print = JasperFillManager.fillReport(report, params, conn);

        JasperViewer.viewReport(print, false);

        

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
   }

    private JButton makeBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
