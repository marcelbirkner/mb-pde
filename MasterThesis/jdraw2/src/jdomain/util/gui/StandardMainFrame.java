package jdomain.util.gui;

import java.awt.BorderLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JComponent;
import javax.swing.JFrame;

import jdomain.util.Log;
import jdomain.util.Util;

public class StandardMainFrame extends JFrame implements KeyEventDispatcher, WindowFocusListener {

   private static final long serialVersionUID = 0L;

   private boolean ignoreKeys = true;

   public StandardMainFrame( String title ) {
      super( title );
      KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher( this );
      setupSize();
      WindowAdapter a = new WindowAdapter() {
         public void windowOpened( WindowEvent e ) {
            opened();
         }

         public void windowClosing( WindowEvent e ) {
            closing();
         }
      };
      addWindowListener( a );
      addWindowFocusListener( this );
      initGui();
      setDefaultCloseOperation( EXIT_ON_CLOSE );
   }

   protected void setupSize() {
      final Rectangle bounds = GUIUtil.getGeometry();
      setSize( bounds.width, bounds.height );
      setLocation( bounds.x, bounds.y );

   }

   protected void opened() {
      if ( System.getProperty( GUIUtil.GEOMETRY_PARAM ) == null ) {
         setExtendedState( MAXIMIZED_BOTH );
      }
   }

   protected void closing() {
      Log.close();
   }

   private final void initGui() {
      ((JComponent)getContentPane()).setLayout( new BorderLayout( 4, 0 ) );
   }

   protected void createGui() {
   }

   protected final String keyDesc( char c, int modifiers ) {
      return "'" + c + "' (0x" + Util.hexString( c ) + "), modifiers = " + "0x" + Util.hexString( modifiers );
   }

   public final boolean isIconfied() {
      return (getExtendedState() & ICONIFIED) != 0;
   }

   protected boolean handleKey( KeyEvent e ) {
      return false;
   }

   public final boolean dispatchKeyEvent( KeyEvent e ) {
      if ( ignoreKeys || (!isActive()) ) {
         return false;
      }
      return handleKey( e );
   }

   private final void setIgnoreKeys( boolean b ) {
      ignoreKeys = b;
   }

   public void windowGainedFocus( WindowEvent e ) {
      setIgnoreKeys( false );
   }

   public void windowLostFocus( WindowEvent e ) {
      setIgnoreKeys( true );
   }

}
