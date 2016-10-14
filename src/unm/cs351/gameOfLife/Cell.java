package unm.cs351.gameOfLife;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;

/**
 * A class the represents a single living or dying cell. It holds all
 * cell-specific info
 * 
 * @author Nicholas Spurlock
 * 
 */

public class Cell
{
  private static final double MAX_DIM = 5.0;
  private static final double MIN_DIM = 0.0;
  private static final double TRANSPARENCY = 1.0;

  public static final Color DEAD_RED = new Color(Color.FIREBRICK.getRed(),
      Color.FIREBRICK.getGreen(), Color.FIREBRICK.getBlue(), TRANSPARENCY);
  public static final Color ALIVE_GREEN = new Color(Color.DARKGREEN.getRed(),
      Color.DARKGREEN.getGreen(), Color.DARKGREEN.getBlue(), TRANSPARENCY);

  double aLimitR = ALIVE_GREEN.getRed();
  double aLimitG = ALIVE_GREEN.getGreen();

  double dLimitR = DEAD_RED.getRed();
  double dLimitG = DEAD_RED.getGreen();

  final private Box cell;
  private PhongMaterial material;

  private boolean isAlive;
  private boolean stateChanged;

  private final double x;
  private final double y;
  private final double z;

  /**
   * Creates a new Sphere cell with initial position (x,y,z) and radius
   * 
   * @param x
   * @param y
   * @param z
   * @param isAlive
   *          weather the cell is currently alive or dead
   */
  public Cell(double x, double y, double z, boolean isAlive)
  {

    this.x = x;
    this.y = y;
    this.z = z;

    stateChanged = true;

    material = new PhongMaterial();

    this.isAlive = isAlive;
    if (isAlive)
    {
      cell = new Box(MAX_DIM, MAX_DIM, MAX_DIM);

      material.setSpecularColor(ALIVE_GREEN);
      material.setDiffuseColor(ALIVE_GREEN);
      material.setSpecularPower(128);
    }
    else
    {
      cell = new Box(MIN_DIM, MIN_DIM, MIN_DIM);
      material.setSpecularColor(DEAD_RED);
      material.setDiffuseColor(DEAD_RED);
    }

    cell.setMaterial(material);
    cell.setCullFace(CullFace.BACK);

  }

  public static double getMaxDimension()
  {
    return MAX_DIM;
  }

  public double getX()
  {
    return this.x;
  }

  public double getY()
  {
    return this.y;
  }

  public double getZ()
  {
    return this.z;
  }

  public Box getCell()
  {
    return this.cell;
  }

  public boolean getAlive()
  {
    return this.isAlive;
  }

  public boolean getStateChanged()
  {
    return this.stateChanged;
  }

  public void setAlive(boolean isAlive)
  {
    this.isAlive = isAlive;
  }

  public void setStateChanged(boolean changed)
  {
    this.stateChanged = changed;
  }

  /**
   * Updates the physical properties of the cell, its radius and color. It
   * updates by a fractional amount so that a full cycle is completed every
   * second.
   * 
   * @param ratio
   *          Used to determine the change in attributes. Usually 1/60
   * @param isAlive
   *          Weather the cell is currently alive or dying
   */

  public void update(double ratio, boolean isAlive)
  {

    // double aLimitR = ALIVE_GREEN.getRed();
    // double aLimitG = ALIVE_GREEN.getGreen();
    //
    // double dLimitR = DEAD_RED.getRed();
    // double dLimitG = DEAD_RED.getGreen();

    double r = material.getDiffuseColor().getRed();
    double g = material.getDiffuseColor().getGreen();
    double b = material.getDiffuseColor().getBlue();

    double rDiff = Math.abs(aLimitR - dLimitR);
    double gDiff = Math.abs(aLimitG - dLimitG);

    if (isAlive)
    {
      if (cell.getHeight() + MAX_DIM * ratio <= MAX_DIM)
      {
        double dimChange = cell.getHeight() + MAX_DIM * ratio;
        cell.setHeight(dimChange);
        cell.setWidth(dimChange);
        cell.setDepth(dimChange);

        if (r - rDiff * ratio >= aLimitR) // If another step towards the
        { // intended color is possible,
          // increment our current color in that direction
          r -= rDiff * ratio;
        }
        if (g + gDiff * ratio <= aLimitG)
        {
          g += gDiff * ratio;
        }

        material.setDiffuseColor(new Color(r, g, b, TRANSPARENCY));
        material.setSpecularColor(new Color(r, g, b, TRANSPARENCY));
      }
      else // We're near our max radius, make sure the size and color is
           // correct
      {
        cell.setHeight(MAX_DIM);
        cell.setWidth(MAX_DIM);
        cell.setDepth(MAX_DIM);
        material.setSpecularColor(ALIVE_GREEN);
        material.setDiffuseColor(ALIVE_GREEN);
      }
    }
    else
    {
        cell.setHeight(MIN_DIM);
        cell.setWidth(MIN_DIM);
        cell.setDepth(MIN_DIM);
        material.setSpecularColor(DEAD_RED);
        material.setDiffuseColor(DEAD_RED);
 }
  }
}
