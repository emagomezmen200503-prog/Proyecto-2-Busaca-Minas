package ui;

import service.TareaService;
import domain.Tarea;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Interfaz Swing mínima: formulario y tabla.
 * Muchas funcionalidades están esbozadas para que sea sencillo de entender e integrar.
 */
public class MainFrame extends JFrame {
    private TareaService service;
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel statusBar;

    public MainFrame() {
        setTitle("Gestor de Tareas - SQL Server (Ejemplo)");
        setSize(800,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        try {
            service = new TareaService();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error iniciando servicio: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        initUI();
        cargarTabla();
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout());
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JTextField txtTitulo = new JTextField(20);
        JComboBox<Integer> cbPrioridad = new JComboBox<>(new Integer[]{1,2,3});
        JCheckBox chEspecial = new JCheckBox("★ Especial");
        JTextField txtFecha = new JTextField(12);
        txtFecha.setToolTipText("Formato: YYYY-MM-DDTHH:MM (ej: 2025-08-12T10:30) opcional");

        JButton btnAgregar = new JButton("Agregar");
        JButton btnAlternar = new JButton("Alternar Hecho");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnDeshacer = new JButton("Deshacer");
        statusBar = new JLabel("Listo");

        form.add(new JLabel("Título:")); form.add(txtTitulo);
        form.add(new JLabel("Prioridad:")); form.add(cbPrioridad);
        form.add(txtFecha); form.add(chEspecial);
        form.add(btnAgregar); form.add(btnAlternar); form.add(btnEliminar); form.add(btnDeshacer);

        tableModel = new DefaultTableModel(new Object[]{"ID","Título","Prioridad","Estado","Especial","Fecha"},0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);

        main.add(form, BorderLayout.NORTH);
        main.add(scroll, BorderLayout.CENTER);
        main.add(statusBar, BorderLayout.SOUTH);
        add(main);

        btnAgregar.addActionListener(e -> {
            try {
                String titulo = txtTitulo.getText();
                int pr = (int)cbPrioridad.getSelectedItem();
                boolean esp = chEspecial.isSelected();
                LocalDateTime fecha = null;
                if (!txtFecha.getText().trim().isEmpty()) {
                    fecha = LocalDateTime.parse(txtFecha.getText().trim());
                }
                service.crearTarea(titulo, pr, esp, fecha);
                statusBar.setText("Tarea creada");
                cargarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnAlternar.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r==-1) { JOptionPane.showMessageDialog(this, "Seleccione una fila"); return; }
            long id = (long) table.getValueAt(r,0);
            try {
                service.alternarHecho(id);
                statusBar.setText("Estado alternado");
                cargarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEliminar.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r==-1) { JOptionPane.showMessageDialog(this, "Seleccione una fila"); return; }
            long id = (long) table.getValueAt(r,0);
            try {
                service.eliminar(id);
                statusBar.setText("Tarea eliminada (oculta)");
                cargarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnDeshacer.addActionListener(e -> {
            try {
                service.deshacerUltimaEliminacion();
                statusBar.setText("Deshacer ejecutado");
                cargarTabla();
            } catch (UnsupportedOperationException uex) {
                JOptionPane.showMessageDialog(this, "Funcionalidad 'deshacer' requiere implementación adicional. Ver README.", "Info", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void cargarTabla() {
        try {
            List<Tarea> lista = service.listarVisibles();
            tableModel.setRowCount(0);
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (Tarea t : lista) {
                Object[] row = new Object[] {
                    t.getId(),
                    t.getTitulo(),
                    t.getPrioridad(),
                    t.isHecho() ? "Hecho" : "Pendiente",
                    t.isEspecial() ? "★" : "",
                    t.getFecha() == null ? "" : t.getFecha().format(df)
                };
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            statusBar.setText("Error cargando tabla: " + ex.getMessage());
        }
    }
}
