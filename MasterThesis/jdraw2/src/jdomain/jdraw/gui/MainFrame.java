package jdomain.jdraw.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;

import jdomain.jdraw.Main;
import jdomain.jdraw.Settings;
import jdomain.jdraw.action.CompressAction;
import jdomain.jdraw.action.DecreaseZoomAction;
import jdomain.jdraw.action.DrawAction;
import jdomain.jdraw.action.EditFrameSettingsAction;
import jdomain.jdraw.action.ExitAction;
import jdomain.jdraw.action.FlipClipHorizontallyAction;
import jdomain.jdraw.action.FlipClipVerticallyAction;
import jdomain.jdraw.action.HelpAction;
import jdomain.jdraw.action.IncreaseZoomAction;
import jdomain.jdraw.action.LoadAction;
import jdomain.jdraw.action.NewAction;
import jdomain.jdraw.action.RedoAction;
import jdomain.jdraw.action.ReduceColoursAction;
import jdomain.jdraw.action.ReducePaletteColoursAction;
import jdomain.jdraw.action.RemoveColourAction;
import jdomain.jdraw.action.ResizeAction;
import jdomain.jdraw.action.RotateClipAction;
import jdomain.jdraw.action.SaveAction;
import jdomain.jdraw.action.SaveAsAction;
import jdomain.jdraw.action.ScaleAction;
import jdomain.jdraw.action.SetColourPickerToolAction;
import jdomain.jdraw.action.SetFillToolAction;
import jdomain.jdraw.action.SetLineToolAction;
import jdomain.jdraw.action.SetMaxZoomAction;
import jdomain.jdraw.action.SetMinZoomAction;
import jdomain.jdraw.action.SetPixelToolAction;
import jdomain.jdraw.action.SetPreviousZoomAction;
import jdomain.jdraw.action.SwapColoursAction;
import jdomain.jdraw.action.ToggleGridAction;
import jdomain.jdraw.action.ToggleTransparencyAction;
import jdomain.jdraw.action.ToggleViewsAction;
import jdomain.jdraw.action.UndoAction;
import jdomain.jdraw.action.ViewAnimationAction;
import jdomain.jdraw.data.DataChangeListener;
import jdomain.jdraw.data.Palette;
import jdomain.jdraw.data.Picture;
import jdomain.jdraw.data.event.ChangeEvent;
import jdomain.jdraw.gui.undo.UndoManager;
import jdomain.util.Log;
import jdomain.util.ResourceLoader;
import jdomain.util.gui.GUIUtil;
import jdomain.util.gui.StandardMainFrame;

public final class MainFrame extends StandardMainFrame implements DataChangeListener {

   /** */
   private static final long serialVersionUID = 1L;
   public static final Font DEFAULT_FONT = GUIUtil.DEFAULT_FONT;
   public static final Font SMALL_FONT = new Font( "SansSerif", Font.PLAIN, 10 );
   public static final Font TINY_FONT = new Font( "SansSerif", Font.PLAIN, 9 );
   public static final Font BOLD_FONT = GUIUtil.BOLD_FONT;
   public static final Font BIG_FONT = new Font( "Serif", Font.BOLD, 32 );

   private static final String PREFIX = Main.APP_NAME + " " + Main.VERSION;

   public static final MainFrame INSTANCE = new MainFrame();

   private String fileName = null;
   private Picture picture;

   private final JPanel centerPanel = new JPanel( new BorderLayout( 0, 0 ) );

   private MainFrame() {
      super( PREFIX );
      setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
      setIconImage( ResourceLoader.getImage( "jdomain/jdraw/images/pixel_tool.png" ).getImage() );

      setJMenuBar( new DrawMenu() );
      updateTitle();
      createGui();
      JPanel glassPane = new JPanel();
      glassPane.setLayout( null );
      glassPane.setOpaque( false );
      glassPane.add( InfoClip.INSTANCE );
      setGlassPane( glassPane );
      getGlassPane().setVisible( true );
   }

