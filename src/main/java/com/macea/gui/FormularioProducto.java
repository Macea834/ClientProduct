package com.macea.gui;

import com.macea.client.ClientRest;
import com.macea.dto.ProductoDto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class FormularioProducto extends JFrame {

    // Única conexión con la API: todo pasa por aquí
    private final ClientRest clientRest = new ClientRest();

    private final JTextField txtCodigo = new JTextField(15);
    private final JTextField txtNombre = new JTextField(15);
    private final JTextField txtDescripcion = new JTextField(15);
    private final JTextField txtPrecio = new JTextField(15);

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Código", "Nombre", "Descripción", "Precio"}, 0);
    private final JTable tabla = new JTable(modelo);

    public FormularioProducto() {
        super("Gestión de Productos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ---- Formulario (arriba) ----
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createTitledBorder("Datos del producto"));

        GridBagConstraints etiqueta = new GridBagConstraints();
        etiqueta.gridx = 0;
        etiqueta.anchor = GridBagConstraints.WEST;
        etiqueta.insets = new Insets(5, 10, 5, 10);

        GridBagConstraints campo = new GridBagConstraints();
        campo.gridx = 1;
        campo.fill = GridBagConstraints.HORIZONTAL;
        campo.weightx = 1.0;
        campo.insets = new Insets(5, 0, 5, 10);

        String[] etiquetas = {"Código:", "Nombre:", "Descripción:", "Precio:"};
        JTextField[] campos = {txtCodigo, txtNombre, txtDescripcion, txtPrecio};

        for (int i = 0; i < etiquetas.length; i++) {
            etiqueta.gridy = i;
            campo.gridy = i;
            formulario.add(new JLabel(etiquetas[i]), etiqueta);
            formulario.add(campos[i], campo);
        }

        // ---- Tabla (centro) ----
        tabla.setRowHeight(22);
        tabla.getTableHeader().setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(650, 280));
        scroll.setBorder(BorderFactory.createTitledBorder("Productos"));

        // ---- Botones (abajo) ----
        JButton btnGuardar = new JButton("Guardar");
        JButton btnConsultar = new JButton("Consultar");
        JButton btnListar = new JButton("Listar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        JPanel botones = new JPanel(new GridLayout(1, 6, 8, 0));
        botones.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        botones.add(btnGuardar);
        botones.add(btnConsultar);
        botones.add(btnListar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        // Agrupamos el formulario y la tabla para dar margen uniforme
        JPanel contenido = new JPanel(new BorderLayout(0, 10));
        contenido.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        contenido.add(formulario, BorderLayout.NORTH);
        contenido.add(scroll, BorderLayout.CENTER);

        add(contenido, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);

        // ---- Eventos: cada botón llama a un método del formulario ----
        btnGuardar.addActionListener(e -> ejecutar(this::guardar));
        btnConsultar.addActionListener(e -> ejecutar(this::consultar));
        btnListar.addActionListener(e -> ejecutar(this::listar));
        btnActualizar.addActionListener(e -> ejecutar(this::actualizar));
        btnEliminar.addActionListener(e -> ejecutar(this::eliminar));
        btnLimpiar.addActionListener(e -> limpiar());

        pack();
        setMinimumSize(new Dimension(700, 520));
        setLocationRelativeTo(null);
    }

    // ---- Acciones de los botones ----

    private void guardar() {
        ProductoDto creado = clientRest.insertarProducto(leerFormulario());
        if (creado != null) {
            mostrar("Producto guardado.");
        } else {
            mostrar("No se pudo guardar. Revisa que el código no exista.");
        }
    }

    private void consultar() {
        String codigo = txtCodigo.getText().trim();
        ProductoDto producto = clientRest.consultarProducto(codigo);
        if (producto != null) {
            llenarCampos(producto);
        } else {
            mostrar("Producto no encontrado.");
        }
    }

    private void listar() {
        List<ProductoDto> productos = clientRest.consultarTodosLosProductos();
        modelo.setRowCount(0); // borra las filas anteriores
        if (productos == null) {
            mostrar("No se pudo obtener la lista.");
            return;
        }
        for (ProductoDto p : productos) {
            modelo.addRow(new Object[]{p.getCodigo(), p.getNombre(), p.getDescripcion(), p.getPrecio()});
        }
    }

    private void actualizar() {
        ProductoDto datos = leerFormulario();
        ProductoDto actualizado = clientRest.actualizarProducto(datos.getCodigo(), datos);
        if (actualizado != null) {
            mostrar("Producto actualizado.");
        } else {
            mostrar("Producto no encontrado.");
        }
    }

    private void eliminar() {
        String codigo = txtCodigo.getText().trim();
        if (clientRest.eliminarProducto(codigo)) {
            mostrar("Producto eliminado.");
            limpiar();
        } else {
            mostrar("Producto no encontrado.");
        }
    }

    private void limpiar() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtPrecio.setText("");
    }

    // ---- Ayudas ----

    private ProductoDto leerFormulario() {
        return new ProductoDto(
                txtCodigo.getText().trim(),
                txtNombre.getText().trim(),
                txtDescripcion.getText().trim(),
                new BigDecimal(txtPrecio.getText().trim())); // puede lanzar NumberFormatException
    }

    private void llenarCampos(ProductoDto p) {
        txtCodigo.setText(p.getCodigo());
        txtNombre.setText(p.getNombre());
        txtDescripcion.setText(p.getDescripcion());
        txtPrecio.setText(String.valueOf(p.getPrecio()));
    }

    // Ejecuta la acción y muestra cualquier error sin cerrar la ventana
    private void ejecutar(Runnable accion) {
        try {
            accion.run();
        } catch (NumberFormatException ex) {
            mostrar("El precio debe ser un número.");
        } catch (Exception ex) {
            mostrar("Error de conexión o del servidor: " + ex.getMessage());
        }
    }

    private void mostrar(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    // ---- Punto de entrada ----

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormularioProducto().setVisible(true));
    }
}