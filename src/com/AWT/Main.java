package com.AWT;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;

class Surface extends JPanel {

    // основыне объекты-переменные
    private Rectangle2D rect;
    private Ellipse2D ellipse;
    private float alpha_rectangle;
    private float alpha_ellipse;

    public Surface() { // конструктор
        initSurface(); // инициализация
    }

    private void initSurface() { // инициализация

        addMouseListener(new HitTestAdapter()); // добавить наше событие на мышку

        rect = new Rectangle2D.Float(20f, 20f, 80f, 50f); // создать объекты
        ellipse = new Ellipse2D.Float(120f, 30f, 60f, 60f);

        alpha_rectangle = 1f;
        alpha_ellipse = 1f;
    }

    private void doDrawing(Graphics g) { // отрисовка

        Graphics2D g2d = (Graphics2D) g.create(); // графика

        g2d.setPaint(new Color(50, 50, 50)); // цвет

        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        rh.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY); // рендеринг

        g2d.setRenderingHints(rh);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                alpha_rectangle));
        g2d.fill(rect); // прорисовка квадрата

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                alpha_ellipse));
        g2d.fill(ellipse); // и круга

        g2d.dispose();
    }

    @Override
    public void paintComponent(Graphics g) { // прорисовка компонента
        super.paintComponent(g);
        doDrawing(g);
    }

    class RectRunnable implements Runnable { // поток для квадрата

        private Thread runner; // создать поток

        public RectRunnable() { // конструтор
            initThread();
        }

        private void initThread() { // инициализация
            runner = new Thread(this); // запуск потока
            runner.start();
        }

        @Override
        public void run() { // сам поток
            while (alpha_rectangle >= 0) {
                repaint(); // перерисовать
                alpha_rectangle += -0.01f;

                if (alpha_rectangle < 0) {
                    alpha_rectangle = 0;
                }
                try {
                    Thread.sleep(50); // уснуть, это что бы исчез не сразу
                } catch (InterruptedException ex) {
                    Logger.getLogger(Surface.class.getName()).log(Level.SEVERE,
                            null, ex);
                }
            }
        }
    }

    class HitTestAdapter extends MouseAdapter implements Runnable {

        private RectRunnable rectAnimator;
        private Thread ellipseAnimator;

        @Override
        public void mousePressed(MouseEvent e) { // нажатие на мышку

            int x = e.getX();
            int y = e.getY();

            if (rect.contains(x, y)) { // совпал с квадратом?
                rectAnimator = new RectRunnable(); // запустить поток
            }

            if (ellipse.contains(x, y)) { // совпал с кругом?
                ellipseAnimator = new Thread(this);
                ellipseAnimator.start(); // запустить поток
            }
        }

        @Override
        public void run() { // поток круга
            while (alpha_ellipse >= 0) {
                repaint(); // прорисовать
                alpha_ellipse += -0.01f;

                if (alpha_ellipse < 0) {
                    alpha_ellipse = 0;
                }

                try {
                    Thread.sleep(50);  // уснуть, это что бы исчез не сразу
                } catch (InterruptedException ex) {
                    Logger.getLogger(Surface.class.getName()).log(Level.SEVERE,
                            null, ex);
                }
            }
        }
    }
}

public class Main extends JFrame {

    public Main() { // конструктор

        add(new Surface()); // наш компонент

        setTitle("Hit testing"); // заголовок
        setSize(250, 150); // размеры
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // выход
        setLocationRelativeTo(null); // по центру
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() { // поток

            @Override
            public void run() { // запуск
                Main ex = new Main(); // создание нашего класса
                ex.setVisible(true); // видимость
            }
        });
    }
}
