import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;

public class Main extends Application {

    private ImageView bird;
    private double velocity = 0;
    private final double gravity = 0.9;
    private final double jumpStrength = -8;

    private final int pipeGap = 150;
    private final int pipeWidth = 60;
    private boolean gameOver = false;

    private final ArrayList<ImageView> pipes = new ArrayList<>();
    private AnimationTimer timer;

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, 800, 800);

        Image bgImage = new Image(getClass().getResource("/resource/background.png").toExternalForm());
        ImageView background = new ImageView(bgImage);
        background.setFitWidth(800);
        background.setFitHeight(800);
        root.getChildren().add(background);

        Image birdImage = new Image(getClass().getResource("/resource/bird.png").toExternalForm());
        bird = new ImageView(birdImage);
        bird.setFitWidth(40);
        bird.setFitHeight(40);
        bird.setX(100);
        bird.setY(300);
        root.getChildren().add(bird);

        timer = new AnimationTimer() {
            long lastPipeTime = 0;

            @Override
            public void handle(long now) {
                if (gameOver)
                    return;

                velocity += gravity;
                bird.setY(bird.getY() + velocity);

                Iterator<ImageView> iter = pipes.iterator();
                while (iter.hasNext()) {
                    ImageView pipe = iter.next();
                    pipe.setX(pipe.getX() - 3);

                    if (pipe.getBoundsInParent().intersects(bird.getBoundsInParent())) {
                        stopGame();
                    }

                    if (pipe.getX() + pipeWidth < 0) {
                        root.getChildren().remove(pipe);
                        iter.remove();
                    }
                }

                if (now - lastPipeTime > 2_000_000_000L) {
                    addPipes(root);
                    lastPipeTime = now;
                }

                if (bird.getY() < 0 || bird.getY() > 600) {
                    stopGame();
                }
            }
        };
        timer.start();

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case SPACE:
                    if (!gameOver)
                        velocity = jumpStrength;
                    break;
                case R:
                    if (gameOver)
                        restartGame(root, background);
                    break;
            }
        });

        stage.setScene(scene);
        stage.setTitle("Flappy Bird FX");
        stage.show();
    }

    private void addPipes(Pane root) {
        Image pipeImage = new Image(getClass().getResource("/resource/pipe.png").toExternalForm());
        double centerY = 100 + Math.random() * 300;

        ImageView topPipe = new ImageView(pipeImage);
        topPipe.setFitWidth(pipeWidth);
        topPipe.setFitHeight(centerY - pipeGap / 2);
        topPipe.setX(800);
        topPipe.setY(0);
        // Flip vertically
        topPipe.setScaleY(-1);

        ImageView bottomPipe = new ImageView(pipeImage);
        bottomPipe.setFitWidth(pipeWidth);
        bottomPipe.setFitHeight(800 - (centerY + pipeGap / 2));
        bottomPipe.setX(800);
        bottomPipe.setY(centerY + pipeGap / 2);

        pipes.add(topPipe);
        pipes.add(bottomPipe);
        root.getChildren().addAll(topPipe, bottomPipe);
    }

    private void stopGame() {
        gameOver = true;
        timer.stop();
        System.out.println("Game Over! Press R to restart.");
    }

    private void restartGame(Pane root, ImageView background) {
        root.getChildren().clear();
        pipes.clear();

        velocity = 0;
        bird.setX(100);
        bird.setY(300);
        gameOver = false;

        root.getChildren().add(background);
        root.getChildren().add(bird);
        timer.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
