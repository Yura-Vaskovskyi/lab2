import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
public class Main extends JFrame {
    private JTabbedPane tabbedPane;
    private ChartPanel chartPanel;
    private List<DataTablePanel> tables = new ArrayList<>();
    private JButton addTableButton, removeTableButton, updateChartsButton;
    public Main() {
        setTitle("Багатотабличний графік з координатами");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.WEST);
        tabbedPane.setPreferredSize(new Dimension(350, 600));
        JPanel tableControlPanel = new JPanel();
        tableControlPanel.setLayout(new GridLayout(3, 1, 5, 5));
        addTableButton = new JButton("Додати таблицю");
        removeTableButton = new JButton("Видалити таблицю");
        updateChartsButton = new JButton("Оновити графіки");
        tableControlPanel.add(addTableButton);
        tableControlPanel.add(removeTableButton);
        tableControlPanel.add(updateChartsButton);
        add(tableControlPanel, BorderLayout.NORTH);
        chartPanel = new ChartPanel();
        add(chartPanel, BorderLayout.CENTER);
        addTableButton.addActionListener(e -> addNewTable());
        removeTableButton.addActionListener(e -> removeSelectedTable());
        updateChartsButton.addActionListener(e -> updateCharts());
        addNewTable();
    }
    private void addNewTable() {
        DataTablePanel tablePanel = new DataTablePanel();
        tables.add(tablePanel);
        tabbedPane.addTab("Таблиця " + tables.size(), tablePanel);
    }
    private void removeSelectedTable() {
        int index = tabbedPane.getSelectedIndex();
        if (index >= 0) {
            tables.remove(index);
            tabbedPane.remove(index);
            updateCharts();
        }
    }
    private void updateCharts() {
        List<GraphData> graphDataList = new ArrayList<>();
        for (DataTablePanel tablePanel : tables) {
            double[][] data = tablePanel.getData();
            graphDataList.add(new GraphData(data, tablePanel.getChartType(), tablePanel.getXType(),
                    tablePanel.getYType(), tablePanel.getXLabel(), tablePanel.getYLabel(),
                    tablePanel.getXUnit(), tablePanel.getYUnit(), tablePanel.getColor()));
        }
        chartPanel.setGraphs(graphDataList);
        chartPanel.repaint();
    }
    static class DataTablePanel extends JPanel {
        private JTable table;
        private JComboBox<String> xTypeCombo, yTypeCombo;
        private JTextField xLabelField, yLabelField, xUnitField, yUnitField;
        private JButton addRowButton, removeRowButton, openChartButton;
        private Color color;
        public DataTablePanel() {
            setLayout(new BorderLayout());
            color = new Color((int)(Math.random()*0x1000000));
// Створюємо таблицю з 6 рядками і 2 колонками
            DefaultTableModel model = new DefaultTableModel(6, 2);
            table = new JTable(model);
            table.getColumnModel().getColumn(0).setHeaderValue("X");
            table.getColumnModel().getColumn(1).setHeaderValue("Y");
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(300, 150));
            add(scrollPane, BorderLayout.CENTER);
// Панель управління
            JPanel controlPanel = new JPanel();
            controlPanel.setLayout(new GridLayout(8, 2, 5, 5)); // +1 рядок для кнопки "Відкрити окремо"
            xTypeCombo = new JComboBox<>(new String[]{"Цілі числа", "Дробові"});
            yTypeCombo = new JComboBox<>(new String[]{"Цілі числа", "Дробові"});
            xLabelField = new JTextField("X");
            yLabelField = new JTextField("Y");
            xUnitField = new JTextField("од.");
            yUnitField = new JTextField("од.");
            addRowButton = new JButton("Додати рядок");
            removeRowButton = new JButton("Видалити рядок");
            openChartButton = new JButton("Відкрити окремо"); // нова кнопка
            controlPanel.add(new JLabel("X тип:"));
            controlPanel.add(xTypeCombo);
            controlPanel.add(new JLabel("Y тип:"));
            controlPanel.add(yTypeCombo);
            controlPanel.add(new JLabel("Назва X:"));
            controlPanel.add(xLabelField);
            controlPanel.add(new JLabel("Назва Y:"));
            controlPanel.add(yLabelField);
            controlPanel.add(new JLabel("Одиниці X:"));
            controlPanel.add(xUnitField);
            controlPanel.add(new JLabel("Одиниці Y:"));
            controlPanel.add(yUnitField);
            controlPanel.add(addRowButton);
            controlPanel.add(removeRowButton);
            controlPanel.add(openChartButton); // додаємо кнопку до панелі
            add(controlPanel, BorderLayout.SOUTH);
// Дії для кнопок
            addRowButton.addActionListener(e -> ((DefaultTableModel)table.getModel()).addRow(new Object[]{"0","0"}));
            removeRowButton.addActionListener(e -> {
                int row = table.getRowCount();
                if(row > 0)
                    ((DefaultTableModel)table.getModel()).removeRow(row - 1);
            });
            openChartButton.addActionListener(e -> openChartSeparately());
        }
// Метод отримання даних з таблиці
        public double[][] getData() {
            int rows = table.getRowCount();
            double[][] data = new double[rows][2];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < 2; j++) {
                    Object value = table.getValueAt(i, j);
                    try { data[i][j] = Double.parseDouble(value.toString()); }
                    catch (Exception e) { data[i][j] = 0; }
                }
            }
            return data;
        }