   protected void setupSize() {
      Settings s = Settings.INSTANCE;
      Dimension dim = s.getFrameDimension();
      if ( dim != null ) {
         Log.debug( "restoring window dimension and location" );
         setLocation( s.getFrameLocation() );
         setSize( dim );
      }
      else {
         setLocation( new Point( 0, 0 ) );
         setSize( new Dimension( 640, 480 ) );
      }
   }

   protected void opened() {
      Settings s = Settings.INSTANCE;
      Dimension dim = s.getFrameDimension();
      if ( dim == null ) {
         super.opened();
      }
   }

   private void attach() {
      picture.addDataChangeListener( this );

      final int frames = picture.getFrameCount();
      jdomain.jdraw.data.Frame f;
      for ( int i = 0; i < frames; i++ ) {
         f = picture.getFrame( i );
         f.addDataChangeListener( this );
         f.getPalette().addDataChangeListener( this );
      }
   }

   private void detach() {
      if ( picture != null ) {
         picture.removeDataChangeListener( this );
         final int frames = picture.getFrameCount();
         jdomain.jdraw.data.Frame f;
         for ( int i = 0; i < frames; i++ ) {
            f = picture.getFrame( i );
            f.removeDataChangeListener( this );
            f.getPalette().removeDataChangeListener( this );
         }
      }
   }

   public void setPicture( Picture pic ) {
      final boolean updateOnly = (pic == picture);

      if ( !updateOnly ) {
         detach();
         picture = pic;
      }
      FolderPanel.INSTANCE.setPicture( picture );
      PalettePanel.INSTANCE.setPalette( picture.getCurrentFrame().getPalette() );

      UndoManager.INSTANCE.reset();
      updateTitle();
      if ( !updateOnly ) {
         attach();
      }
   }

   public Picture getPicture() {
      return picture;
   }

   private final JPanel getMainPanel() {
      return (JPanel)getContentPane();
   }

   protected void createGui() {
      JPanel mainPanel = getMainPanel();
      mainPanel.add( StatusPanel.INSTANCE, BorderLayout.SOUTH );
      centerPanel.add( FolderPanel.INSTANCE, BorderLayout.CENTER );
      mainPanel.add( centerPanel, BorderLayout.CENTER );
      showViews( true );
   }

   public void showViews( boolean flag ) {
      if ( flag ) {
         getMainPanel().add( PalettePanel.INSTANCE, BorderLayout.WEST );
         centerPanel.add( ToolPanel.INSTANCE, BorderLayout.NORTH );
      }
      else {
         getMainPanel().remove( PalettePanel.INSTANCE );
         centerPanel.remove( ToolPanel.INSTANCE );
      }
   }

   public String getFileName() {
      return fileName;
   }

   public void setFileName( String aName ) {
      fileName = aName;
      updateTitle();
   }

   public void updateTitle() {
      String f = fileName;
      if ( f == null ) {
         f = "noname";
      }
      StringBuffer title = new StringBuffer( f );
      title.insert( 0, "[" );
      if ( UndoManager.INSTANCE.hasChanges() ) {
         title.append( "*" );
      }
      title.append( "] " );
      if ( picture != null ) {
         title.append( "(" ).append( String.valueOf( picture.getWidth() ) );
         title.append( "x" ).append( String.valueOf( picture.getHeight() ) );
         title.append( ", " ).append( Tool.getCurrentPalette().size() );
         title.append( " colours)" );
      }
      setTitle( PREFIX + "  -  " + title.toString() );
      if ( picture != null ) {
         DrawAction.getAction( ReduceColoursAction.class ).setEnabled(
               picture.getMaximalPaletteSize() > Palette.GIF_MAX_COLOURS );
         DrawAction.getAction( ReducePaletteColoursAction.class ).setEnabled(
               Tool.getCurrentPalette().size() > Palette.GIF_MAX_COLOURS );
      }
   }

   protected void closing() {
      DrawAction.getAction( ExitAction.class ).actionPerformed();
   }

