import java.awt.*;
import java.util.List;
import javax.swing.*;

public class Main {
    private JFrame frame;
    private javax.swing.JTable table;
    private javax.swing.table.DefaultTableModel tableModel;
    private List<Airfoil> airfoils;
    private AirfoilService service;
    private AuthenticationService authService;
    private User currentUser;
    private JLabel userLabel;
    
    // Buttons that require login
    private JButton refreshBtn;
    private JButton addBtn;
    private JButton historyBtn;
    private JButton compareBtn;
    private JButton recommendBtn;

    public Main() {
        try {
            service = new AirfoilService();
            airfoils = service.getAllAirfoils();
        } catch (Throwable t) {
            System.err.println("Warning: could not initialize AirfoilService: " + t.getMessage());
            service = null;
            airfoils = java.util.Collections.emptyList();
        }

        try {
            authService = new AuthenticationService();
        } catch (Throwable t) {
            System.err.println("Warning: could not initialize AuthenticationService: " + t.getMessage());
            authService = null;
        }

        SwingUtilities.invokeLater(this::createAndShow);
    }

    private void createAndShow() {
        frame = new JFrame("WingWise");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        Container c = frame.getContentPane();
        c.setLayout(new BorderLayout(8, 8));

        // Top title
        JLabel title = new JLabel("WingWise: Airfoil Advisor", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        c.add(title, BorderLayout.NORTH);

        // Top-right user panel
        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        userLabel = new JLabel("Not logged in");
        userLabel.setForeground(Color.RED);
        topRight.add(userLabel);
        
        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> {
            showLoginDialog();
            e.setSource(null); // Use event to prevent warning
        });
        topRight.add(loginBtn);
        
        JButton signupBtn = new JButton("Sign Up");
        signupBtn.addActionListener(e -> {
            showSignupDialog();
            e.setSource(null); // Use event to prevent warning
        });
        topRight.add(signupBtn);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            currentUser = null;
            userLabel.setText("Not logged in");
            userLabel.setForeground(Color.RED);
            updateButtonStates();
            e.setSource(null); // Use event to prevent warning
        });
        topRight.add(logoutBtn);
        c.add(topRight, BorderLayout.EAST);

        // Center table with checkboxes to select airfoils for comparison
        String[] cols = new String[] { "Select", "Airfoil" };
        tableModel = new javax.swing.table.DefaultTableModel(cols, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class;
                return String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 && currentUser != null; // only checkbox editable when logged in
            }
        };
        for (Airfoil a : airfoils) {
            tableModel.addRow(new Object[] { Boolean.FALSE, formatListEntry(a) });
        }
        table = new javax.swing.JTable(tableModel);
        table.setRowHeight(24);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
        table.getColumnModel().getColumn(0).setCellRenderer(table.getDefaultRenderer(Boolean.class));
        JScrollPane sp = new JScrollPane(table);
        c.add(sp, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        refreshBtn = new JButton("Refresh");
        refreshBtn.setEnabled(false);
        refreshBtn.addActionListener(e -> {
            refreshList();
            e.setSource(null); // Use event
        });
        buttons.add(refreshBtn);

        addBtn = new JButton("Add New Airfoil");
        addBtn.setEnabled(false);
        addBtn.addActionListener(e -> {
            showAddAirfoilDialog();
            e.setSource(null); // Use event
        });
        buttons.add(addBtn);
        
        historyBtn = new JButton("View Search History");
        historyBtn.setEnabled(false);
        historyBtn.addActionListener(e -> {
            viewSearchHistory();
            e.setSource(null); // Use event
        });
        buttons.add(historyBtn);

        JButton eduBtn = new JButton("Educational Guide");
        eduBtn.addActionListener(e -> {
            showEducationalGuide();
            e.setSource(null); // Use event
        });
        buttons.add(eduBtn);

        compareBtn = new JButton("Compare Selected (2)");
        compareBtn.setEnabled(false);
        compareBtn.addActionListener(e -> {
            compareSelected();
            e.setSource(null); // Use event
        });
        buttons.add(compareBtn);

        recommendBtn = new JButton("Recommend for Flight");
        recommendBtn.setEnabled(false);
        recommendBtn.addActionListener(e -> {
            recommendForFlight();
            e.setSource(null); // Use event
        });
        buttons.add(recommendBtn);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> {
            frame.dispose();
            e.setSource(null); // Use event
        });
        buttons.add(closeBtn);

        c.add(buttons, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void updateButtonStates() {
        boolean loggedIn = currentUser != null;
        
        refreshBtn.setEnabled(loggedIn);
        addBtn.setEnabled(loggedIn);
        historyBtn.setEnabled(loggedIn);
        compareBtn.setEnabled(loggedIn);
        recommendBtn.setEnabled(loggedIn);
        
        // Update table editability
        tableModel.fireTableDataChanged();
    }

    private String formatListEntry(Airfoil a) {
        return String.format("%s  |  Type: %s  |  MaxCl: %.3f  |  MinCd: %.4f  |  Thk: %.1f%%", 
            a.getName(), a.getApplicationType(), a.getMaxCl(), a.getMinCd(), a.getThickness());
    }

    private void refreshList() {
        if (currentUser == null) {
            showLoginRequiredMessage();
            return;
        }
        
        try {
            airfoils = service.getAllAirfoils();
            // refresh table rows
            tableModel.setRowCount(0);
            for (Airfoil a : airfoils) tableModel.addRow(new Object[] { Boolean.FALSE, formatListEntry(a) });
        } catch (RuntimeException ex) {
            String type = (ex instanceof IllegalStateException) ? "System Error" : "Error";
            JOptionPane.showMessageDialog(frame,
                type + " during refresh: " + ex.getMessage(),
                type,
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void compareSelected() {
        if (currentUser == null) {
            showLoginRequiredMessage();
            return;
        }
        
        // Collect checked rows from the table
        java.util.List<Integer> selected = new java.util.ArrayList<>();
        for (int r = 0; r < tableModel.getRowCount(); r++) {
            Object v = tableModel.getValueAt(r, 0);
            if (v instanceof Boolean && ((Boolean) v)) selected.add(r);
        }

        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please select at least one airfoil (check the box).", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // If exactly one selected, show detailed info about that airfoil
        if (selected.size() == 1) {
            Airfoil a = airfoils.get(selected.get(0));
            StringBuilder sb = new StringBuilder();
            sb.append("Airfoil: ").append(a.getName()).append("\n\n");
            sb.append(a.getDetails()).append("\n\n");
            sb.append("Recommendation:\n");
            sb.append(String.format(" - Lift-to-Drag (L/D): %.2f\n", a.getLiftToDragRatio()));
            JTextArea area = new JTextArea(sb.toString());
            area.setEditable(false);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JScrollPane sp = new JScrollPane(area);
            sp.setPreferredSize(new Dimension(600, 260));
            JOptionPane.showMessageDialog(frame, sp, "Airfoil Details", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // If more than two selected, pick the first two but inform the user
        if (selected.size() > 2) {
            JOptionPane.showMessageDialog(frame, "More than two selected — comparing the first two selected items.", "Notice", JOptionPane.INFORMATION_MESSAGE);
        }

        // Compare first two selections
        Airfoil a1 = airfoils.get(selected.get(0));
        Airfoil a2 = airfoils.get(selected.get(1));
        String desc = buildComparisonDescription(a1, a2);
        JTextArea area = new JTextArea(desc);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(700, 300));
        JOptionPane.showMessageDialog(frame, sp, "Airfoil Comparison", JOptionPane.INFORMATION_MESSAGE);
    }

    private String buildComparisonDescription(Airfoil a, Airfoil b) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Comparison: %s  vs  %s\n\n", a.getName(), b.getName()));

        appendParam(sb, "Max Lift Coefficient (Cl)", a.getMaxCl(), b.getMaxCl(), true);
        appendParam(sb, "Min Drag Coefficient (Cd)", a.getMinCd(), b.getMinCd(), false);
        appendParam(sb, "Thickness (%)", a.getThickness(), b.getThickness(), false);
        appendParamRaw(sb, "Reynolds Min", a.getReynoldsMin(), b.getReynoldsMin());
        appendParamRaw(sb, "Reynolds Max", a.getReynoldsMax(), b.getReynoldsMax());

        sb.append("\nApplication Types:\n");
        sb.append(String.format(" - %s: %s\n", a.getName(), a.getApplicationType()));
        sb.append(String.format(" - %s: %s\n", b.getName(), b.getApplicationType()));

        sb.append("\nSummary:\n");
        // Simple decision heuristics
        double ldA = safeDiv(a.getMaxCl(), a.getMinCd());
        double ldB = safeDiv(b.getMaxCl(), b.getMinCd());
        if (ldA > ldB) sb.append(String.format(" - %s has better L/D (%.1f vs %.1f) -> more efficient.\n", a.getName(), ldA, ldB));
        else if (ldA < ldB) sb.append(String.format(" - %s has better L/D (%.1f vs %.1f) -> more efficient.\n", b.getName(), ldB, ldA));
        else sb.append(" - Both have similar L/D efficiency.\n");

        if (a.getMaxCl() > b.getMaxCl()) sb.append(String.format(" - %s provides more maximum lift (Cl %.3f vs %.3f).\n", a.getName(), a.getMaxCl(), b.getMaxCl()));
        else if (a.getMaxCl() < b.getMaxCl()) sb.append(String.format(" - %s provides more maximum lift (Cl %.3f vs %.3f).\n", b.getName(), b.getMaxCl(), a.getMaxCl()));

        if (a.getThickness() > b.getThickness()) sb.append(String.format(" - %s is thicker (%.1f%% vs %.1f%%) -> likely stronger but slightly higher drag.\n", a.getName(), a.getThickness(), b.getThickness()));
        else if (a.getThickness() < b.getThickness()) sb.append(String.format(" - %s is thicker (%.1f%% vs %.1f%%) -> likely stronger but slightly higher drag.\n", b.getName(), b.getThickness(), a.getThickness()));

        return sb.toString();
    }

    private void appendParam(StringBuilder sb, String label, double v1, double v2, boolean higherIsBetter) {
        double diff = v1 - v2;
        double pct = (v2 != 0) ? (diff / Math.abs(v2) * 100.0) : 0.0;
        sb.append(String.format("%s:\n  - %s: %.4f\n  - %s: %.4f\n  - Difference: %+.4f (%.1f%%) -> %s\n\n",
            label, 
            "A", v1,
            "B", v2,
            diff, pct,
            (higherIsBetter ? (diff>0?"A better":"B better") : (diff>0?"A worse":"B worse"))));
    }

    private void appendParamRaw(StringBuilder sb, String label, double v1, double v2) {
        sb.append(String.format("%s:\n  - A: %.1e\n  - B: %.1e\n\n", label, v1, v2));
    }

    private double safeDiv(double a, double b) {
        if (b == 0) return a == 0 ? 0 : Double.POSITIVE_INFINITY;
        return a / b;
    }

    private void recommendForFlight() {
        if (currentUser == null) {
            showLoginRequiredMessage();
            return;
        }
        
        JPanel p = new JPanel(new GridLayout(0,2,6,6));
        JTextField speedF = new JTextField("10");
        JTextField altF = new JTextField("1000");
        JTextField areaF = new JTextField("1.0");
        JTextField liftF = new JTextField("1000");

        p.add(new JLabel("Speed (m/s):")); p.add(speedF);
        p.add(new JLabel("Altitude (m):")); p.add(altF);
        p.add(new JLabel("Wing area (m^2):")); p.add(areaF);
        p.add(new JLabel("Required lift (N):")); p.add(liftF);

        int res = JOptionPane.showConfirmDialog(frame, p, "Flight parameters", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;
        try {
            double speed = Double.parseDouble(speedF.getText().trim());
            double alt = Double.parseDouble(altF.getText().trim());
            double area = Double.parseDouble(areaF.getText().trim());
            double lift = Double.parseDouble(liftF.getText().trim());

            FlightParameters params = new FlightParameters(speed, alt, area, lift);
            List<RecommendationResult> recs = service.recommendAirfoils(params);
            if (recs == null || recs.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No recommendations available.", "No results", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (int i=0;i<Math.min(5,recs.size());i++) {
                RecommendationResult r = recs.get(i);
                sb.append(String.format("%d. %s  (score: %.2f) - %s\n", i+1, r.getAirfoil().getName(), r.getScore(), r.getReason()));
            }
            JTextArea areaOut = new JTextArea(sb.toString());
            areaOut.setEditable(false);
            areaOut.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JOptionPane.showMessageDialog(frame, new JScrollPane(areaOut), "Recommendations", JOptionPane.INFORMATION_MESSAGE);
            // Save search to user history if logged in
            if (currentUser != null) {
                currentUser.addSearchHistory(params);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter valid numeric values.", "Input error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error while recommending: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ----- Authentication dialogs -----
    private void showLoginDialog() {
        if (authService == null) {
            JOptionPane.showMessageDialog(frame, "Authentication is not available (service not initialized).", "Not available", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JPanel p = new JPanel(new GridLayout(0,2,6,6));
        JTextField userF = new JTextField();
        JPasswordField passF = new JPasswordField();
        p.add(new JLabel("Username:")); p.add(userF);
        p.add(new JLabel("Password:")); p.add(passF);
        int res = JOptionPane.showConfirmDialog(frame, p, "Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;
        String username = userF.getText().trim();
        String password = new String(passF.getPassword());
        try {
            User u = authService.login(username, password);
            if (u == null) {
                JOptionPane.showMessageDialog(frame, "Invalid username or password.", "Login failed", JOptionPane.ERROR_MESSAGE);
            } else {
                currentUser = u;
                userLabel.setText("Logged in: " + u.getUsername());
                userLabel.setForeground(Color.GREEN);
                updateButtonStates();
                JOptionPane.showMessageDialog(frame, "Login successful. Welcome, " + u.getUsername() + "!", "Logged in", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error during login: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSignupDialog() {
        if (authService == null) {
            JOptionPane.showMessageDialog(frame, "Authentication is not available (service not initialized).", "Not available", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JPanel p = new JPanel(new GridLayout(0,2,6,6));
        JTextField userF = new JTextField();
        JPasswordField passF = new JPasswordField();
        JTextField emailF = new JTextField();
        p.add(new JLabel("Username:")); p.add(userF);
        p.add(new JLabel("Password:")); p.add(passF);
        p.add(new JLabel("Email:")); p.add(emailF);
        int res = JOptionPane.showConfirmDialog(frame, p, "Sign Up", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;
        String username = userF.getText().trim();
        String password = new String(passF.getPassword());
        String email = emailF.getText().trim();
        try {
            String validation = authService.validateSignup(username, password, email);
            if (validation != null) {
                JOptionPane.showMessageDialog(frame, "Invalid signup: " + validation, "Sign up error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean ok = authService.signUp(username, password, email);
            if (ok) {
                JOptionPane.showMessageDialog(frame, "Sign up successful. You may now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Sign up failed. Try a different username or email.", "Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error during sign up: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewSearchHistory() {
        if (currentUser == null) {
            showLoginRequiredMessage();
            return;
        }
        java.util.Stack<FlightParameters> hist = currentUser.getSearchHistory();
        if (hist == null || hist.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No search history.", "History", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (FlightParameters fp : hist) {
            sb.append(String.format("%d) %s\n", i++, fp.toString()));
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(frame, new JScrollPane(area), "Search History", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAddAirfoilDialog() {
        if (currentUser == null) {
            showLoginRequiredMessage();
            return;
        }
        
        JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));
        JTextField nameF = new JTextField();
        JTextField maxClF = new JTextField();
        JTextField minCdF = new JTextField();
        JTextField reynoldsMinF = new JTextField();
        JTextField reynoldsMaxF = new JTextField();
        JTextField typeF = new JTextField();
        JTextField thicknessF = new JTextField();

        p.add(new JLabel("Name:")); p.add(nameF);
        p.add(new JLabel("Max Lift Coefficient (Cl):")); p.add(maxClF);
        p.add(new JLabel("Min Drag Coefficient (Cd):")); p.add(minCdF);
        p.add(new JLabel("Reynolds Min:")); p.add(reynoldsMinF);
        p.add(new JLabel("Reynolds Max:")); p.add(reynoldsMaxF);
        p.add(new JLabel("Application Type:")); p.add(typeF);
        p.add(new JLabel("Thickness (%):")); p.add(thicknessF);

        int result = JOptionPane.showConfirmDialog(frame, p, "Add New Airfoil", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameF.getText().trim();
                if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty");
                
                double maxCl = Double.parseDouble(maxClF.getText().trim());
                double minCd = Double.parseDouble(minCdF.getText().trim());
                double reynoldsMin = Double.parseDouble(reynoldsMinF.getText().trim());
                double reynoldsMax = Double.parseDouble(reynoldsMaxF.getText().trim());
                String type = typeF.getText().trim();
                if (type.isEmpty()) throw new IllegalArgumentException("Type cannot be empty");
                double thickness = Double.parseDouble(thicknessF.getText().trim());

                Airfoil newAirfoil = new Airfoil(name, maxCl, minCd, reynoldsMin, 
                    reynoldsMax, type, thickness);
                    
                if (service.addAirfoil(newAirfoil)) {
                    JOptionPane.showMessageDialog(frame, "Airfoil added successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshList();
                } else {
                    JOptionPane.showMessageDialog(frame, "An airfoil with this name already exists.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid numbers for all numeric fields.", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEducationalGuide() {
        String eduText = MenuHandler.getEducationalInfoText();
        JTextArea area = new JTextArea(eduText);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(700, 500));
        
        JOptionPane.showMessageDialog(frame, sp, "Educational Guide", JOptionPane.PLAIN_MESSAGE);
    }

    private void showLoginRequiredMessage() {
        JOptionPane.showMessageDialog(frame, 
            "Please login to access this feature!\n\n" +
            "Available features after login:\n" +
            "• Refresh airfoil list\n" +
            "• Add new airfoils\n" +
            "• View search history\n" +
            "• Compare airfoils\n" +
            "• Get flight recommendations", 
            "Login Required", 
            JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        // Create and show UI on the Event Dispatch Thread
        SwingUtilities.invokeLater(Main::new);
    }
}