package editor;

import engine.Math3D;
import paint.painterelement.PainterPolygon;
import paint.painterelement.PainterQueue;

import java.awt.*;
import java.io.Serializable;

//todo: clean up
class ScreenEditorMap extends ScreenTable implements ImageProvider, Serializable {
	private static final int RIGHT_FACE = 0, FRONT_FACE = 1, TOP_FACE = 2;
	private static final int[][][] FACE_COLOR = new int[][][] {
			{{60, 150, 200}, {80, 170, 220}, {100, 190, 240}}, // block==1
			{{200, 150, 60}, {220, 170, 80}, {240, 190, 100}}, // preview
	}; // [block][face][rgb]
	
	private Blueprint blueprint;
	private int preview[][][], previewCounter;
	private boolean alpha;
	
	private int scrollX, scrollY, zoom;
	private int mapHalfShowWidthDefault, mapHalfShowLengthDefault;
	private int mapHalfShowWidth, mapHalfShowLength;
	private final double blockXShift, blockYShift;
	private final double blockWidthDefault, blockHeightDefault;
	
	int startX, startY, endX, endY;
	double blockWidth, blockHeight;
	
	ScreenEditorMap(int mapWidth, int mapLength, int mapHeight) {
		super(mapWidth, mapLength);
		
		mapHalfShowWidthDefault = 4;
		mapHalfShowLengthDefault = 4;
		blueprint = new Blueprint(mapWidth, mapLength, mapHeight);
		preview = new int[blueprint.width][blueprint.length][blueprint.height];
		previewCounter = 0;
		
		blockWidthDefault = 1.0 / blueprint.width;
		blockHeightDefault = 1.0 / blueprint.length;
		blockXShift = .005;
		blockYShift = .015;
		
		zoom = 1;
		scroll(0, 0, 0);
	}
	
	void setPosition(double left, double top, double width, double height) {
		super.setPosition(left, top, width, height);
		scroll(0, 0, 0);
	}
	
	void updateMap(boolean[][] select, boolean[][] vertSelect, int value) {
		for (int z = 0; z < vertSelect[0].length; z++)
			if (vertSelect[0][z])
				for (int x = 0; x < select.length; x++)
					for (int y = 0; y < select[0].length; y++)
						if (select[x][y])
							blueprint.blueprint[x][y][vertSelect[0].length - z - 1][0] = value;
	}
	
	void updatePreviewMap(boolean[][] select, boolean[][] vertSelect) {
		previewCounter++;
		for (int z = 0; z < vertSelect[0].length; z++)
			for (int x = 0; x < select.length; x++)
				for (int y = 0; y < select[0].length; y++)
					if (select[x][y] && vertSelect[0][z])
						preview[x][y][vertSelect[0].length - z - 1] = previewCounter;
	}
	
	void setAlpha(boolean value) {
		alpha = value;
	}
	
	public void provideImage(PainterQueue painterQueue, double left, double top, double width, double height) {
		drawContents(painterQueue, left, top, width, height, 0, 0, blueprint.width, blueprint.length, blockWidthDefault, blockHeightDefault);
	}
	
	private boolean[][] getShadow(int startX, int startY, int endX, int endY) {
		boolean[][] shadow = new boolean[blueprint.width][blueprint.length];
		for (int x = (int) startX; x < endX; x++)
			for (int y = (int) startY; y < endY; y++)
				if (blueprint.blueprint[x][y][0][0] == Blueprint.EMPTY) {
					int z = 1;
					while (z < blueprint.height && blueprint.blueprint[x][y][z][0] == Blueprint.EMPTY)
						z++;
					if (z < blueprint.height)
						shadow[x][y] = true;
				}
		return shadow;
	}
	
