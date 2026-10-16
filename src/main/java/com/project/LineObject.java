package com.project;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class LineObject extends DrawingObject { // Esta clase es para guardar los objetos que sean una linea recta (Objecto a elegir en el programa)
    //Por lo tanto, unicamente necesitaremos guardar el inicio y el final de la linea y luego unir los puntos 
    private double startX;
    private double startY;
    private double endX;
    private double endY;

    public LineObject(int id, double startX, double startY,
                      double endX, double endY,
                      Color lineColor, double lineWidth) {

        super(id, lineColor, Color.TRANSPARENT, lineWidth);

        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    @Override
    public String getTypeName() {
        return "Línia";
    }

    public void setEnd(double x, double y) {
        this.endX = x;
        this.endY = y;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(getLineColor());
        gc.setLineWidth(getLineWidth());

        gc.strokeLine(startX, startY, endX, endY);
    }

    @Override
    public Rectangle2D getBounds() {
        double minX = Math.min(startX, endX);
        double minY = Math.min(startY, endY);
        double maxX = Math.max(startX, endX);
        double maxY = Math.max(startY, endY);

        return createBounds(minX, minY, maxX, maxY);
    }

    @Override
    public void move(double dx, double dy) {
        startX += dx;
        startY += dy;

        endX += dx;
        endY += dy;
    }
}