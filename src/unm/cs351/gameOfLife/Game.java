package unm.cs351.gameOfLife;

import java.util.LinkedList;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.Group;

/**
 * This class handles all of the game logic, including both animation timers and
 * their associated updates. It represents the model or state of the game.
 * 
 * @author Nicholas Spurlock
 *
 */

public class Game extends Application
{

  private static final double CAMERA_INITIAL_DISTANCE = -450;
  private static final double CAMERA_INITIAL_X_ANGLE = 70.0;
  private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
  private static final double CAMERA_NEAR_CLIP = 0.1;
  private static final double CAMERA_FAR_CLIP = 10000.0;
  private static final double MOUSE_SPEED = 0.1;
  private static final double ROTATION_SPEED = 2.0;
  private static final double TRACK_SPEED = 0.3;

  private final double FRAME_RATIO = 1.0 / 60.0;

  private final MyAnimationTimer timer = new MyAnimationTimer();
  private final MyRotationTimer rotationTimer = new MyRotationTimer();
  private final PerspectiveCamera camera = new PerspectiveCamera(true);

  private double aliveDeadRatio = 50;
  
  private final Xform cameraXform = new Xform();
  private final Xform cameraXform2 = new Xform();
  private final Xform cameraXform3 = new Xform();

  private GameOfLifeController controller;

  private Xform gridWorld; // The group the cell grid is associated with

  private CellGrid grid;

  private Pane root;
  private SubScene subScene; // The scene we use to draw the grid

  private boolean isPaused = true;
  private boolean isRotating = false; // Is the grid rotating or not
  private int frame = 0;
  private int seed = 0;

  private double mouseOldX;
  private double mouseOldY;
  private double mousePosX;
  private double mousePosY;
  private double mouseDeltaX;
  private double mouseDeltaY;

  /**
   * Simple game timer that updates the cellGrid each frame and updates the
   * state of the board every second
   * 
   * @author Nicholas Spurlock
   *
   */

  private class MyAnimationTimer extends AnimationTimer
  {
    @Override
    public void handle(long now)
    {
      if (!isPaused)
      {

        int period = frame % 60;
        frame++;
        if (period == 0)
        {
          grid.update();
        }

        drawNewGrid(FRAME_RATIO, period);
      }
    }

  }

  /**
   * A separate timer for the grid rotation that can be started and stopped when
   * the grid is being interacted with
   * 
   * @author Nicholas Spurlock
   *
   */
  private class MyRotationTimer extends AnimationTimer
  {

    @Override
    public void handle(long now)
    {

      if (!isPaused)
      {
        cameraXform.rx.setAngle(cameraXform.rx.getAngle() + 0.25);
        cameraXform.ry.setAngle(cameraXform.rx.getAngle() + 0.25);
        cameraXform.rz.setAngle(cameraXform.rx.getAngle() + 0.25);

      }
    }

  }

  public void setAliveDeadRatio(double ratio)
  {
    this.aliveDeadRatio = ratio;
  }
  
  public void setSeed(int seed)
  {
    this.seed = seed;
  }

  /**
   * Handles the behavior of the Start/Pause/Play button's interaction with the
   * state of the game
   * 
   * @return Whether the games is currently paused
   */
  public boolean setPlayPause()
  {
    if (frame == 0)
    {
      timer.start();
      rotationTimer.start();      
      isPaused = false;
      isRotating = true;
    }
    else if (isPaused)
    {
      isPaused = false;
    }
    else
    {
      isPaused = true;
    }
    return isPaused;
  }

  /**
   * Pauses the game if it's not currently paused
   */
  public void setPause()
  {
    if (!isPaused)
    {
      isPaused = true;
    }
  }
  
  /**
   * Pauses the rotation animation timer
   */
  public void pauseRotation()
  {
    if (isRotating)
    {
      rotationTimer.stop();
    }
  }

  /**
   * Toggles the rotation timer on or off
   * 
   * @return Whether the grid is currently rotating
   */
  public boolean setRotationTime()
  {
    if (!isPaused)
    {
      if (isRotating)
      {
        rotationTimer.stop();
        isRotating = false;
      }
      else
      {
        rotationTimer.start();
        isRotating = true;
      }
    }
    return isRotating;
  }

  /**
   * Handles the mouse drag and rotates or otherwise moves the grid
   * 
   * @param me
   *          The mouse event
   */
  public void setMouseDragPosition(MouseEvent me)
  {

    mouseOldX = mousePosX;
    mouseOldY = mousePosY;
    mousePosX = me.getSceneX();
    mousePosY = me.getSceneY();
    mouseDeltaX = (mousePosX - mouseOldX);
    mouseDeltaY = (mousePosY - mouseOldY);

    double modifier = 1.0;
    double modifierFactor = 0.4;

    if (me.isPrimaryButtonDown())
    {
      cameraXform.ry.setAngle(cameraXform.ry.getAngle()
          - mouseDeltaX * modifierFactor * modifier * ROTATION_SPEED);
      cameraXform.rx.setAngle(cameraXform.rx.getAngle()
          + mouseDeltaY * modifierFactor * modifier * ROTATION_SPEED);
    }
    else if (me.isSecondaryButtonDown())
    {
      double z = camera.getTranslateZ();
      double newZ = z + mouseDeltaX * MOUSE_SPEED * modifier;
      camera.setTranslateZ(newZ);
    }
    else if (me.isMiddleButtonDown())
    {
      cameraXform2.t.setX(cameraXform2.t.getX()
          + mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED);
      cameraXform2.t.setY(cameraXform2.t.getY()
          + mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);
    }
  }