	private void drawContents(PainterQueue painterQueue, double left, double top, double width, double height, int startX, int startY, int endX, int endY, double blockWidth, double blockHeight) {
		boolean[][] shadow = getShadow(startX, startY, endX, endY);
		double offX, leftBottomX, leftTopX, rightBottomX, rightTopX, offY, backBottomY, backTopY, frontBottomY, frontTopY;
		int block, topZ;
		double[][] rightxy = null, frontxy = null, topxy = null, bottomxy = null;
		int alphaAmount = alpha ? 40 : 255;
		
		for (int z = 0; z < blueprint.height; z++)
			for (int x = (int) startX; x < endX; x++)
				for (int y = (int) startY; y < endY; y++)
					if (blueprint.blueprint[x][y][z][0] == 1 || preview[x][y][z] == previewCounter) {
						topZ = z + 1;
						
						// x
						offX = x - startX;
						leftBottomX = (offX * blockWidth - z * blockXShift) * width - .5 + left;
						leftTopX = (offX * blockWidth - topZ * blockXShift) * width - .5 + left;
						rightBottomX = leftBottomX + blockWidth * width;
						rightTopX = leftTopX + blockWidth * width;
						
						// y
						offY = y - startY;
						backBottomY = (offY * blockHeight - z * blockYShift) * height - .5 + top;
						backTopY = (offY * blockHeight - topZ * blockYShift) * height - .5 + top;
						frontBottomY = backBottomY + blockHeight * height;
						frontTopY = backTopY + blockHeight * height;
						
						block = blueprint.blueprint[x][y][z][0] == Blueprint.BLOCK ? 0 : 1;
						
						// fill
						
						// right face
						if (blueprint.isEmpty(x + 1, y, z, startX, endX, startY, endY) || alpha) {
							rightxy = new double[][] {{rightTopX, rightBottomX, rightBottomX, rightTopX}, {backTopY, backBottomY, frontBottomY, frontTopY}};
							painterQueue.add(new PainterPolygon(rightxy, 1, createColor(block, RIGHT_FACE, alphaAmount), false));
						}
						
						// front face
						if (blueprint.isEmpty(x, y + 1, z, startX, endX, startY, endY) || alpha) {
							frontxy = new double[][] {{leftTopX, rightTopX, rightBottomX, leftBottomX}, {frontTopY, frontTopY, frontBottomY, frontBottomY}};
							painterQueue.add(new PainterPolygon(frontxy, 1, createColor(block, FRONT_FACE, alphaAmount), false));
						}
						
						// top face
						if (blueprint.isEmpty(x, y, z + 1, startX, endX, startY, endY) || alpha) {
							topxy = new double[][] {{leftTopX, rightTopX, rightTopX, leftTopX}, {backTopY, backTopY, frontTopY, frontTopY}};
							painterQueue.add(new PainterPolygon(topxy, 1, createColor(block, TOP_FACE, alphaAmount), false));
						}
						
						// outline
						
						// right face
						if (rightxy != null)
							painterQueue.add(new PainterPolygon(rightxy, 1, null, true));
						
						// front face
						if (frontxy != null)
							painterQueue.add(new PainterPolygon(frontxy, 1, null, true));
						
						// top face
						if (topxy != null)
							painterQueue.add(new PainterPolygon(topxy, 1, null, true));
						
					} else if (z == 0 && shadow[x][y]) {
						// shadow 
						offX = x - startX;
						offY = y - startY;
						leftBottomX = (offX * blockWidth) * width - .5 + left;
						rightBottomX = leftBottomX + blockWidth * width;
						backBottomY = (offY * blockHeight) * height - .5 + top;
						frontBottomY = backBottomY + blockHeight * height;
						
						bottomxy = new double[][] {{leftBottomX, rightBottomX, rightBottomX, leftBottomX}, {backBottomY, backBottomY, frontBottomY, frontBottomY}};
						painterQueue.add(new PainterPolygon(bottomxy, 1, new Color(100, 100, 100, alphaAmount), false));
					}
	}
	
	private Color createColor(int block, int face, int alpha) {
		return new Color(FACE_COLOR[block][face][0], FACE_COLOR[block][face][1], FACE_COLOR[block][face][2], alpha);
	}
	
	void scroll(int dx, int dy, int dz) {
		zoom = Math3D.maxMin(zoom + dz, 10, 1);
		mapHalfShowWidth = mapHalfShowWidthDefault * zoom;
		mapHalfShowLength = mapHalfShowLengthDefault * zoom;
		scrollX = Math3D.maxMin(scrollX + dx * zoom, blueprint.width - mapHalfShowWidth, mapHalfShowWidth);
		scrollY = Math3D.maxMin(scrollY + dy * zoom, blueprint.length - mapHalfShowLength, mapHalfShowLength);
		
		startX = scrollX - mapHalfShowWidth;
		startY = scrollY - mapHalfShowLength;
		endX = scrollX + mapHalfShowWidth;
		endY = scrollY + mapHalfShowLength;
		blockWidth = .5 / mapHalfShowWidth;
		blockHeight = .5 / mapHalfShowLength;
		
		columnWidth = blockWidth * width;
		rowHeight = blockHeight * height;
	}
	
	boolean handleMouseInput(double screenX, double screenY, int mouseState, char charInput, int charState) {
		return super.handleMouseInput(screenX, screenY, mouseState, charInput, charState);
	}
	
	int[] itemToGridCoord(double[] xy) {
		return new int[] {(int) (xy[0] * mapHalfShowWidth * 2) + startX, (int) (xy[1] * mapHalfShowLength * 2) + startY};
	}
	
	double[] gridToScreenCoord(int gridx, int gridy) {
		return new double[] {left + columnWidth * (gridx - startX), top + rowHeight * (gridy - startY)};
	}
	
	void draw(PainterQueue painterQueue) {
		drawGrid(painterQueue, startX, startY, endX, endY);
		drawContents(painterQueue, left, top, width, height, startX, startY, endX, endY, blockWidth, blockHeight);
	}
	
	void storeSave() {
		blueprint.save(blueprint.DEFAULT_PATH);
	}
	
	void storeLoad() {
		blueprint = Blueprint.load(Blueprint.DEFAULT_PATH);
	}
}
