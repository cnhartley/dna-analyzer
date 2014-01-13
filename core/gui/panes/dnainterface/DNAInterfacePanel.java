/**
 * 
 */
package core.gui.panes.dnainterface;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import core.molecules.AminoAcid;
import core.molecules.Nucleotide;

import core.process.AnalyzedDeoxyribonucleicAcid;

/**
 * 
 * @author Chris N. Hartley 
 *
 */
public class DNAInterfacePanel extends JPanel {

    private static final boolean SHOW_REFRESH_RATE = true;
    
    private static final long serialVersionUID = -1369148998273504455L;
    
    //private final Color fontColor = getForeground();
    private final static Font DEFAULT_FONT =
            new Font(Font.SANS_SERIF, Font.PLAIN, 9);
    
    private Color highlightColor, highlightColorNormal, highlightColorActive;
    private Color highlightBorderColor, highlightBorderColorNormal, highlightBorderColorActive;
    
    private final Stroke penBorder = new BasicStroke(1.5f);
    
    private final int pad = 3;
    
    private final Dimension requiredDim = new Dimension();
    
    private DNAFullPreviewPanel fullDNA;
    private ExpandViewPanel expandedDNA;
    private ExpandViewSelectionBox evBox;
    
    private AnalyzedDeoxyribonucleicAcid dna = null;
    
    
    /**
     * Constructor for a new instance of this DNA interface panel.
     */
    public DNAInterfacePanel() {
        super( new BorderLayout() );
        
        setOpaque(false);
        setFont(DEFAULT_FONT);
        
        requiredDim.height = 200;
        
        initialize();
        makeLayout();
    }

    
    /**
     * 
     * @param dna
     */
    public void loadDNA(AnalyzedDeoxyribonucleicAcid dna) {
        this.dna = dna;
        
        fullDNA.forceRepaint();
        expandedDNA.forceRepaint();
        
        System.out.println("Loading new DNA object to DNA Interface Panel...");
        repaint();
    }
    
    
    /**
     * 
     */
    private final void initialize() {
        UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        Color bg = defaults.getColor("List.selectionBackground");
        
        highlightColorActive =
                new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 64);
        highlightBorderColorActive = bg = bg.darker();
        
