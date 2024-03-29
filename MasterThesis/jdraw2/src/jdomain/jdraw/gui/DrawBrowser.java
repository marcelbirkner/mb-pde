package jdomain.jdraw.gui;

import java.io.File;

import jdomain.jdraw.Main;
import jdomain.util.gui.BrowserFilter;
import jdomain.util.gui.FileBrowser;
import jdomain.util.gui.IconViewer;

public final class DrawBrowser extends FileBrowser {

   /** */
   private static final long serialVersionUID = 1L;

   public static final DrawBrowser INSTANCE = new DrawBrowser();

   private static final String[] PALLETTE_EXTENSIONS = { ".pal" };

   private static final String[] ICO_EXTENSIONS = { ".ico" };
   private static final String[] GIF_EXTENSIONS = { ".gif" };
   private static final String[] GIF_INTERLACED_EXTENSIONS = { ".gif" };
   private static final String[] PNG_EXTENSIONS = { ".png" };
   private static final String[] JPEG_EXTENSIONS = { ".jpg", ".jpeg" };
   private static final String[] DRAW_EXTENSIONS = { ".jd" };

   private static final BrowserFilter PAL_FILTER = BrowserFilter.createFilter( "Palettes",
         PALLETTE_EXTENSIONS );

   public static final BrowserFilter ICO_FILTER = BrowserFilter.createFilter( "ICO Images", ICO_EXTENSIONS );

   public static final BrowserFilter GIF_FILTER = BrowserFilter.createFilter( "GIF Images", GIF_EXTENSIONS );

   public static final BrowserFilter GIF_INTERLACED_FILTER = BrowserFilter.createFilter(
         "GIF Images (Interlaced)", GIF_INTERLACED_EXTENSIONS );

   public static final BrowserFilter PNG_FILTER = BrowserFilter.createFilter( "PNG Images", PNG_EXTENSIONS );

   public static final BrowserFilter PNG_INTERLACED_FILTER = BrowserFilter.createFilter(
         "PNG Images (Interlaced)", PNG_EXTENSIONS );

   public static final BrowserFilter JPG_FILTER = BrowserFilter.createFilter( "JPEG Images", JPEG_EXTENSIONS );

   public static final BrowserFilter DRAW_FILTER = BrowserFilter.createFilter( Main.APP_NAME + " Images",
         DRAW_EXTENSIONS );

   public static final BrowserFilter ALL_IMAGES_READ_FILTER = BrowserFilter.createFilter( "All Images",
         DRAW_EXTENSIONS );

   private static final BrowserFilter ALL_IMAGES_WRITE_FILTER = BrowserFilter.createFilter( "All Images",
         GIF_EXTENSIONS );

   private static final int UNDEFINED_MODE = 0;
   private static final int SAVING_IMAGE = 1;
   private static final int SAVING_PALETTE = 2;

   private int mode = UNDEFINED_MODE;

   static {
      // ALL_IMAGES_READ_FILTER.addExtensions(DRAW_EXTENSIONS);
      ALL_IMAGES_READ_FILTER.addExtensions( GIF_EXTENSIONS );
      ALL_IMAGES_READ_FILTER.addExtensions( ICO_EXTENSIONS );
      ALL_IMAGES_READ_FILTER.addExtensions( JPEG_EXTENSIONS );
      ALL_IMAGES_READ_FILTER.addExtensions( PNG_EXTENSIONS );

      ALL_IMAGES_WRITE_FILTER.addExtensions( DRAW_EXTENSIONS );
      ALL_IMAGES_WRITE_FILTER.addExtensions( ICO_EXTENSIONS );
      ALL_IMAGES_WRITE_FILTER.addExtensions( JPEG_EXTENSIONS );
      ALL_IMAGES_WRITE_FILTER.addExtensions( PNG_EXTENSIONS );
   }

   private DrawBrowser() {
      setAcceptAllFileFilterUsed( false );
      IconViewer.addImageHandler( new ICOHandler() );
      IconViewer.addImageHandler( new JDHandler() );
   }

   public final File openImage() {
      BrowserFilter[] filters = new BrowserFilter[] {      
            DRAW_FILTER, GIF_FILTER, ICO_FILTER, JPG_FILTER, PNG_FILTER };

      File f = open( MainFrame.INSTANCE, "Open Image...", filters, ALL_IMAGES_READ_FILTER, true );

      return f;
   }

   public final File openPalette() {
      BrowserFilter[] filters = new BrowserFilter[] { PAL_FILTER };
      File f = open( MainFrame.INSTANCE, "Open Palette...", filters, null, true );

      return f;
   }

   public final File savePalette() {
      mode = SAVING_PALETTE;
      BrowserFilter[] filters = new BrowserFilter[] { PAL_FILTER };
      File f = open( MainFrame.INSTANCE, "Save Palette...", filters, null, true );
      mode = UNDEFINED_MODE;
      return f;
   }

   public final boolean saveInterlaced() {
      boolean flag = (getFileFilter() == GIF_INTERLACED_FILTER) || (getFileFilter() == PNG_INTERLACED_FILTER);
      return flag;
   }

   public final File saveImage() {
      mode = SAVING_IMAGE;
      BrowserFilter[] filters;
      filters = new BrowserFilter[] { DRAW_FILTER, GIF_FILTER, GIF_INTERLACED_FILTER, ICO_FILTER, JPG_FILTER,
            PNG_FILTER, PNG_INTERLACED_FILTER };

      File f = save( MainFrame.INSTANCE, "Save Image...", filters, ALL_IMAGES_WRITE_FILTER, true );
      mode = UNDEFINED_MODE;
      return f;
   }

   protected boolean hasValidExtension( File f, BrowserFilter filter ) {
      switch ( mode ) {
         case SAVING_IMAGE:
            if ( ALL_IMAGES_WRITE_FILTER != null ) {
               return ALL_IMAGES_WRITE_FILTER.accept( f );
            }
         case SAVING_PALETTE:
            return PAL_FILTER.accept( f );
         default:
            return super.hasValidExtension( f, filter );
      }
   }

   protected String adjustFilename( String filename, BrowserFilter selectedFilter ) {
      String f = super.adjustFilename( filename, selectedFilter );
      if ( f.length() > 0 ) {
         f = f + selectedFilter.getFirstExtension();
      }
      return f;
   }
}
