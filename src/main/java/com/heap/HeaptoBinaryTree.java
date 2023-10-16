package com.heap;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;

public class HeaptoBinaryTree extends Application {

    private static Scene scene;

    public class SVGIcon extends Region {

        String path;
        SVGPath content = new SVGPath();

        public SVGIcon(String styleClass, String path, double width, double height) {
            this.setMinSize(width, height);
            this.setPrefSize(width, height);
            this.setMaxSize(width, height);
            this.content.setContent(path);
            this.setShape(content);
            this.getStyleClass().add(styleClass);
        }

        public SVGIcon(String id, String styleClass, String path, double width, double height) {
            this(styleClass, path, width, height);
            this.setId(id);
        }
    }

    public class SVGButton extends Button {

        public SVGButton(SVGIcon icon, double width, double height) {
            this.setMinSize(width, height);
            this.setPrefSize(width, height);
            this.setMaxSize(width, height);
            this.setGraphic(icon);
        }

        public SVGButton(String styleClass, SVGIcon icon, double width, double height) {
            this(icon, width, height);
            this.getStyleClass().add(styleClass);
        }

        public SVGButton(String id, String styleClass, SVGIcon icon, double width, double height) {
            this(styleClass, icon, width, height);
            this.setId(id);
        }

        public void setIcon(SVGIcon icon) {
            this.setGraphic(icon);
        }
    }

    public class UndecoratedTitleBar extends BorderPane {

        Stage primaryStage;
        StageStyle stageStyle;
        double posX, posY;
        WindowButtons windowButtons;
        WindowTitle windowTitle;

        public UndecoratedTitleBar(String id, Stage primaryStage, WindowTitle windowTitle,
                WindowButtons windowButtons) {
            this(id, primaryStage, StageStyle.UNDECORATED, windowTitle, windowButtons);
        }

        public UndecoratedTitleBar(String id, Stage primaryStage, StageStyle stageStyle, WindowTitle windowTitle,
                WindowButtons windowButtons) {
            this.primaryStage = primaryStage;
            this.windowButtons = windowButtons;
            this.windowTitle = windowTitle;

            primaryStage.initStyle(stageStyle);
            this.setLeft(windowTitle);
            this.setRight(windowButtons);
            this.enableDraggableEvents();
            this.setId(id);
        }

