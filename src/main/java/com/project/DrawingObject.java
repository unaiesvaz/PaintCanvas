package com.project;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class DrawingObject { //Clase padre para los objetos al dibujar, rectangulos, circulos, etc.

    private final int id;
    private final Color lineColor;
    private final Color fillColor;
    private final double lineWidth;

    public DrawingObject(int id, Color lineColor, Color fillColor, double lineWidth) {
        this.id = id;
        this.lineColor = lineColor;
        this.fillColor = fillColor;
        this.lineWidth = lineWidth;
    }

    public int getId() {
        return id;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public double getLineWidth() {
        return lineWidth;
    }

    public abstract String getTypeName();

    public abstract void draw(GraphicsContext gc);

    public abstract Rectangle2D getBounds();

    public abstract void move(double dx, double dy);

    protected Rectangle2D createBounds(
            double minX, double minY,
            double maxX, double maxY) {

        double margin = lineWidth / 2 + 4;

        return new Rectangle2D(
                minX - margin,
                minY - margin,
                maxX - minX + margin * 2,
                maxY - minY + margin * 2);
    }

    @Override
    public String toString() {
        return getTypeName() + " " + id;
    }
}