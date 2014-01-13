/**
 * 
 */
package core.gui.panes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

/**
 * 
 * 
 * @author Chris Hartley
 */
public class DNAInspectorPanel extends JPanel  implements ComponentListener,
														  MouseListener,
														  MouseMotionListener,
														  MouseWheelListener
{

	private static final long serialVersionUID = 252780500041247156L;

	private BufferedImage buffer;
	private Object sequenceData = null;
	private boolean isUpdated = false;
	
	private final Cursor HAND_CURSOR =
			Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	
	private final Cursor MOVE_CURSOR =
			Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
	
	private ExpandViewBox evBox = new ExpandViewBox();
	
	private static final int EXPAND_VIEW_HEIGHT = 64;
	private static final int EXPAND_LINE_HEIGHT = 24;
	private static final int EXPANDED_VIEW_HEIGHT = 192;
	
	
	public DNAInspectorPanel() {
		super(null);
		
		int height = EXPAND_VIEW_HEIGHT
				   + EXPAND_LINE_HEIGHT
				   + EXPANDED_VIEW_HEIGHT;
		
		Dimension dim = new Dimension(0, height);
		setMinimumSize(dim);
		setMaximumSize(dim);
		setPreferredSize(dim);
		
		addComponentListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		
		setCursor(HAND_CURSOR);
	}
	
	public final void loadDNA(Object seqData) {
		this.sequenceData = seqData;
		refresh();
	}
	
	private final void refresh() {
	   int w = getWidth() - 1;
	   int h = getHeight() - 1;
	   buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	   
	   Graphics2D g2d = buffer.createGraphics();
	   g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
			   RenderingHints.VALUE_ANTIALIAS_ON );
	   g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
	   g2d.setRenderingHint( RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY );
	   
	   if (sequenceData != null) {
		   drawFullGenome(g2d, 0, 0, w, EXPAND_VIEW_HEIGHT);
		   drawExpandedGenome(g2d, EXPAND_VIEW_HEIGHT + EXPAND_LINE_HEIGHT, 0, w, EXPANDED_VIEW_HEIGHT);
	   }
	   
	   evBox.setHeight(EXPAND_VIEW_HEIGHT);
	   evBox.setWidth(w / 10);
	   
	   isUpdated = true;
	   repaint();
	}
	
	private final void drawFullGenome(Graphics2D g2d, int top, int left, int width, int height) {
		Color frmLineColor = getBackground().darker();
		final int pad = 3;
		int x = top + pad;
		int y = left + pad;
		width -= pad + pad;
		height -= pad + pad; 
		
		g2d.setColor(frmLineColor);
		for (int i = 0; i < 4; i++)
			g2d.drawLine(x, y + i * 2, width, y + i * 2);
	}
	
	private final void drawExpandedGenome(Graphics2D g2d, int top, int left, int width, int height) {
		final Color fontColor = getForeground();
		final Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 9);
		final int pad = 3;
		int x = left + pad;
		int y = top + pad;
		width -= pad + pad;
		height -= pad + pad;
		
		FontMetrics fm = g2d.getFontMetrics(font);
		Rectangle2D box = fm.getStringBounds("stop", g2d); //font.getMaxCharBounds(g2d.getFontRenderContext());
		box.setFrame(x, y, box.getWidth() + pad + pad, box.getHeight() + pad + pad);
		
		y = (int) (top + height - box.getHeight() * 10 - pad);
		g2d.setColor(fontColor);
		g2d.setFont(font);
		
		g2d.drawString("Frame A", x, y + fm.getAscent() + pad);
		y += box.getHeight();
		
		g2d.drawString("Frame B", x, y + fm.getAscent() + pad);
		y += box.getHeight();
		
		g2d.drawString("Frame C", x, y + fm.getAscent() + pad);
		y += box.getHeight();
		
		g2d.drawString("mRNA(3')", x, y + fm.getAscent() + pad);
		y += box.getHeight();
		
		g2d.drawString("3'", x, y + fm.getAscent() + pad);
		y += box.getHeight();
		
		g2d.drawString("5'", x, y + fm.getAscent() + pad);
		y += box.getHeight();
		
		g2d.drawString("mRNA(5')", x, y + fm.getAscent() + pad);
		y += box.getHeight();
		
		g2d.drawString("Frame A", x, y + fm.getAscent() + pad);
		y += box.getHeight();
		
		g2d.drawString("Frame B", x, y + fm.getAscent() + pad);
		y += box.getHeight();
		
		g2d.drawString("Frame C", x, y + fm.getAscent() + pad);
		y += box.getHeight();
	}
	
	public void paintComponent(Graphics g) {
		if (isUpdated) {
			Graphics2D g2d = (Graphics2D)g;
			g2d.clearRect(0, 0, getWidth(), getHeight());
			g2d.drawImage(buffer, 0, 0, this);
			evBox.paint(g2d);
		}
		else
			refresh();
		
		g.dispose();
	}
	
	private boolean isDragging = false;

	@Override
	public void componentHidden(ComponentEvent ce) { }

	@Override
	public void componentMoved(ComponentEvent ce) {
		isUpdated = false;
		repaint();
	}

	@Override
	public void componentResized(ComponentEvent ce) {
		isUpdated = false;
		repaint();
	}

	@Override
	public void componentShown(ComponentEvent ce) {
		isUpdated = false;
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent me) {
		beginMoving(me.getPoint());
	}

	@Override
	public void mouseMoved(MouseEvent me) { }



	@Override
	public void mouseWheelMoved(MouseWheelEvent mwe) {
		int wheelClicks = mwe.getWheelRotation();
		if (!isDragging)
			 evBox.scrollBy(wheelClicks);
	}

	@Override
	public void mouseClicked(MouseEvent me) { }

	@Override
	public void mouseEntered(MouseEvent me) {
		evBox.setActive(true);
	}

	@Override
	public void mouseExited(MouseEvent me) {
		if (!isDragging)
			evBox.setActive(false);
	}

	@Override
	public void mousePressed(MouseEvent me) {
		beginMoving(me.getPoint());
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		Point pt = me.getPoint();
		endMoving(pt);
		
		if (!contains(pt))
			evBox.setActive(false);
	}
	
	private synchronized final void beginMoving(Point pt) {
		if (!isDragging) {
			isDragging = true;
			setCursor(MOVE_CURSOR);
		}
		evBox.setPositionBy(pt);
	}
	
	private synchronized final void endMoving(Point pt) {
		if (isDragging) {
			isDragging = false;
			setCursor(HAND_CURSOR);
		}
		evBox.setPositionBy(pt);
	}
	
	
	public class ExpandViewBox {
		
		protected int x;
		protected int y;
		
		private int height;
		private int width;
		
		private Color highlightColor, highlightColorNormal, highlightColorActive;
		private Color highlightBorderColor, highlightBorderColorNormal, highlightBorderColorActive;
		
		private final Stroke penBorder = new BasicStroke(1.5f);
		
		public ExpandViewBox() {
			initialize();
		}

		private final void initialize() {
			UIDefaults defaults = UIManager.getLookAndFeelDefaults();
			Color bg = defaults.getColor("List.selectionBackground");
			highlightColorActive = new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 64);
			highlightBorderColorActive = bg = bg.darker();
			
			highlightColorNormal = new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 64);
			highlightBorderColorNormal = bg.darker();
			
			highlightColor = highlightColorNormal;
			highlightBorderColor = highlightBorderColorNormal;
			
			x = y = 0;
			height = 64;
			width = 24;
		}
		
		public final void scrollBy(int scrollCLicks) {
			setX(x + scrollCLicks);
		}
		
		public void setPositionBy(Point pt) {
			setX(pt.x - width / 2);
		}
		
		public final void setX(int newX) {
			if (x == newX)
				return;
			
			int maxX = DNAInspectorPanel.this.getWidth() - width - 1;
			
			if (newX < 0)
				newX = 0;
			else if (newX > maxX)
				newX = maxX;
			
			x = newX;
			repaint();
		}
		
		public synchronized final void setActive(boolean active) {
			if (active) {
				highlightColor = highlightColorActive;
				highlightBorderColor = highlightBorderColorActive;
			}
			else {
				highlightColor = highlightColorNormal;
				highlightBorderColor = highlightBorderColorNormal;
			}
			repaint();
		}
		
		public final void setWidth(int width) {
			this.width = width;
		}
		
		public final void setHeight(double height) {
			setHeight((int)height);
		}
		public final void setHeight(float height) {
			setHeight((int)height);
		}
		public final void setHeight(int height) {
			this.height = height;
		}
		
		public void paint(Graphics2D g2d) {
			g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
	            RenderingHints.VALUE_ANTIALIAS_ON );
	      g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
	            RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
	      g2d.setRenderingHint( RenderingHints.KEY_RENDERING,
	            RenderingHints.VALUE_RENDER_QUALITY );

			int maxX = DNAInspectorPanel.this.getWidth() - 1;
			int maxY = height + DNAInspectorPanel.EXPAND_LINE_HEIGHT;
			
			FontMetrics fm = g2d.getFontMetrics(g2d.getFont());
			Rectangle2D box = fm.getStringBounds("mRNA(5')", g2d);
			int labelWidth = (int) (6 + box.getWidth());
			
			Polygon pgLeft = new Polygon();
			pgLeft.addPoint(0, maxY);
			pgLeft.addPoint(x, height);
			pgLeft.addPoint(labelWidth, maxY);
			
			Polygon pgRight = new Polygon();
			pgRight.addPoint(x + width, height);
			pgRight.addPoint(maxX, maxY);
			pgRight.addPoint(maxX - labelWidth, maxY);
			
			g2d.setColor(highlightColor);
			g2d.fillRect(x, y, width, height);
			g2d.fill(pgLeft);
			g2d.fill(pgRight);
			
			g2d.setStroke(penBorder);
			g2d.setColor(highlightBorderColor);
			g2d.drawRect(x, y, width, height);
			
			g2d.draw(pgLeft);
			g2d.draw(pgRight);

			//g2d.drawLine(0, maxY, x, height);
			//g2d.drawLine(x + width, height, maxX, maxY);
		}
		
	}
}
