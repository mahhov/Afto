package shapes;

import engine.Math3D;
import shapes.drawelement.Line;
import shapes.drawelement.Surface;

import java.awt.*;

public class StaticCube extends Shape {
	private double x, y, z;
	private Surface top, bottom, left, right, front, back;
	Line[] line;
	
	public StaticCube(double x, double y, double z, Color color, boolean side[], ShapeParent shapeParent) {
		this(x, y, z, color, side, .5, shapeParent);
	}
	
	public StaticCube(double x, double y, double z, Color color, boolean side[], double size, ShapeParent shapeParent) {
		super(shapeParent);
		this.x = x;
		this.y = y;
		this.z = z;
		
		if (color == null)
			color = Color.LIGHT_GRAY;
		if (side == null) {
			side = new boolean[6];
			for (int i = 0; i < side.length; i++)
				side[i] = true;
		}
		
		initGeom(color, side, size);
	}
	
	void initGeom(Color color, boolean[] side, double size) {
		// dimensions
		double topZ = z + size;
		double bottomZ = z - size;
		double leftX = x - size;
		double rightX = x + size;
		double frontY = y - size;
		double backY = y + size;
		
		// side coordinates
		double[] xs = new double[] {leftX, rightX, rightX, leftX};
		double[] ys = new double[] {backY, backY, frontY, frontY};
		double[] zs = new double[] {bottomZ, topZ, topZ, bottomZ};
		
		// from back/left -> back/right -> front/right -> front/left
		if (side[Math3D.TOP]) {
			top = new Surface(xs, ys, topZ, true);
			top.setColor(color);
			top.setLight(1);
		}
		if (side[Math3D.BOTTOM]) {
			bottom = new Surface(xs, ys, bottomZ, false);
			bottom.setColor(color);
			bottom.setLight(1);
		}
		
		// from bottom/back -> top/back -> top/front -> bottom/front
		if (side[Math3D.LEFT]) {
			left = new Surface(leftX, ys, zs, true);
			left.setColor(color);
			left.setLight(1);
		}
		if (side[Math3D.RIGHT]) {
			right = new Surface(rightX, ys, zs, false);
			right.setColor(color);
			right.setLight(1);
		}
		
		xs = new double[] {leftX, leftX, rightX, rightX};
		// from left/bottom -> left/top -> right/top -> right/bottom
		if (side[Math3D.FRONT]) {
			front = new Surface(xs, frontY, zs, true);
			front.setColor(color);
			front.setLight(1);
		}
		if (side[Math3D.BACK]) {
			back = new Surface(xs, backY, zs, false);
			back.setColor(color);
			back.setLight(1);
		}
		
		// lines
		line = new Line[12];
		if (side[Math3D.BACK] && side[Math3D.BOTTOM])
			line[0] = new Line(new double[][] {{leftX, backY, bottomZ}, {rightX, backY, bottomZ}});
		if (side[Math3D.LEFT] && side[Math3D.BOTTOM])
			line[1] = new Line(new double[][] {{leftX, backY, bottomZ}, {leftX, frontY, bottomZ}});
		if (side[Math3D.LEFT] && side[Math3D.BACK])
			line[2] = new Line(new double[][] {{leftX, backY, bottomZ}, {leftX, backY, topZ}});
		
		if (side[Math3D.FRONT] && side[Math3D.BOTTOM])
			line[3] = new Line(new double[][] {{rightX, frontY, bottomZ}, {leftX, frontY, bottomZ}});
		if (side[Math3D.RIGHT] && side[Math3D.BOTTOM])
			line[4] = new Line(new double[][] {{rightX, frontY, bottomZ}, {rightX, backY, bottomZ}});
		if (side[Math3D.RIGHT] && side[Math3D.FRONT])
			line[5] = new Line(new double[][] {{rightX, frontY, bottomZ}, {rightX, frontY, topZ}});
		
		if (side[Math3D.BACK] && side[Math3D.TOP])
			line[6] = new Line(new double[][] {{leftX, backY, topZ}, {rightX, backY, topZ}});
		if (side[Math3D.LEFT] && side[Math3D.TOP])
			line[7] = new Line(new double[][] {{leftX, backY, topZ}, {leftX, frontY, topZ}});
		if (side[Math3D.LEFT] && side[Math3D.BACK])
			line[8] = new Line(new double[][] {{leftX, backY, topZ}, {leftX, backY, bottomZ}});
		
		if (side[Math3D.BACK] && side[Math3D.TOP])
			line[9] = new Line(new double[][] {{rightX, frontY, topZ}, {leftX, frontY, topZ}});
		if (side[Math3D.RIGHT] && side[Math3D.TOP])
			line[10] = new Line(new double[][] {{rightX, frontY, topZ}, {rightX, backY, topZ}});
		if (side[Math3D.RIGHT] && side[Math3D.FRONT])
			line[11] = new Line(new double[][] {{rightX, frontY, topZ}, {rightX, frontY, bottomZ}});
	}
	
	Surface[] getSurfaces(int xSide, int ySide, int zSide) {
		Surface xSurface = xSide == Math3D.LEFT ? left : (xSide == Math3D.RIGHT ? right : null);
		Surface ySurface = ySide == Math3D.FRONT ? front : (ySide == Math3D.BACK ? back : null);
		Surface zSurface = zSide == Math3D.BOTTOM ? bottom : (zSide == Math3D.TOP ? top : null);
		return new Surface[] {xSurface, ySurface, zSurface};
	}
	
	public Line[] drawLines() {
		return line;
	}
}

// todo: make more efficeint in hiding surfaces until world modified to make them visible