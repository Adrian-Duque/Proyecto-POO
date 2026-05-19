package Vista;

import Controlador.GestorUsuarios;
import Modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Ventana de inicio de sesión y registro de la aplicación MiniJuegos.
 *
 * <p>Presenta un formulario con dos modos intercambiables:</p>
 * <ul>
 *   <li><b>Login:</b> el usuario introduce sus credenciales para acceder.</li>
 *   <li><b>Registro:</b> el usuario crea una cuenta nueva.</li>
 * </ul>
 *
 * <p>La lógica de autenticación y validación se delega completamente en
 * {@link GestorUsuarios}; esta clase solo gestiona la presentación.</p>
 *
 * @author Adrián
 * @version 2.0
 */
public class VentanaLogin extends JFrame {

    private final GestorUsuarios gestorUsuarios;

    private JLabel  lblTitulo;
    private JLabel  lblSubtitulo;
    private JLabel  lblUsername;
    private JTextField txtUsername;
    private JLabel  lblPassword;
    private JPasswordField txtPassword;
    private JButton btnAccion;
    private JButton btnCambiarModo;
    private JLabel  lblError;

    private boolean modoLogin = true;

    public VentanaLogin(GestorUsuarios gestorUsuarios) {
        this.gestorUsuarios = gestorUsuarios;
        construirUI();
        actualizarModo();
    }

    private void construirUI() {
        setTitle("MiniJuegos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(Tema.FONDO);
        root.setBorder(new EmptyBorder(40, 40, 40, 40));
        setContentPane(root);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Tema.FONDO_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.ACENTO, 1),
                new EmptyBorder(36, 40, 36, 40)
        ));
        card.setPreferredSize(new Dimension(380, 430));

        lblTitulo = new JLabel("MINIJUEGOS");
        lblTitulo.setFont(Tema.FUENTE_TITULO);
        lblTitulo.setForeground(Tema.ACENTO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        lblSubtitulo = new JLabel();
        lblSubtitulo.setFont(Tema.FUENTE_LABEL);
        lblSubtitulo.setForeground(Tema.SUBTEXTO);
        lblSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);

        lblUsername = new JLabel("Usuario");
        lblUsername.setFont(Tema.FUENTE_LABEL);
        lblUsername.setForeground(Tema.TEXTO);
        lblUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUsername = crearCampoTexto();

        lblPassword = new JLabel("Contraseña");
        lblPassword.setFont(Tema.FUENTE_LABEL);
        lblPassword.setForeground(Tema.TEXTO);
        lblPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPassword = new JPasswordField();
        estilizarCampo(txtPassword);

        lblError = new JLabel(" ");
        lblError.setFont(Tema.FUENTE_LABEL);
        lblError.setForeground(Tema.ERROR);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblError.setHorizontalAlignment(SwingConstants.CENTER);

        btnAccion = new JButton();
        btnAccion.setFont(Tema.FUENTE_BOTON);
        btnAccion.setForeground(Color.BLACK);
        btnAccion.setBackground(Tema.ACENTO);
        btnAccion.setFocusPainted(false);
        btnAccion.setBorderPainted(false);
        btnAccion.setOpaque(true);
        btnAccion.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAccion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnAccion.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAccion.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btnAccion.setBackground(Tema.ACENTO.brighter());
            }
            @Override public void mouseExited(MouseEvent e) {
                btnAccion.setBackground(Tema.ACENTO);
            }
        });
        btnAccion.addActionListener(e -> {
            if (modoLogin) accionLogin();
            else           accionRegistro();
        });

        btnCambiarModo = new JButton();
        btnCambiarModo.setFont(Tema.FUENTE_ENLACE);
        btnCambiarModo.setForeground(Tema.ACENTO);
        btnCambiarModo.setBackground(Tema.FONDO_PANEL);
        btnCambiarModo.setBorderPainted(false);
        btnCambiarModo.setFocusPainted(false);
        btnCambiarModo.setContentAreaFilled(false);
        btnCambiarModo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCambiarModo.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCambiarModo.setHorizontalAlignment(SwingConstants.CENTER);
        btnCambiarModo.addActionListener(e -> cambiarModo());

        txtPassword.addActionListener(e -> {
            if (modoLogin) accionLogin();
            else           accionRegistro();
        });

        card.add(lblTitulo);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(lblSubtitulo);
        card.add(Box.createRigidArea(new Dimension(0, 28)));
        card.add(lblUsername);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(txtUsername);
        card.add(Box.createRigidArea(new Dimension(0, 16)));
        card.add(lblPassword);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(txtPassword);
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        card.add(lblError);
        card.add(Box.createRigidArea(new Dimension(0, 16)));
        card.add(btnAccion);
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        card.add(btnCambiarModo);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        root.add(card, gbc);

        pack();
        setLocationRelativeTo(null);
    }

    private JTextField crearCampoTexto() {
        JTextField campo = new JTextField();
        estilizarCampo(campo);
        return campo;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setFont(Tema.FUENTE_CAMPO);
        campo.setForeground(Tema.TEXTO);
        campo.setBackground(Tema.FONDO_CAMPO);
        campo.setCaretColor(Tema.ACENTO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDE, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Tema.ACENTO, 1),
                        new EmptyBorder(8, 12, 8, 12)
                ));
            }
            @Override public void focusLost(FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Tema.BORDE, 1),
                        new EmptyBorder(8, 12, 8, 12)
                ));
            }
        });
    }

    private void accionLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            mostrarError("Rellena todos los campos.");
            return;
        }

        Usuario usuario = gestorUsuarios.iniciarSesion(username, password);

        if (usuario != null) {
            limpiarError();
            abrirMenuPrincipal();
        } else {
            mostrarError("Usuario o contraseña incorrectos.");
            txtPassword.setText("");
        }
    }

    private void accionRegistro() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            mostrarError("Rellena todos los campos.");
            return;
        }

        String error = gestorUsuarios.registrarUsuario(username, password);

        if (error == null) {
            gestorUsuarios.iniciarSesion(username, password);
            limpiarError();
            abrirMenuPrincipal();
        } else {
            mostrarError(error);
        }
    }

    private void cambiarModo() {
        modoLogin = !modoLogin;
        limpiarError();
        txtUsername.setText("");
        txtPassword.setText("");
        actualizarModo();
    }

    private void actualizarModo() {
        if (modoLogin) {
            lblSubtitulo.setText("Inicia sesión para jugar");
            btnAccion.setText("Iniciar sesión");
            btnCambiarModo.setText("¿No tienes cuenta? Regístrate");
        } else {
            lblSubtitulo.setText("Crea tu cuenta");
            btnAccion.setText("Registrarse");
            btnCambiarModo.setText("¿Ya tienes cuenta? Inicia sesión");
        }
    }

    private void mostrarError(String msg) { lblError.setText(msg); }
    private void limpiarError()           { lblError.setText(" "); }

    private void abrirMenuPrincipal() {
        new VentanaMenuPrincipal(
                gestorUsuarios,
                Aplicacion.getGestorPartidas(),
                Aplicacion.getGestorEstadisticas(),
                Aplicacion.getGestorJuegos()
        ).setVisible(true);
        dispose();
    }
}