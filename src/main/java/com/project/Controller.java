package com.project;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.geometry.Rectangle2D;

public class Controller implements Initializable {

    @FXML private Canvas canvas;
    @FXML private Canvas colorsCanvas;

    @FXML private ComboBox<String> toolComboBox;
    @FXML private ColorPicker lineColorPicker;
    @FXML private ColorPicker fillColorPicker;
    @FXML private Slider lineWidthSlider;

    @FXML private ListView<DrawingObject> objectsListView;
    @FXML private Button deleteButton;

    private GraphicsContext gc;
    private ColorPalette colorPalette;

    private final ObservableList<DrawingObject> objects = FXCollections.observableArrayList();

    private int nextObjectId = 1;

    private DrawingObject currentObject;

    private boolean movingObject = false;

    private double lastMouseX;
    private double lastMouseY;

    private double startX;
    private double startY;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        gc = canvas.getGraphicsContext2D();

        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);

        // Selector de herramientas
        toolComboBox.getItems().addAll(
            "Traç lliure",
            "Línia",
            "Rectangle"
        );

        toolComboBox.getSelectionModel().selectFirst();

        // Colores iniciales
        lineColorPicker.setValue(Color.BLUE);
        fillColorPicker.setValue(Color.WHITE);

        // Paleta de colores predefinidos
        colorPalette = new ColorPalette(
            colorsCanvas,
            color -> lineColorPicker.setValue(color)
        );

        // Sincronizar el ColorPicker con la paleta
        lineColorPicker.valueProperty().addListener(
            (observable, oldColor, newColor) ->
                colorPalette.setSelectedColor(newColor)
        );

        // Lista de objetos
        objectsListView.setItems(objects);

        deleteButton.setDisable(true);

        objectsListView.getSelectionModel()
            .selectedItemProperty()
            .addListener((observable, oldObject, newObject) -> {
                deleteButton.setDisable(newObject == null);
                redraw();
            });

        // Al elegir una herramienta, volvemos al modo de dibujo
        toolComboBox.valueProperty().addListener(
            (observable, oldTool, newTool) ->
                objectsListView.getSelectionModel().clearSelection()
        );

        deleteButton.setOnAction(event -> deleteSelectedObject());

        // Eventos del lienzo
        canvas.setOnMousePressed(this::onMousePressed);
        canvas.setOnMouseDragged(this::onMouseDragged);
        canvas.setOnMouseReleased(this::onMouseReleased);

        redraw();
    }

    private double getSelectedWidth() {
        return Math.max(1, lineWidthSlider.getValue());
    }

    private void onMousePressed(MouseEvent event) {

        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        DrawingObject selected = objectsListView.getSelectionModel().getSelectedItem();

        if (selected != null) {

            if (selected.getBounds().contains(event.getX(), event.getY())) {
                movingObject = true;

                lastMouseX = event.getX();
                lastMouseY = event.getY();
            }

        return;
        }

            DrawingObject clickedObject = findObjectAt(event.getX(), event.getY());

        if (clickedObject != null) {

            objectsListView.getSelectionModel().select(clickedObject);

            movingObject = true;
            lastMouseX = event.getX();
            lastMouseY = event.getY();

            redraw();
            return;
        }


        startX = event.getX();
        startY = event.getY();

        
        String tool = toolComboBox.getValue();

        if (tool == null) {
            return;
        }

        Color lineColor = lineColorPicker.getValue();
        Color fillColor = fillColorPicker.getValue();
        double width = getSelectedWidth();

        switch (tool) {
            case "Traç lliure" ->
                currentObject = new Stroke(
                    nextObjectId++,
                    lineColor,
                    width
                );

            case "Línia" ->
                currentObject = new LineObject(
                    nextObjectId++,
                    startX, startY,
                    startX, startY,
                    lineColor,
                    width
                );

            case "Rectangle" ->
                currentObject = new RectangleObject(
                    nextObjectId++,
                    startX, startY,
                    startX, startY,
                    lineColor,
                    fillColor,
                    width
                );
        }

        updateCurrentObject(startX, startY);
        redraw();
    }

    private void onMouseDragged(MouseEvent event) {

        if (movingObject) {

            DrawingObject selected = objectsListView.getSelectionModel().getSelectedItem();

            if (selected != null) {

                double dx = event.getX() - lastMouseX;
                double dy = event.getY() - lastMouseY;

                selected.move(dx, dy);

                lastMouseX = event.getX();
                lastMouseY = event.getY();

                redraw();
            }

            return;
        }


        if (currentObject == null) {
            return;
        }

        updateCurrentObject(event.getX(), event.getY());
        redraw();
    }

    private void onMouseReleased(MouseEvent event) {

        if (movingObject) {
            movingObject = false;
            redraw();
            return;
        }

        if (currentObject == null) {
            return;
        }

        updateCurrentObject(event.getX(), event.getY());

        objects.add(currentObject);

        currentObject = null;

        redraw();
    }

    private void updateCurrentObject(double x, double y) {

        if (currentObject instanceof Stroke stroke) {
            stroke.addPoint(x, y);

        } else if (currentObject instanceof LineObject line) {
            line.setEnd(x, y);

        } else if (currentObject instanceof RectangleObject rectangle) {
            rectangle.updateFromPoints(
                startX, startY,
                x, y
            );
        }
    }

    private void deleteSelectedObject() {

        DrawingObject selected =
                objectsListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            return;
        }

        objects.remove(selected);
        objectsListView.getSelectionModel().clearSelection();

        redraw();
    }

    private void redraw() {

        gc.setFill(Color.WHITE);
        gc.fillRect(
            0, 0,
            canvas.getWidth(),
            canvas.getHeight()
        );

        // Dibujar los objetos guardados en orden de creación
        for (DrawingObject object : objects) {
            object.draw(gc);
        }

        // Previsualización del objeto actual
        if (currentObject != null) {
            currentObject.draw(gc);
        }

        DrawingObject selected = objectsListView.getSelectionModel().getSelectedItem();

        if (selected != null) {
            Rectangle2D bounds = selected.getBounds();

            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.setLineDashes(6, 4);

            gc.strokeRect(
                bounds.getMinX(),
                bounds.getMinY(),
                bounds.getWidth(),
                bounds.getHeight()
            );

            // Restaurar el trazado continuo
            gc.setLineDashes();
        }
    }

    private DrawingObject findObjectAt(double x, double y) {

        // Revisar primero los objetos más recientes
        for (int i = objects.size() - 1; i >= 0; i--) {

            DrawingObject object = objects.get(i);

            if (object.getBounds().contains(x, y)) {
                return object;
            }
        }

        return null;
        }
}