/**
 * 
 */
package core.gui;

import core.gui.panes.DNASequenceFromFilePanel;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

/**
 * 
 * @author Chris N. Hartley
 */
public final class AppFrame extends JFrame implements Runnable {


    /**
     * 
     */
    private static final String APP_TITLE = "DNA Analysis";
   
    
    /**
     * 
     */
    private static final long serialVersionUID = -2480432146969139127L;

    
    /**
     * Constructor for this implementation of the application frame.
     */
    public AppFrame() {
        super(APP_TITLE);
    }

    
    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        buildGUI();

        setSize(640, 640);
        setVisible(true);
    }

    
    /**
     * Builds the graphical user interface to support this application.
     */
    private final void buildGUI() {
        System.out.printf("loading GUI...\n");
        JPanel main = new JPanel( new BorderLayout(6, 6) );
        main.setBorder( BorderFactory.createEmptyBorder(6, 6, 6, 6) );

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFocusable(false);
        //tabs.add("Manual", new ManualEntryPanel());
        JPanel pnl = new JPanel( new BorderLayout() );
        pnl.add( new DNASequenceFromFilePanel(), BorderLayout.NORTH);
        pnl.add( new DNASequenceFromFilePanel(), BorderLayout.SOUTH);
        //tabs.add("From File", pnl);
        //tabs.add("From File", new DNASequenceFromFilePanel());
        //main.add(tabs, BorderLayout.CENTER);
        main.add(new JScrollPane(pnl), BorderLayout.CENTER);

       // Status bar...
       JPanel bottomBar = new JPanel();
       main.add(bottomBar, BorderLayout.SOUTH);

       setContentPane(main);
       setJMenuBar( new AppMenuBar() );
    }


    /**
     * 
     * @author Chris Hartley
     *
     */
    class AppToolBar extends JToolBar {

        /**
         * 
         */
        private static final long serialVersionUID = 755081205609393470L;
        
        
        /**
         * Constructor for this applications tool bar.
         */
        public AppToolBar() {
            
        }
        
    }

    
    /**
     * 
     * @author Chris Hartley
     *
     */
    class AppMenuBar extends JMenuBar {
        
        /**
         * 
         */
        private static final long serialVersionUID = -7686971217707856183L;

        
        /**
         * Constructor for this applications menu bar.
         */
        public AppMenuBar() {
            buildMenus();
        }

        
        /**
         * Generates the menu bar with its component items and actions.
         */
        private void buildMenus() {
            JMenu menu;
            JMenuItem item;
            JRadioButtonMenuItem rb;

            menu = new JMenu("File");
            item = new JMenuItem("Open Sequence...");
            menu.add(item);
            item = new JMenuItem("Close Sequence");
            menu.add(item);
            menu.addSeparator();
            item = new JMenuItem("Properties...");
            menu.add(item);
            menu.addSeparator();
            item = new JMenuItem("Exit");
            menu.add(item);
            add(menu);
           
            menu = new JMenu("Analysis");
            item = new JMenuItem("PAM Score...");
            menu.add(item);
            add(menu);

            menu = new JMenu("View");
            ButtonGroup sequenceGroup = new ButtonGroup();
            rb = new JRadioButtonMenuItem("1 Sequence");
            rb.setSelected(false);
            sequenceGroup.add(rb);
            menu.add(rb);
            rb = new JRadioButtonMenuItem("2 Sequences");
            rb.setSelected(true);
            sequenceGroup.add(rb);
            menu.add(rb);
            menu.addSeparator();
            item = new JMenuItem("Show/Hide Details");
            menu.add(item);
            item = new JMenuItem("Show/Hide Status Bar");
            menu.add(item);
            add(menu);
           
            menu = new JMenu("Help");
            add(menu);
        }
        
    }

}
