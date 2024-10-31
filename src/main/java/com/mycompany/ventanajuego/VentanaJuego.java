package com.mycompany.ventanajuego;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;

public class VentanaJuego extends JFrame {
    private int x1 = 150, y1 = 200;
    private int x2 = 400, y2 = 200;
    private int velocidad = 10;
    private int vida1 = 50, vida2 = 50;
    private boolean juegoActivo = true;
    private String direccion1 = "RIGHT", direccion2 = "LEFT";
    private ArrayList<Disparo> disparos = new ArrayList<>();

    private Image jugador1Imagen, jugador2Imagen;

    public VentanaJuego() {
        setTitle("Juego de Disparos con Vida y Ganador");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Cargar la imagen para ambos jugadores
        jugador1Imagen = new ImageIcon(getClass().getResource("/images/nave.png")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        jugador2Imagen = new ImageIcon(getClass().getResource("/images/nave.png")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);

        PanelJuego panelJuego = new PanelJuego();
        add(panelJuego);

        // Iniciar el "Game Loop" para actualizar disparos y redibujar el juego constantemente
        Timer timer = new Timer(30, e -> {
            if (juegoActivo) {
                actualizarDisparos();
                panelJuego.repaint();
            }
        });
        timer.start();

        // Agregar el KeyListener para capturar las teclas
        addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!juegoActivo) return;

                int key = e.getKeyCode();

                // Movimiento del Jugador 1
                if (key == KeyEvent.VK_W && y1 - velocidad >= 0) { y1 -= velocidad; direccion1 = "UP"; }
                else if (key == KeyEvent.VK_S && y1 + velocidad + 50 <= getHeight()) { y1 += velocidad; direccion1 = "DOWN"; }
                else if (key == KeyEvent.VK_A && x1 - velocidad >= 0) { x1 -= velocidad; direccion1 = "LEFT"; }
                else if (key == KeyEvent.VK_D && x1 + velocidad + 50 <= getWidth()) { x1 += velocidad; direccion1 = "RIGHT"; }
                else if (key == KeyEvent.VK_F) { disparos.add(new Disparo(x1 + 25, y1 + 25, direccion1, 1)); }

                // Movimiento del Jugador 2
                if (key == KeyEvent.VK_UP && y2 - velocidad >= 0) { y2 -= velocidad; direccion2 = "UP"; }
                else if (key == KeyEvent.VK_DOWN && y2 + velocidad + 50 <= getHeight()) { y2 += velocidad; direccion2 = "DOWN"; }
                else if (key == KeyEvent.VK_LEFT && x2 - velocidad >= 0) { x2 -= velocidad; direccion2 = "LEFT"; }
                else if (key == KeyEvent.VK_RIGHT && x2 + velocidad + 50 <= getWidth()) { x2 += velocidad; direccion2 = "RIGHT"; }
                else if (key == KeyEvent.VK_M) { disparos.add(new Disparo(x2 + 25, y2 + 25, direccion2, 2)); }
                
                panelJuego.repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {}
            @Override
            public void keyTyped(KeyEvent e) {}
        });

        setVisible(true);
    }

    // Método para actualizar la posición de los disparos y manejar las colisiones
    private void actualizarDisparos() {
        Iterator<Disparo> iterador = disparos.iterator();
        while (iterador.hasNext()) {
            Disparo disparo = iterador.next();
            disparo.mover();

            // Verificar colisiones con los jugadores
            if (disparo.jugador == 1 && disparo.colisiona(x2, y2)) {
                vida2 -= 10;
                iterador.remove();
            } else if (disparo.jugador == 2 && disparo.colisiona(x1, y1)) {
                vida1 -= 10;
                iterador.remove();
            }

            // Remover disparos que se salen de los límites de la ventana
            if (disparo.x < 0 || disparo.x > getWidth() || disparo.y < 0 || disparo.y > getHeight()) {
                iterador.remove();
            }
        }

        // Verificar si hay un ganador
        if (vida1 <= 0) {
            juegoActivo = false;
            mostrarGanador("¡Jugador 2 Gana!");
        } else if (vida2 <= 0) {
            juegoActivo = false;
            mostrarGanador("¡Jugador 1 Gana!");
        }
    }

    // Clase interna para el panel donde se dibujan los objetos
    private class PanelJuego extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (!juegoActivo) return;

            // Fondo del panel
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());

            // Dibujar el Jugador 1 con la imagen
            g.drawImage(jugador1Imagen, x1, y1, this);
            g.setColor(Color.BLACK);
            g.drawString("Vida: " + vida1, x1, y1 - 10);

            // Dibujar el Jugador 2 con la imagen
            g.drawImage(jugador2Imagen, x2, y2, this);
            g.drawString("Vida: " + vida2, x2, y2 - 10);

            // Dibujar los disparos
            g.setColor(Color.ORANGE);
            for (Disparo disparo : disparos) {
                g.fillOval(disparo.x, disparo.y, 10, 10);
            }
        }
    }

    // Método para mostrar el ganador y preguntar si se desea jugar de nuevo
    private void mostrarGanador(String mensaje) {
        int opcion = JOptionPane.showConfirmDialog(this, mensaje + " ¿Desea jugar de nuevo?", "Fin del juego", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            reiniciarJuego();
        } else {
            System.exit(0);
        }
    }

    // Método para reiniciar el juego
    private void reiniciarJuego() {
        x1 = 150; y1 = 200;
        x2 = 400; y2 = 200;
        vida1 = 50;
        vida2 = 50;
        disparos.clear();
        juegoActivo = true;
        repaint();
    }

    // Clase interna para representar un disparo
    private class Disparo {
        int x, y;
        int velocidad = 5;
        String direccion;
        int jugador;

        public Disparo(int x, int y, String direccion, int jugador) {
            this.x = x;
            this.y = y;
            this.direccion = direccion;
            this.jugador = jugador;
        }

        public void mover() {
            switch (direccion) {
                case "UP": y -= velocidad; break;
                case "DOWN": y += velocidad; break;
                case "LEFT": x -= velocidad; break;
                case "RIGHT": x += velocidad; break;
            }
        }

        // Verificar colisión con un jugador
        public boolean colisiona(int objX, int objY) {
            return x >= objX && x <= objX + 50 && y >= objY && y <= objY + 50;
        }
    }

    public static void main(String[] args) {
        new VentanaJuego();
    }
}