   private DrawAction handleCtrlKeys( char c ) {
      switch ( c ) {
         case 0x01: // Ctrl-A
            return DrawAction.getAction( SaveAsAction.class );
         case 0x03: // Ctrl-C
            return DrawAction.getAction( ScaleAction.class );
         case 0x04: // Ctrl-D
            return DrawAction.getAction( RemoveColourAction.class );
         case 0x05: // Ctrl-E
            return DrawAction.getAction( EditFrameSettingsAction.class );
         case 0x07: // Ctrl+G
            return DrawAction.getAction( ToggleGridAction.class );
         case 0x08: // Ctrl-H
            return DrawAction.getAction( HelpAction.class );
         case 0x0b: // Ctrl-K
            return DrawAction.getAction( CompressAction.class );
         case 0x0c: // Ctrl-L
            return DrawAction.getAction( LoadAction.class );
         case 0x0a: // Ctrl-M
            return DrawAction.getAction( SwapColoursAction.class );
         case 0x0e: // Ctrl-N
            return DrawAction.getAction( NewAction.class );
         case 0x10: // Ctrl+P
            return DrawAction.getAction( SetPixelToolAction.class );
         case 0x12: // Ctrl-R
            return DrawAction.getAction( ResizeAction.class );
         case 0x13: // Ctrl-S
            return DrawAction.getAction( SaveAction.class );
         case 0x16: // Ctrl-V
            return DrawAction.getAction( ViewAnimationAction.class );
         case 0x1a: // Ctrl-Z
            return DrawAction.getAction( UndoAction.class );
         default:
            return null;
      }
   }

   private DrawAction handleCtrlShiftKeys( char c ) {
      switch ( c ) {
         case 0x1a: // Ctrl-Shift-Z
            return DrawAction.getAction( RedoAction.class );
         default:
            return null;
      }
   }

   private DrawAction handlePlainKeys( char c ) {
      switch ( c ) {
         case '+':
            return DrawAction.getAction( IncreaseZoomAction.class );
         case '-':
            return DrawAction.getAction( DecreaseZoomAction.class );
         case '1':
            return DrawAction.getAction( SetPixelToolAction.class );
         case '2':
            return DrawAction.getAction( SetFillToolAction.class );
         case '3':
            return DrawAction.getAction( SetColourPickerToolAction.class );
         case '4':
            return DrawAction.getAction( SetLineToolAction.class );
         case '0':
            return DrawAction.getAction( SetMinZoomAction.class );
         case '9':
            return DrawAction.getAction( SetMaxZoomAction.class );
         case '8':
            return DrawAction.getAction( SetPreviousZoomAction.class );
         case ' ':
            return DrawAction.getAction( ToggleTransparencyAction.class );
         case '\t':
            return DrawAction.getAction( ToggleViewsAction.class );
         case 'r':
            return DrawAction.getAction( RotateClipAction.class );
         case 'h':
            return DrawAction.getAction( FlipClipHorizontallyAction.class );
         case 'v':
            return DrawAction.getAction( FlipClipVerticallyAction.class );
         case 0x1b: // ESC
            return DrawAction.getAction( SetPixelToolAction.class );
         default:
            return null;
      }
   }

   private DrawAction handleShiftKeys( char c ) {
      return null;
   }

   public boolean handleKey( KeyEvent e ) {
      if ( e.getID() == KeyEvent.KEY_TYPED ) {
         if ( (e.getModifiers() & KeyEvent.ALT_MASK) > 0 ) {
            return false;
         }
         DrawAction action = null;

         switch ( e.getModifiers() ) {
            case KeyEvent.CTRL_MASK:
               action = handleCtrlKeys( e.getKeyChar() );
               break;
            case 0:
               action = handlePlainKeys( e.getKeyChar() );
               break;
            case KeyEvent.SHIFT_MASK:
               action = handleShiftKeys( e.getKeyChar() );
               break;
            case KeyEvent.SHIFT_MASK + KeyEvent.CTRL_MASK:
               action = handleCtrlShiftKeys( e.getKeyChar() );
               break;
            default:
               return false;
         }
         if ( action != null ) {
            action.actionPerformed();
            e.consume();
            return true;
         }

         if ( Log.DEBUG )
            Log.debug( "unknown key: " + keyDesc( e.getKeyChar(), e.getModifiers() ) );

      }
      return false;
   }

   public void dataChanged( ChangeEvent e ) {
      Dispatcher.dispatch( e );
   }

}
