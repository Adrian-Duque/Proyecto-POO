package Vista;

import javax.swing.*;
import java.awt.*;

import Controlador.GestorJuegos;

public class VentanaSeleccionJuego extends JDialog {

    private String juegoSeleccionado;
    private JList<String> listaJuegos;

    public VentanaSeleccionJuego(JFrame padre, GestorJuegos gestorJuegos){
        super(padre, "Selecciona un Juego", true);
        setSize(280,220);
        setLocationRelativeTo(padre);
        setResizable(false);

        DefaultListModel<String> modelo = new DefaultListModel<>();
        for(String nombre : gestorJuegos.getJuegosDisponibles()){
            modelo.addElement(nombre);
        }

        listaJuegos = new JList<>(modelo);
        listaJuegos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaJuegos.setSelectedIndex(0);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        panel.add(new JScrollPane(listaJuegos), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSeleccionar = new JButton("Jugar");
        btnSeleccionar.addActionListener(e -> accionSeleccionar());
        JButton btnCancelar = new JButton("Cancelar");
        btnSeleccionar.addActionListener(e -> accionCancelar());
        botones.add(btnSeleccionar);
        botones.add(btnCancelar);
        panel.add(botones, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }

    private void accionSeleccionar() {
        juegoSeleccionado = listaJuegos.getSelectedValue();
        dispose();
    }

    private void accionCancelar() {
        juegoSeleccionado = null;
        dispose();
    }

    public String getJuegoSeleccionado() {
        return juegoSeleccionado;
    }



    public static void main(String[] args) {
        // Crea un GestorJuegos con juegos de prueba
        GestorJuegos gestorJuegos = new GestorJuegos();
        gestorJuegos.registrarJuego("Pasapalabra");
        gestorJuegos.registrarJuego("Tres en Raya");
    
        // Necesita un JFrame padre (puede ser invisible)
        JFrame parent = new JFrame();
    
        VentanaSeleccionJuego ventana = new VentanaSeleccionJuego(parent, gestorJuegos);
    
        System.out.println("Juego seleccionado: " + ventana.getJuegoSeleccionado());
        System.exit(0);
    }

}