  /**
   * Handles the mouse scroll wheel even to zoom the grid in or out
   * 
   * @param se
   */
  public void handleMouseScroll(ScrollEvent se)
  {
    if (se.getDeltaY() > 0)
    {
      gridWorld.setScaleX(gridWorld.getScaleX() * 1.1);
      gridWorld.setScaleY(gridWorld.getScaleY() * 1.1);
      gridWorld.setScaleZ(gridWorld.getScaleZ() * 1.1);
    }
    else
    {
      gridWorld.setScaleX(gridWorld.getScaleX() * 0.9);
      gridWorld.setScaleY(gridWorld.getScaleY() * 0.9);
      gridWorld.setScaleZ(gridWorld.getScaleZ() * 0.9);
    }

  }

  /**
   * Restarts the games by rebuild the grid and pulling new initial values
   */
  public void restart(String preset)
  {
    if (frame != 0)
    {
      isPaused = true;
    }

    gridWorld.getChildren().clear();
    buildGrid(controller.getRValues(), preset);
    drawNewGrid(FRAME_RATIO, 1);
    setDefaultCameraPosition();
  }

  /**
   * Builds the grid of displayed cells
   * 
   * @param rValues
   */
  public void buildGrid(int[] rValues, String preset)
  {
    int maxDim = controller.getMaxDim();
    grid = new CellGrid(rValues, maxDim, seed, aliveDeadRatio, preset);
    Cell[][][] cells = grid.getCellGrid();
    LinkedList<Group> cellAddList = new LinkedList<>();

    for (int i = 1; i < maxDim - 1; i++)
    {
      for (int j = 1; j < maxDim - 1; j++)
      {
        for (int k = 1; k < maxDim - 1; k++)
        {
          Cell cell = cells[i][j][k];
          Xform cellXform = new Xform();
          cellXform.getChildren().add(cell.getCell());
          cellXform.setTranslate(cell.getX(), cell.getY(), cell.getZ());
          cellAddList.add(cellXform);
        }

      }
    }
    gridWorld.getChildren().addAll(cellAddList);
  }

  @Override
  public void start(Stage stage)
  {
    try
    {

      FXMLLoader loader = new FXMLLoader(getClass().getResource("GoLGUI.fxml"));
      this.root = (Pane) loader.load();
      this.controller = loader.getController();
      this.subScene = controller.getSubScene();

      controller.setGame(this);
      gridWorld = new Xform();

      buildCamera();
      buildGrid(controller.getRValues(), null);
      setDefaultCameraPosition();

      subScene.setFill(Color.GRAY);
      subScene.setRoot(gridWorld);
      subScene.setCamera(camera);

      stage.setTitle("Game of Life");
      stage.setScene(new Scene(root, 1000, 700));
      stage.show();

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public static void main(String[] args)
  {
    launch(args);
  }

  /**
   * Updates each cell each frame, if new period then we add a point light for a
   * frame as the "spark of life"
   * 
   * @param ratio
   *          The frame ratio we are using, generally 1/60
   * @param period
   *          If 0 then this is a new period
   */
  private void drawNewGrid(double ratio, int period)
  {
    Cell[][][] cells = grid.getCellGrid();
    int maxDim = controller.getMaxDim();
    for (int i = 1; i < maxDim - 1; i++)
    {
      for (int j = 1; j < maxDim - 1; j++)
      {
        for (int k = 1; k < maxDim - 1; k++)
        {
          if (cells[i][j][k].getStateChanged())
          {
            Cell c = cells[i][j][k];
            c.update(ratio, c.getAlive());
          }
        }
      }
    }
  }

  /**
   * Sets a good starting camera position
   */
  private void setDefaultCameraPosition()
  {
    double n = controller.getMaxDim() - 2;
    double d = Cell.getMaxDimension();
    double o = grid.getShiftAmount();
    double camPos = ((n * d + (n - 1) * o) / 2);

    cameraXform.rx.setAngle(0.0);
    cameraXform.ry.setAngle(0.0);
    cameraXform.rz.setAngle(0.0);
    cameraXform.setTranslate(camPos, camPos, camPos);
    cameraXform.setScale(3.0);
  }

  private void buildCamera()
  {

    cameraXform.getChildren().add(cameraXform2);
    cameraXform2.getChildren().add(cameraXform3);
    cameraXform3.getChildren().add(camera);
    cameraXform3.setRotateZ(180.0);
    camera.setNearClip(CAMERA_NEAR_CLIP);
    camera.setFarClip(CAMERA_FAR_CLIP);
    camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
    cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
    cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
  }
}