        highlightColorNormal =
                new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 64);
        highlightBorderColorNormal = bg.darker();
        
        highlightColor = highlightColorNormal;
        highlightBorderColor = highlightBorderColorNormal;
    }

    
    /**
     * 
     */
    private void makeLayout() {
        add( fullDNA = new DNAFullPreviewPanel(), BorderLayout.NORTH);
        
        add( new LabelPanel(SwingConstants.RIGHT), BorderLayout.WEST);
        add( new LabelPanel(SwingConstants.LEFT), BorderLayout.EAST);
        
        add( expandedDNA = new ExpandViewPanel(), BorderLayout.CENTER);
    }
    
    
    /**
     * Draws a region containing a simi-transparent background and label to
     * display the refresh rate, or time lapse of a refresh thread, to update
     * its buffered image which the <CODE>Graphics2D</CODE> specifies. This
     * time should be specified in milliseconds. This method preserves any
     * previous state of the <CODE>Graphics2D</CODE> object (e.g.
     * <CODE>Color</CODE>, <CODE>Stroke</CODE>, <CODE>Font</CODE>, etc.)
     * 
     * @param g <CODE>Graphics2D</CODE> to draw on to.
     * @param left <I>int</I> specifying the left coordinate to begin drawing at.
     * @param top  <I>int</I> specifying the top coordinate to begin drawing at.
     * @param refreshTime <I>long</I> refresh time in milliseconds.
     */
    protected static final void drawRefreshRate(Graphics2D g, int left, int top, long refreshTime) {
        final String msg = "[Refreshed in " + refreshTime + " ms]";
        FontMetrics fm = g.getFontMetrics();
        int y = top + fm.getAscent();
        Color origColor = g.getColor();
        
        Rectangle2D block = fm.getStringBounds(msg, g);
        block.setFrame(left, top, block.getWidth(), block.getHeight() + 1);
        g.setColor( new Color(255, 255, 255, 128) );
        g.fill(block);
        
        g.setColor( g.getColor().darker().darker() );
        g.drawString(msg, left, y);
        
        g.setColor(origColor);
    }


    /**
     * 
     * @author Chris N. Hartley
     */
    private class DNAFullPreviewPanel extends JComponent implements
            ComponentListener, MouseListener, MouseMotionListener,
            MouseWheelListener
    {

        private final Cursor HAND_CURSOR =
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        
        private final Cursor MOVE_CURSOR =
                Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
        
        private static final long serialVersionUID = 7490382155316324194L;
        private final Dimension requiredDim = new Dimension(0, 64);
        
        private BufferedImage buffer;
        private boolean isUpdated = false;
        private boolean isDragging = false;
        private RefreshWorker refresh;
      
        //private final ExpandViewSelectionBox evBox;
        
        
        /**
         * Constructor for the full view DNA preview panel.
         */
        public DNAFullPreviewPanel() {
            setOpaque(false);
            setMinimumSize(requiredDim);
            setMaximumSize(requiredDim);
            setPreferredSize(requiredDim);
            
            evBox = new ExpandViewSelectionBox(this);
            
            isUpdated = false;
            
            addComponentListener(this);
            addMouseListener(this);
            addMouseMotionListener(this);
            addMouseWheelListener(this);
        }
        
        
        /**
         * 
         */
        public final void forceRepaint() {
            isUpdated = false;
            repaint();
        }
        
        
        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D)g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            
            g2d.setFont( DNAFullPreviewPanel.this.getFont() );
            
            if (isUpdated) {
                g2d.drawImage(buffer, 0, 0, this);
                evBox.paint(g2d);
            }
            else {
                g2d.drawString("Loading...", 14, 14);
                
                if (refresh != null)
                    refresh.cancel(true);
                
                refresh = new RefreshWorker(getWidth() - 1, getHeight() - 1);
                refresh.execute();
            }
            g2d.dispose();
        }
        
        
        /**
         * 
         * @author Chris Hartley
         *
         */
        private class RefreshWorker extends SwingWorker<BufferedImage,Integer> {

            private int width, height;
            private final BufferedImage tmpBuffer;
            
            
            /**
             * Constructor for a new instance of the refresh worker thread with
             * the specified width and height for the buffered image to be
             * created.
             * 
             * @param width
             * @param height
             */
            public RefreshWorker(int width, int height) {
                tmpBuffer = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
                this.width = width;
                this.height = height;
            }
            
            
            @Override
            protected void done() {
                try { 
                    buffer = get();
                    isUpdated = true;
                    repaint();
                }
                catch (Exception ignore) { }
            }
            
            
            @Override
            protected BufferedImage doInBackground() throws Exception {
                long refreshTime = System.currentTimeMillis();
                Graphics2D g2d = getBufferGraphics();
                
                int x = pad;
                int y = pad;
                int pad2 = pad + pad;
                width -= pad2;
                height -= pad2;
                
                evBox.setHeight(height);
                evBox.setWidth(width / 10);
                
                if (dna != null) {
                    y = paintFramesForStrand(g2d, x, y, 0);
                    y = paintNucleotides(g2d, x, y);
                    y = paintFramesForStrand(g2d, x, y, 1);
                }
                
                if (SHOW_REFRESH_RATE) {
                    refreshTime = System.currentTimeMillis() - refreshTime;
                    drawRefreshRate(g2d, 14, 36, refreshTime);
                }
                g2d.dispose();
                
                return tmpBuffer;
            }
            
            
            /**
             * 
             * @param g
             * @param left
             * @param top
             * @return
             */
            private final int paintNucleotides(Graphics2D g, int left, int top) {
                double numOfNucleotides = dna.getSequenceLength();
                double pwidth = width / numOfNucleotides;
                int nh = height - 11 - pad - pad;
                
                Color origColor = g.getColor();
                
                int alpha = (int) (pwidth * 255);
                if (alpha == 0)
                    alpha = 1;
                else if (alpha > 255)
                    alpha = 128;
                g.setColor( new Color(0, 0, 0, alpha) );
                
                if (pwidth > 1)
                    paintNucleotideRectangles(g, left, top, pwidth);
                else
                    paintNucleotideLines(g, left, top, pwidth);
                
                g.setColor(origColor);
                top += nh + 1;
                return top;
            }
            
            
            /**
             * 
             * @param g
             * @param left
             * @param top
             * @param width
             */
            private final void paintNucleotideLines(Graphics2D g, int left, int top, double width) {
                Iterator<Nucleotide> itr = dna.getStrand(0).iterator();
                int y2 = height - 10;
                for (double p = left; itr.hasNext(); p += width) {
                    if (itr.next().key == 0) {
                        g.drawLine((int)p, top, (int)p, y2);
                    }
                }
            }
            
            
            /**
             * 
             * @param g
             * @param left
             * @param top
             * @param width
             */
            private final void paintNucleotideRectangles(Graphics2D g, int left, int top, double width) {
                Iterator<Nucleotide> itr = dna.getStrand(0).iterator();
                int nh = height - 11 - pad - pad;
                for (double p = left; itr.hasNext(); p += width) {
                    if (itr.next().key == 0) {
                        g.fillRect((int)p, top, (int)width, nh);
                    }
                }
            }
            
            
            /**
             * 
             * @param g
             * @param left
             * @param top
             * @param strand
             * @return
             */
            private final int paintFramesForStrand(Graphics2D g, int left, int top, int strand) {
                double numOfAminoAcid = (double) (dna.getSequenceLength() / AminoAcid.NUCLEOTIDES_PER_CODON);
                double pwidth = width / numOfAminoAcid;
                double x2;
                
                Color origColor = g.getColor();
                
                for (int f = 0; f < dna.getStrand(strand).getNumOfFrames(); f++) {
                    g.setColor(Color.LIGHT_GRAY);
                    g.drawLine(left, top, left + width, top);
                    
                    x2 = left;
                    for (AminoAcid aa : dna.getStrand(strand).getFrame(f)) {
                        if (aa == AminoAcid.START_CODON) {
                            g.setColor(Color.RED);
                            g.drawLine((int)x2, top, (int)(x2 + pwidth), top);
                        }
                        else if (aa == AminoAcid.STOP_CODON) {
                            g.setColor(Color.BLACK);
                            g.drawLine((int)x2, top, (int)(x2 + pwidth), top);
                        }
                        x2 += pwidth;
                    }
                    top += 2;
                }
                
                g.setColor(origColor);
                return top;
            }
            
            
            /**
             * 
             * @return
             */
            private final Graphics2D getBufferGraphics() {
                Graphics2D g2d = tmpBuffer.createGraphics();
                g2d.setColor( DNAFullPreviewPanel.this.getForeground() );
                g2d.setFont( DNAFullPreviewPanel.this.getFont() );
                g2d.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON );
                
                return g2d;
            }
            
        }
        
        
        @Override
        public void componentHidden(ComponentEvent ce) { }
        
        
        @Override
        public void componentMoved(ComponentEvent ce) {
            //isUpdated = false;
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
        
        
        /**
         * 
         * @param pt
         */
        private synchronized final void beginMoving(Point pt) {
            if (!isDragging) {
                isDragging = true;
                setCursor(MOVE_CURSOR);
            }
            evBox.setPositionBy(pt);
        }

        
        /**
         * 
         * @param pt
         */
        private synchronized final void endMoving(Point pt) {
            if (isDragging) {
                isDragging = false;
                setCursor(HAND_CURSOR);
            }
            evBox.setPositionBy(pt);
        }
        
    }
    
    
    /**
     * 
     * @author Chris Hartley
     *
     */
    public class ExpandViewSelectionBox implements ComponentListener {
        
        protected int x;
        protected int y;
        
        private int height;
        private int width;
        private int maxX;
        private int nucleotidesViewable = 0;
        
        private final JComponent parent;
        
        
        /**
         * 
         * @param parent
         */
        public ExpandViewSelectionBox(JComponent parent) {
            this.parent = parent;
            x = y = 0;
            height = 64;
            width = 24;
            parent.addComponentListener(this);
        }
        
        
        /**
         * 
         * @param scrollCLicks
         */
        public final void scrollBy(int scrollCLicks) {
            setX(x + scrollCLicks);
        }
        
        
        /**
         * 
         * @param pt
         */
        public void setPositionBy(Point pt) {
            setX(pt.x - width / 2);
        }
        
        
        /**
         * 
         * @param newX
         */
        public final void setX(int newX) {
            if (x == newX)
                return;
            
            int maxX = parent.getWidth() - width - 1;
            
            if (newX < 0)
                newX = 0;
            else if (newX > maxX)
                newX = maxX;
            
            x = newX;
            repaint();
        }
        
        
        /**
         * 
         * @param active
         */
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
        
        
        /**
         * 
         * @param width
         */
        public final void setWidth(int width) {
            this.width = width;
        }
        
        
        /**
         * 
         * @param height
         */
        public final void setHeight(double height) {
            setHeight((int)height);
        }

        
        /**
         * 
         * @param height
         */
        public final void setHeight(float height) {
            setHeight((int)height);
        }
        
        
        /**
         * 
         * @param height
         */
        public final void setHeight(int height) {
            this.height = height;
        }
        
        
        /**
         * 
         * @param g2d
         */
        public void paint(Graphics2D g2d) {
            g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING ,
                                  RenderingHints.VALUE_ANTIALIAS_ON );
            
            int maxY = parent.getHeight();
            
            //TODO int labelWidth = LabelPanel.this.requiredDim.width;
            int labelWidth = 60;
            
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
        }
        
        
        @Override
        public void componentHidden(ComponentEvent ce) { }
        
        
        @Override
        public void componentMoved(ComponentEvent ce) { }

        
        @Override
        public void componentResized(ComponentEvent ce) {
            double oldWidth = maxX;
            double newWidth = ce.getComponent().getWidth() - 1;
            double percent = newWidth / oldWidth;
            maxX = (int)newWidth;
            setX((int)(x * percent));
            updateWidth();
        }
        
        
        @Override
        public void componentShown(ComponentEvent ce) { }
        
        
        /**
         * 
         * @param nucleotidesViewable
         */
        public void setWidthByNumNucleotides(int nucleotidesViewable) {
            this.nucleotidesViewable = nucleotidesViewable;
            updateWidth();
        }
        
        
        /**
         * 
         */
        private final void updateWidth() {
            if (dna != null && nucleotidesViewable != 0) {
                double newWidth = dna.getSequenceLength() / nucleotidesViewable;
                setWidth((int) (parent.getWidth() * newWidth));
                parent.repaint();
            }
        }
        
    }
    
    
    /**
     * 
     * @author Chris Hartley
     *
     */
    private class ExpandViewPanel extends JComponent implements
            ComponentListener
    {
        
        private static final long serialVersionUID = -7521749571891341074L;
        
        private BufferedImage buffer;
        private boolean isUpdated = false;
        private int nucleotidesViewable = 0;
        
        private RefreshWorker refresh;
        
        
        /**
         * Constructor for a new instance of the expanded view of the DNA.
         */
        public ExpandViewPanel() {
            setOpaque(false);
            setMinimumSize( new Dimension(256, 0) );
            addComponentListener(this);
        }
        
        
        /**
         * 
         */
        public void forceRepaint() {
            isUpdated = false;
            repaint();
        }
        
        
        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D)g;
            g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON );
            g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
            g2d.setRenderingHint( RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY );
            
            g2d.setFont( DNAInterfacePanel.this.getFont() );
            
            if (isUpdated) {
                g2d.drawImage(buffer, 0, 0, this);
            }
            else {
                g2d.drawString("Loading...", 14, 14);
                
                if (refresh != null)
                    refresh.cancel(true);
                
                refresh = new RefreshWorker(getWidth() - 1, getHeight() - 1);
                refresh.execute();
            }
            g2d.dispose();
        }
        
        
        /**
         * 
         * @author Chris Hartley
         *
         */
        private class RefreshWorker extends SwingWorker<BufferedImage,Integer> {
            
            private int width, height;
            private final BufferedImage tmpBuffer;
            
            private Rectangle2D nucleotideBox = null;
            private FontMetrics fontMetrics = null;
            
            
            /**
             * Constructor for the refresh worker thread.
             * 
             * @param width
             * @param height
             */
            public RefreshWorker(int width, int height) {
                tmpBuffer = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
                this.width = width;
                this.height = height;
            }
            
            
            @Override
            protected void done() {
                try { 
                    buffer = get();
                    isUpdated = true;
                    repaint();
                }
                catch (Exception ignore) { }
            }
            
            
            @Override
            protected BufferedImage doInBackground() throws Exception {
                long refreshTime = System.currentTimeMillis();
                Graphics2D g2d = getBufferGraphics();
                
                int x = pad;
                int y = pad;
                int pad2 = pad + pad;
                width -= pad2;
                height -= pad2;
                
                if (dna != null) {
                    y = paintIndeciesForStrand(g2d, x, y, 0);
                    y = paintFramesForStrand(g2d, x, y, 0);
                    
                    //TODO draw mRNA of 3', template strand 0 of DNA...
                    
                    //TODO draw DNA template strand 0 nucleotides...
                    
                    //TODO draw DNA template strand 1 nucleotides...
                    
                    //TODO draw mRNA of 5', template strand 1 of DNA...
                    
                    y = (int) (height + pad2 - 4 * nucleotideBox.getHeight() - 1);
                    y = paintFramesForStrand(g2d, x, y, 1);
                }
                
                if (SHOW_REFRESH_RATE) {
                    refreshTime = System.currentTimeMillis() - refreshTime;
                    drawRefreshRate(g2d, 14, height + pad - fontMetrics.getHeight(), refreshTime);
                }
                g2d.dispose();
                
                return tmpBuffer;
            }
            
            
            /**
             * 
             * @param g
             * @param left
             * @param top
             * @param strand
             * @return
             */
            private final int paintIndeciesForStrand(Graphics2D g, int left, int top, int strand) {
                long numOfNucleotides = dna.getSequenceLength();
                
                top += nucleotideBox.getHeight();
                int y2 = top - fontMetrics.getDescent();
                int shift = (int) (2 * (nucleotideBox.getWidth() + 1));
                int increment = 2 * AminoAcid.NUCLEOTIDES_PER_CODON;
                
                for (long n = 0; n <= numOfNucleotides; n += increment) {
                    g.drawLine(left, top, left, top - 1);
                    g.drawString(n + "", left, y2);
                    left += shift;
                }
                
                return ++top;
            }
            
            
            /**
             * 
             * @param g
             * @param left
             * @param top
             * @param strand
             * @return
             */
            private final int paintFramesForStrand(Graphics2D g, int left, int top, int strand) {
                int numOfAminoAcid = 0;
                int x2 = 0;
                
                int w = (int) nucleotideBox.getWidth();
                int h = (int) nucleotideBox.getHeight();
                
                Color origColor = g.getColor();
                Polygon p = new Polygon();
                p.addPoint(left,  top);
                p.addPoint(left + pad, top + h / 2);
                p.addPoint(left, top + h);
                p.addPoint(left + w, top + h);
                p.addPoint(left + w + pad, top + h / 2);
                p.addPoint(left + w, top);
                p.invalidate();
                
                p.translate(-w, 0);
                for (int f = 0; f < dna.getStrand(strand).getNumOfFrames(); f++) {
                    x2 = left + f * w / 3;
                    numOfAminoAcid = 0;
                    for (AminoAcid aa : dna.getStrand(strand).getFrame(f)) {
                        if (x2 > width)
                            break;
                        
                        numOfAminoAcid++;
                        if (aa == AminoAcid.START_CODON)
                            g.setColor(Color.RED);
                        else if (aa == AminoAcid.STOP_CODON)
                            g.setColor(Color.BLACK);
                        else
                            g.setColor(Color.LIGHT_GRAY);
                        
                        p.translate(w + 1, 0);
                        g.draw(p);
                        
                        String shortName = aa.getShortName();
                        int sleft = x2 + (w - fontMetrics.stringWidth(shortName)) / 2;
                        int sTop = top + (h - fontMetrics.getHeight()) / 2 + fontMetrics.getAscent();
                        g.drawString(shortName, sleft + pad, sTop + pad);
                        
                        x2 += w + 1;
                    }
                    top += h + 1;
                    p.translate(w / 3 - numOfAminoAcid * (w + 1), h + 1);
                }
                
                g.setColor(origColor);
                return top;
            }
            
            
            /**
             * 
             * @return
             */
            private final Graphics2D getBufferGraphics() {
                Graphics2D g2d = tmpBuffer.createGraphics();
                g2d.setColor( DNAInterfacePanel.this.getForeground() );
                g2d.setFont( DNAInterfacePanel.this.getFont() );
                g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON );
                g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
                g2d.setRenderingHint( RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY );
                
                if (nucleotideBox == null) {
                    fontMetrics = g2d.getFontMetrics( g2d.getFont() );
                    nucleotideBox = fontMetrics.getStringBounds("stop", g2d);
                    nucleotideBox.setFrame(0, 0, nucleotideBox.getWidth() + pad + pad, nucleotideBox.getHeight() + pad + pad);
                }
                
                return g2d;
            }
            
        }
        
        
        @Override
        public void componentHidden(ComponentEvent ce) { }

        
        @Override
        public void componentMoved(ComponentEvent ce) {
            //isUpdated = false;
            repaint();
        }

        
        @Override
        public void componentResized(ComponentEvent ce) {
            //nucleotidesViewable = (int) (width / nucleotideBox.getWidth());
            //evBox.setWidthByNumNucleotides(nucleotidesViewable);
            
            isUpdated = false;
            repaint();
        }

        
        @Override
        public void componentShown(ComponentEvent ce) {
            isUpdated = false;
            repaint();
        }
        
    }
   
   
    /**
     * 
     * @author Chris Hartley
     *
     */
    private class LabelPanel extends JComponent {
        
        private static final long serialVersionUID = -6921431984772933041L;
        
        private final String labels[] = {
                "index", "Frame A", "Frame B", "Frame C", "mRNA(3')", "3'",
                "5'", "mRNA(5')", "Frame A", "Frame B", "Frame C", "index"
        };
        private boolean requiredDimensionsCalculated = false;
        
        private final int alignment;
        private int LineHeight;
        private final Dimension requiredDim = new Dimension();
        
        private BufferedImage buffer;
        private boolean isUpdated = false;
        
        private Thread refreshThread;
        
        private FontMetrics fontMetrics = null;
        private Rectangle2D lblBox = null;
        
        
        /**
         * 
         * @param align
         */
        public LabelPanel(int align) {
            assert(align == SwingConstants.LEFT || align == SwingConstants.RIGHT);
            alignment = align;
            LineHeight = 20;
            requiredDim.height = LineHeight * 12;
            requiredDim.width = 80;
            
            setOpaque(false);
            setFont( DNAInterfacePanel.this.getFont() );
            
            setMinimumSize(requiredDim);
            setMaximumSize(requiredDim);
            setPreferredSize(requiredDim);
            
            addComponentListener( new ComponentAdapter() {
                @Override
                public void componentMoved(ComponentEvent ce) {
                    repaint();
                }
                
                @Override
                public void componentResized(ComponentEvent ce) {
                    isUpdated = false;
                    repaint();
                }
                
                @Override
                public void componentShown(ComponentEvent ce) {
                    repaint();
                }
            });
        }
        
        
        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D)g;
            g2d.setFont( getFont() );
            
            if (isUpdated) {
                g2d.drawImage(buffer, 0, 0, this);
            }
            else {
                if (refreshThread != null)
                    refreshThread.interrupt();
                
                refreshThread = new Thread() {
                    public void run() {
                        refresh();
                    }
                };
                refreshThread.start();
            }
            g2d.dispose();
        }
        
        
        @Override
        public void doLayout() {
            if (!requiredDimensionsCalculated)
                calculateRequiredDimensions();
            super.doLayout();
        }
        
        
        /**
         * 
         */
        private final synchronized void calculateRequiredDimensions() {
            Graphics g = getGraphics();
            if (g != null) {
                int pad2 = pad + pad;
                FontMetrics fm = g.getFontMetrics( getFont() );
                Rectangle2D box = fm.getStringBounds("stop", g);
                box.setFrame(0, 0, box.getWidth() + pad2, box.getHeight() + pad2 + 1);
                
                int w, maxWidth = (int) box.getWidth();
                for (String lbl : labels) {
                    w = fm.stringWidth(lbl);
                    if (w > maxWidth)
                        maxWidth = w;
                }
                
                requiredDim.height = (int) (box.getHeight() * labels.length);
                requiredDim.width = maxWidth + pad2;
                
                setMinimumSize(requiredDim);
                setMaximumSize(requiredDim);
                setPreferredSize(requiredDim);
                
                requiredDimensionsCalculated = true;
                g.dispose();
            }
        }
        
        
        /**
         * 
         */
        private synchronized void refresh() {
            int w = getWidth() - 1;
            int h = getHeight() - 1;
            buffer = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR_PRE);
            
            Graphics2D g2d = getBufferGraphics();
            
            int pad2 = pad + pad;
            int x = pad;
            int y = pad + fontMetrics.getAscent();
            y += (int) (lblBox.getHeight() - fontMetrics.getHeight()) / 2;
            
            g2d.setColor(highlightBorderColorNormal);
            g2d.drawRect(0, 0, w - 1, h - 1);
            
            w -= pad2;
            h -= pad2;
            
            for (String lbl : labels) {
                if (alignment == SwingConstants.RIGHT)
                    x = w - fontMetrics.stringWidth(lbl) + pad;
                
                g2d.drawString(lbl, x, y);
                y += lblBox.getHeight();
            }
            
            g2d.dispose();
            
            isUpdated = true;
            repaint();
        }
        
        
        /**
         * 
         * @return
         */
        private final Graphics2D getBufferGraphics() {
            Graphics2D g2d = buffer.createGraphics();
            g2d.setColor( getForeground() );
            g2d.setFont( getFont() );
            g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON );
            g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
            g2d.setRenderingHint( RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY );
            
            if (lblBox == null) {
                int pad2 = pad + pad;
                fontMetrics = g2d.getFontMetrics( getFont() );
                lblBox = fontMetrics.getStringBounds("stop", g2d);
                lblBox.setFrame(0, 0, lblBox.getWidth() + pad2, lblBox.getHeight() + pad2 + 1);
            }
            
            return g2d;
        }
      
    }

}