// Графік
        public String getChartType() { return "Лінійний"; }
        public String getXType() { return (String)xTypeCombo.getSelectedItem(); }
        public String getYType() { return (String)yTypeCombo.getSelectedItem(); }
        public String getXLabel() { return xLabelField.getText(); }
        public String getYLabel() { return yLabelField.getText(); }
        public String getXUnit() { return xUnitField.getText(); }
        public String getYUnit() { return yUnitField.getText(); }
        public Color getColor() { return color; }
// Відкрити графік цієї таблиці у окремому вікні
        private void openChartSeparately() {
            double[][] data = getData();
            GraphData graphData = new GraphData(data, getChartType(), getXType(), getYType(), getXLabel(), getYLabel(), getXUnit(), getYUnit(), getColor());
            JFrame frame = new JFrame("Окремий графік: " + getXLabel() + " vs " + getYLabel());
            frame.setSize(600, 500);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            ChartPanel singleChartPanel = new ChartPanel();
            List<GraphData> list = new ArrayList<>();
            list.add(graphData);
            singleChartPanel.setGraphs(list);
            frame.add(singleChartPanel);
            frame.setVisible(true);
        }
    }
    static class ChartPanel extends JPanel {
        private List<GraphData> graphs = new ArrayList<>();
        public void setGraphs(List<GraphData> graphs) {
            this.graphs = graphs;
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if(graphs.isEmpty()) return;
            int width = getWidth();
            int height = getHeight();
            int padding = 60;
            int labelPadding = 40; // для підписів сітки
// Глобальні maxX та maxY
            double maxX = Double.MIN_VALUE;
            double maxY = Double.MIN_VALUE;
            for(GraphData graph : graphs){
                for(double[] point : graph.data){
                    if(point[0] > maxX) maxX = point[0];
                    if(point[1] > maxY) maxY = point[1];
                }
            }
            maxX = Math.ceil(maxX/10)*10;
            maxY = Math.ceil(maxY/10)*10;
// Малюємо сітку та підписи координат
            g.setColor(Color.LIGHT_GRAY);
            int gridLines = 10;
            for(int i=0;i<=gridLines;i++){
                int y = padding + i*(height-2*padding)/gridLines;
                int x = padding + i*(width-2*padding)/gridLines;
                g.drawLine(padding, y, width-padding, y); // горизонтальна
                g.drawLine(x, padding, x, height-padding); // вертикальна
                g.setColor(Color.BLACK);
                String yLabel = String.format("%.1f", maxY*(gridLines-i)/gridLines);
                g.drawString(yLabel, padding-labelPadding, y+5);
                String xLabel = String.format("%.1f", maxX*i/gridLines);
                g.drawString(xLabel, x-10, height-padding+15);
                g.setColor(Color.LIGHT_GRAY);
            }
// Малюємо осі
            g.setColor(Color.BLACK);
            g.drawLine(padding, height-padding, width-padding, height-padding);
            g.drawLine(padding, padding, padding, height-padding);
// Малюємо лише лінійні графіки
            for(GraphData graph : graphs){
                g.setColor(graph.color);
                for(int i=0;i<graph.data.length-1;i++){
                    int x1 = padding + (int)(graph.data[i][0]/maxX*(width-2*padding));
                    int y1 = height-padding-(int)(graph.data[i][1]/maxY*(height-2*padding));
                    int x2 = padding + (int)(graph.data[i+1][0]/maxX*(width-2*padding));
                    int y2 = height-padding-(int)(graph.data[i+1][1]/maxY*(height-2*padding));
                    g.drawLine(x1,y1,x2,y2);
                    g.fillOval(x1-3, y1-3, 6, 6);
                    g.fillOval(x2-3, y2-3, 6, 6);
                    // Відображення координат
                    g.drawString("("+graph.data[i][0]+","+graph.data[i][1]+")", x1+5, y1-5);
                    if(i==graph.data.length-2)
                        g.drawString("("+graph.data[i+1][0]+","+graph.data[i+1][1]+")", x2+5, y2-5);
                }
            }
// Відображення назв осей та одиниць окремо, щоб не накладалися
            if(!graphs.isEmpty()){
                GraphData g0 = graphs.get(0);
                g.setColor(Color.BLACK);
                g.drawString(g0.xLabel + " (" + g0.xUnit + ")", width/2 - 20, height-10);
                g.drawString(g0.yLabel + " (" + g0.yUnit + ")", 10, padding-20);
            }
        }
    }
    static class GraphData {
        double[][] data;
        String chartType, xType, yType, xLabel, yLabel, xUnit, yUnit;
        Color color;
        public GraphData(double[][] data, String chartType, String xType, String yType, String xLabel, String yLabel, String xUnit, String yUnit, Color color){
            this.data = data;
            this.chartType = chartType;
            this.xType = xType;
            this.yType = yType;
            this.xLabel = xLabel;
            this.yLabel = yLabel;
            this.xUnit = xUnit;
            this.yUnit = yUnit;
            this.color = color;
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main frame = new Main();
            frame.setVisible(true);
        });
    }
}
