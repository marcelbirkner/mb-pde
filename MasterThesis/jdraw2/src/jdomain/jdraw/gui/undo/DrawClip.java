package jdomain.jdraw.gui.undo;

import java.awt.Rectangle;
import java.util.ArrayList;

import jdomain.jdraw.data.Clip;
import jdomain.jdraw.data.Pixel;
import jdomain.jdraw.gui.TextTool;
import jdomain.jdraw.gui.ToolPanel;

/*
 * DrawPixel.java - created on 30.10.2003
 * 
 * @author Michaela Behling
 */

public final class DrawClip extends Undoable {

	private final DrawPixel drawPixel;

	public DrawClip(int x, int y, Clip clip) {
		drawPixel = getPixels(clip, x, y);
	}

	private DrawPixel getPixels(Clip clip, final int startx, final int starty) {
		final int w = clip.getWidth();
		final int h = clip.getHeight();
		ArrayList pixels = new ArrayList();
		if ( ToolPanel.INSTANCE.getCurrentTool() instanceof TextTool ) {
			clip = TextTool.INSTANCE.createClip(startx,starty,w,h);
		}
		Rectangle r = frame.getBounds();
		final int trans = clip.getTransparentColour();
		for (int y = starty; y < starty + h; y++) {
			for (int x = startx; x < startx + w; x++) {
				if (r.contains(x, y)) {
					int newCol = clip.getPixel(x - startx, y - starty);
					if (newCol != trans) {
						pixels.add(new Pixel(x, y, frame.getPixel(x, y), newCol));
					}
				}
			}
		}

		Pixel[] pixelField = new Pixel[pixels.size()];
		pixels.toArray(pixelField);
		return new DrawPixel(pixelField);
	}

	public boolean isValid() {
		return drawPixel.isValid();
	}

	public void redo() {
		drawPixel.redo();
	}

	public void undo() {
		drawPixel.undo();
	}

}