        public void enableDraggableEvents() {
            this.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (mouseEvent.isPrimaryButtonDown()) {
                        posX = primaryStage.getX() - mouseEvent.getScreenX();
                        posY = primaryStage.getY() - mouseEvent.getScreenY();
                    }
                }
            });
            this.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (mouseEvent.isPrimaryButtonDown()) {
                        primaryStage.setX(mouseEvent.getScreenX() + posX);
                        primaryStage.setY(mouseEvent.getScreenY() + posY);
                    }
                }
            });
        }
    }

    public class WindowButtons extends HBox {

        Stage primaryStage;
        SVGButton minimizeBtn, closeBtn;

        public WindowButtons(Stage primaryStage, SVGButton minimizeBtn, SVGButton closeBtn) {
            this.primaryStage = primaryStage;
            this.minimizeBtn = minimizeBtn;
            this.closeBtn = closeBtn;
            this.getChildren().addAll(minimizeBtn, closeBtn);
            this.enableClickableEvents();
        }

        public void enableClickableEvents() {
            minimizeBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    primaryStage.setIconified(true);
                }
            });
            closeBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    Platform.exit();
                }
            });
        }
    }

    public class WindowTitle extends HBox {

        public WindowTitle(String id, SVGIcon appIcon, Label appTitle) {
            this.getChildren().addAll(appIcon, appTitle);
            this.setId(id);
        }
    }

    public class Node {
        int value;
        Node left;
        Node right;

        public Node(int value) {
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }

    public class Window extends VBox {

        TextField input = new TextField();
        Button go = new Button("Convert");
        Label validLabel = new Label("Please enter a valid heap.");
        Node root;
        Canvas canvas = new Canvas();

        public Window() {

            HBox inputBox = new HBox(input, go);
            StackPane treePane = new StackPane();

            inputBox.setAlignment(Pos.CENTER);
            this.getChildren().addAll(validLabel, inputBox, treePane);
            this.setAlignment(Pos.TOP_CENTER);
            validLabel.setId("valid-label");
            go.setId("go");
            go.setText(go.getText().toUpperCase());
            setSize(input, 204, 30);
            setSize(go, 75, 28);
            canvas.setWidth(420);
            canvas.setHeight(600);

            go.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    handleInput();
                    if (isValidHeap(root)) {
                        validLabel.setText("Heap = " + Arrays.toString(heapToArray(root)));
                        drawBinaryTree(canvas.getGraphicsContext2D(), root, 200, 50, 100, 30);
                        treePane.getChildren().add(canvas);
                    } else {
                        validLabel.setText("This is not a valid heap.");
                    }
                }
            });
        }

        private Node arrayToHeap(int[] array, int index) {
            if (index < array.length) {
                Node node = new Node(array[index]);
                node.left = arrayToHeap(array, 2 * index + 1);
                node.right = arrayToHeap(array, 2 * index + 2);
                return node;
            }
            return null;
        }

        private int[] heapToArray(Node root) {
            List<Integer> list = new ArrayList<>();
            if (root != null) {
                Queue<Node> queue = new LinkedList<>();
                queue.add(root);
                while (!queue.isEmpty()) {
                    Node current = queue.poll();
                    list.add(current.value);
                    if (current.left != null) {
                        queue.add(current.left);
                    }
                    if (current.right != null) {
                        queue.add(current.right);
                    }
                }
            }
            return list.stream().mapToInt(Integer::intValue).toArray();
        }

        private boolean isValidHeap(Node node) {
            return isValidMinHeap(node) || isValidMaxHeap(node);
        }

        private boolean isValidMinHeap(Node node) {
            if (node == null) {
                return true; 
            }
            if (node.left != null && node.left.value < node.value) {
                return false;
            }
            if (node.right != null && node.right.value < node.value) {
                return false;
            }
            return (isValidMinHeap(node.left) && isValidMinHeap(node.right));
        }
 
        private boolean isValidMaxHeap(Node node) {
            if (node == null) {
                return true; 
            }
            if (node.left != null && node.left.value > node.value) {
                return false;
            }
            if (node.right != null && node.right.value > node.value) {
                return false;
            }
            return (isValidMaxHeap(node.left) && isValidMaxHeap(node.right));
        }

        private void setSize(Control control, double width, double height) {
            control.setMinSize(width, height);
            control.setMaxSize(width, height);
            control.setPrefSize(width, height);
        }

        private void handleInput() {
            String inputText = input.getText();
            root = parseInput(inputText);
        }

        private Node parseInput(String inputText) {
            String[] parts = inputText.split(",");
            int[] numbers = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                try {
                    numbers[i] = Integer.parseInt(parts[i].trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return arrayToHeap(numbers, 0);
        }



        private void drawBinaryTree(GraphicsContext gc, Node node, double x, double y, int xOffset, int yOffset) {
            Color nodeColor = Color.rgb(43, 42, 51);
            Color textColor = Color.rgb(208, 207, 219);

            int textXOffset = 10;
            int textYOffset = 20;
            int lineYOffset = 30;
            int nodeSize = 30;

            if (node == null) {
                return;
            }

            gc.setFill(nodeColor);
            gc.fillOval(x, y, nodeSize, nodeSize);
            gc.setFill(textColor);
            gc.fillText(String.valueOf(node.value), x + textXOffset, y + textYOffset);
            gc.setStroke(nodeColor);

            if (node.left != null) {
                double leftX = x - xOffset;
                double leftY = y + yOffset + lineYOffset;
                
                double parentBottomX = x + nodeSize / 2;
                double parentBottomY = y + nodeSize;

                double childTopX = leftX + nodeSize / 2;
                double childTopY = leftY;

                gc.strokeLine(parentBottomX, parentBottomY, childTopX, childTopY);
                drawBinaryTree(gc, node.left, leftX, leftY, xOffset / 2, yOffset);
            }

            if (node.right != null) {
                double rightX = x + xOffset;
                double rightY = y + yOffset + lineYOffset;

                double parentBottomX = x + nodeSize / 2;
                double parentBottomY = y + nodeSize;
        
                double childTopX = rightX + nodeSize / 2;
                double childTopY = rightY;
                
                gc.strokeLine(parentBottomX, parentBottomY, childTopX, childTopY);
                drawBinaryTree(gc, node.right, rightX, rightY, xOffset / 2, yOffset);
            }
        }
 
    
    }

    @Override
    public void start(Stage primaryStage) {

        String appPath = "M277.74 312.9c9.8-6.7 23.4-12.5 23.4-12.5s-38.7 7-77.2 10.2c-47.1 3.9-97.7 4.7-123.1 1.3-60.1-8 33-30.1 33-30.1s-36.1-2.4-80.6 19c-52.5 25.4 130 37 224.5 12.1zm-85.4-32.1c-19-42.7-83.1-80.2 0-145.8C296 53.2 242.84 0 242.84 0c21.5 84.5-75.6 110.1-110.7 162.6-23.9 35.9 11.7 74.4 60.2 118.2zm114.6-176.2c.1 0-175.2 43.8-91.5 140.2 24.7 28.4-6.5 54-6.5 54s62.7-32.4 33.9-72.9c-26.9-37.8-47.5-56.6 64.1-121.3zm-6.1 270.5a12.19 12.19 0 0 1-2 2.6c128.3-33.7 81.1-118.9 19.8-97.3a17.33 17.33 0 0 0-8.2 6.3 70.45 70.45 0 0 1 11-3c31-6.5 75.5 41.5-20.6 91.4zM348 437.4s14.5 11.9-15.9 21.2c-57.9 17.5-240.8 22.8-291.6.7-18.3-7.9 16-19 26.8-21.3 11.2-2.4 17.7-2 17.7-2-20.3-14.3-131.3 28.1-56.4 40.2C232.84 509.4 401 461.3 348 437.4zM124.44 396c-78.7 22 47.9 67.4 148.1 24.5a185.89 185.89 0 0 1-28.2-13.8c-44.7 8.5-65.4 9.1-106 4.5-33.5-3.8-13.9-15.2-13.9-15.2zm179.8 97.2c-78.7 14.8-175.8 13.1-233.3 3.6 0-.1 11.8 9.7 72.4 13.6 92.2 5.9 233.8-3.3 237.1-46.9 0 0-6.4 16.5-76.2 29.7zM260.64 353c-59.2 11.4-93.5 11.1-136.8 6.6-33.5-3.5-11.6-19.7-11.6-19.7-86.8 28.8 48.2 61.4 169.5 25.9a60.37 60.37 0 0 1-21.1-12.8z";
        SVGIcon appIcon = new SVGIcon("app-icon", null, appPath, 18, 18);
        Label appTitle = new Label("Heap to Binary Tree");
        appTitle.setId("app-title");
        WindowTitle windowTitle = new WindowTitle("window-title", appIcon, appTitle);

        String minimizePath = "M32 416c-17.7 0-32 14.3-32 32s14.3 32 32 32H480c17.7 0 32-14.3 32-32s-14.3-32-32-32H32z";
        SVGIcon minimizeIcon = new SVGIcon("window-btn-icon", minimizePath, 10, 2);

        String closePath = "M310.6 150.6c12.5-12.5 12.5-32.8 0-45.3s-32.8-12.5-45.3 0L160 210.7 54.6 105.4c-12.5-12.5-32.8-12.5-45.3 0s-12.5 32.8 0 45.3L114.7 256 9.4 361.4c-12.5 12.5-12.5 32.8 0 45.3s32.8 12.5 45.3 0L160 301.3 265.4 406.6c12.5 12.5 32.8 12.5 45.3 0s12.5-32.8 0-45.3L205.3 256 310.6 150.6z";
        SVGIcon closeIcon = new SVGIcon("window-btn-icon", closePath, 10, 10);

        SVGButton minimizeBtn = new SVGButton("window-btn", minimizeIcon, 46, 32);
        SVGButton closeBtn = new SVGButton("close-btn", "window-btn", closeIcon, 46, 32);

        WindowButtons windowButtons = new WindowButtons(primaryStage, minimizeBtn, closeBtn);

        UndecoratedTitleBar titleBar = new UndecoratedTitleBar("title-bar", primaryStage, windowTitle, windowButtons);

        Window window = new Window();

        BorderPane windowPane = new BorderPane();
        windowPane.setId("window-pane");
        windowPane.setTop(titleBar);
        windowPane.setCenter(window);

        scene = new Scene(windowPane, 600, 600);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("file:app-icon.png"));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}