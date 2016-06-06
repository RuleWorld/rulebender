package rulebender.editors.bngl;


import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.Integer;
import java.lang.NumberFormatException;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.antlr.runtime.RecognitionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.jface.viewers.ISelectionProvider;


import rulebender.core.utility.ANTLRFilteredPrintStream;
import rulebender.core.utility.Console;
import rulebender.editors.bngl.model.BNGLModel;
import rulebender.errorview.model.BNGLError;
import rulebender.logging.Logger;
import rulebender.simulate.BioNetGenConsole;
import rulebender.contactmap.prefuse.CMapClickControlDelegate;
import rulebender.contactmap.properties.MoleculePropertySource;
import rulebender.contactmap.view.ContactMapSelectionListener;
import rulebender.contactmap.view.ContactMapView;
import bngparser.BNGParseData;
import bngparser.BNGParserUtility;

//import rulebender.simulate.BioNetGenConsole;

/**
 * This class defines the key listener editor for bngl.
 * 
 * @author 
 * 
 */
public class BNGLListener implements KeyListener {
	
		   @Override
		   public void keyReleased(KeyEvent e) {
			   int  c_offset = 0;
		  	   int  c_length = 0;

  		  	   IEditorPart rceditor  = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
  		  	   if (rceditor instanceof ITextEditor) {

  		  		 	ISelectionProvider rcselectionProvider = ((ITextEditor)rceditor).getSelectionProvider();
  		  		  	ISelection rcselection = rcselectionProvider.getSelection();
  		  		  	if (rcselection instanceof ITextSelection) {
  		  		  	   ITextSelection rctextSelection = (ITextSelection)rcselection;
  		  		  	   int rcoffset = rctextSelection.getOffset();   	  	  	  		   			   
 					       if (rcoffset > -1) {
  						     c_offset = rcoffset;
  				  		     c_length = rctextSelection.getLength();
  			               }
  		 		    }
  	            }
  		  		
  		 // 92 = \
         if ((e.keyCode == 92) && ((e.stateMask & SWT.CTRL)  == SWT.CTRL) && 
						          ((e.stateMask & SWT.SHIFT) == SWT.SHIFT))	{

        	 
		  	     try {
		  	  	   ITextEditor    c_editor = (ITextEditor)rceditor;
		  	  	   IDocumentProvider dp = c_editor.getDocumentProvider();
		  	  	   IDocument     rhcdoc = dp.getDocument(c_editor.getEditorInput());

                   try {		  	  	   
 		  	    	  int c_lines = rhcdoc.getNumberOfLines(c_offset,c_length); 
		  				
 		  	    	 /*
 		             System.out.println(" keycode  = " + e.keyCode + 
		                                " c_offset = " + c_offset  +
		                                " c_length = " + c_length  +
		                                " c_lines  = " + c_lines);
		             */
	  	   
		  			  if ((c_lines == 1) || (c_lines == 0)) {
 		  	    	    int line2 = rhcdoc.getLineOfOffset(c_offset); 
  					    IRegion irgn = rhcdoc.getLineInformation(line2);
  					    if (rhcdoc.getChar(irgn.getOffset()) == '#') {
 		  	              rhcdoc.replace(irgn.getOffset(), 1, "");
  					    } else {
		                  int zzzpoint = traverse_line(irgn.getOffset(),line2,rhcdoc);
		                  if (zzzpoint > 0) rhcdoc.replace(zzzpoint, 1, ""); 
  					    }
		  			 } else {
                       int c_lines_2 = c_lines;
                       if (c_length > 1) {
 		  	    	     c_lines_2 = rhcdoc.getNumberOfLines(c_offset,c_length-1);
                       }
		  				 
	  				   int line2 = rhcdoc.getLineOfOffset(c_offset); 
		  			   int kkk;
		  			   for (kkk=0; kkk<c_lines_2; kkk++) {	 
	 		  	    	  int line5 = line2 + kkk; 
	  					  IRegion irgn = rhcdoc.getLineInformation(line5);
	  					  if (rhcdoc.getChar(irgn.getOffset()) == '#') {
	 		  	            rhcdoc.replace(irgn.getOffset(), 1, "");
	  					  } else {
			                int zzzpoint = traverse_line(irgn.getOffset(),line5,rhcdoc);
			                if (zzzpoint > 0) rhcdoc.replace(zzzpoint, 1, ""); 
	  					  }
		  			   }		 
		  		     }
                   } catch (BadLocationException yyy) {
  		  			 System.out.println("BadLocationException");		  	  		  	    	                  	   
                   }		  	  	   
			     } catch (Exception ee) {
				   System.out.println("92 Cast failed " + ee.getMessage());
			     }		  		   
		  	} 
		}
		
		   
		  		  		
		@Override
		public void keyPressed(KeyEvent e) {
		int tres=0;
			int c_offset=0;
	  		int c_length=0;

		
	  	    IEditorPart rceditor  = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	  		if (rceditor instanceof ITextEditor) {

                // System.out.println(" keycode = " + e.keyCode);
	  			
	  		  	ISelectionProvider rcselectionProvider = ((ITextEditor)rceditor).getSelectionProvider();
	  		  	ISelection rcselection = rcselectionProvider.getSelection();
	  		  	if (rcselection instanceof ITextSelection) {
	  		  	   ITextSelection rctextSelection = (ITextSelection)rcselection;
	  		  	   int rcoffset = rctextSelection.getOffset(); // etc.
	  		  	   if (rcoffset > -1) {
	  		  		   c_offset = rcoffset;
	  		  		   c_length = rctextSelection.getLength();
	  		  	   }
		  		  	
	  		  	  // 13 = #
  		  		  if (e.keyCode == 13) {
  		  		     try {    
  			  	     ITextEditor    c_editor = (ITextEditor)rceditor;
		  			 IDocumentProvider dp = c_editor.getDocumentProvider();
		  			 IDocument     rhcdoc = dp.getDocument(c_editor.getEditorInput());
                           		  		  		      
		  	  		 try {
		  		  	     int line8 = rhcdoc.getLineOfOffset(c_offset) - 1; 
 		  				 IRegion irgn8 = rhcdoc.getLineInformation(line8);
 		  				 IRegion irgn9 = rhcdoc.getLineInformation(line8+1);

 		  				 
 		  				 tres = traverse_line(irgn8.getOffset(), line8, rhcdoc);
                   //    System.out.println(" c_offset = " + c_offset + " tres = " + tres);
                   //    System.out.println("Traversal of line 8 yields: " + tres);
 	 		  			 if (tres > -1) {
 	 		  				 if (c_offset > tres-2) { 	 		  					 
 		  			             rhcdoc.replace(irgn9.getOffset()+tres-irgn8.getOffset()
 		  			            		 , 0, "# ");
                             //    System.out.println("Comment create 1");
  				    	         c_editor.selectAndReveal(irgn9.getOffset() + tres 
	  				    	    	- irgn8.getOffset() + 2, 0);
 	 		  				 }
                         } else {
                           	if (tres == -3) {
     		  				   int tres2 = traverse_line(irgn9.getOffset(), line8+1, rhcdoc);
     		  				   if (tres2 > -1) {
         		  				   // It's a comment
     					  	      
                               	  if (c_offset > tres2-1) {  
   	 		  				        if (irgn8.getLength() == 0) {
   	   	                              // System.out.println("Comment create 2a");
   	 		  				    	  // The # sign is in column 1 of line8
  	                                  rhcdoc.replace(irgn9.getOffset(), 1, "# ");
  	                                  rhcdoc.replace(irgn8.getOffset(), 0, "# ");
   		  				    	      c_editor.selectAndReveal(irgn9.getOffset() + 4, 0);     		  					           		  					         	 		  			    	   
                                    } else {
         	                          // System.out.println("Comment create 2b");
	                                  rhcdoc.replace(irgn9.getOffset() + irgn8.getLength(),
	 	                                     1, "# ");
	                                  rhcdoc.replace(irgn8.getOffset() + irgn8.getLength(),
	 	 	                                 0, "# ");
 		  				    	      c_editor.selectAndReveal(irgn9.getOffset() + 
 		  				    	    	 irgn8.getLength() + 4, 0);
                                	 }
                                  }
     		  				   } 
                           	}
                         }
		  	  	     } catch (BadLocationException yyy) {
				  	     System.out.println("BadLocationException Return key");		  	  		  	    	  
		  	  		 } 
 		  		 } catch (Exception ee) {
	 	 		     System.out.println("13 Cast failed " + ee.getMessage());
 		     	 }
	  		 }
		 }	 
	  }	   

		  	// 47 = /	  			
			if ((e.keyCode == 47) && ((e.stateMask & SWT.CTRL)  == SWT.CTRL) && 
				 	                 ((e.stateMask & SWT.SHIFT) == SWT.SHIFT))	{
	  			  try {
		  			ITextEditor    c_editor = (ITextEditor)rceditor;
		  	        IDocumentProvider dp = c_editor.getDocumentProvider();
		  	        IDocument     rhcdoc = dp.getDocument(c_editor.getEditorInput());

		  	        
		  	        try {
 		    	    	  int c_lines = rhcdoc.getNumberOfLines(c_offset,c_length); 

 		    	    	 /*
 			             System.out.println(" keycode  = " + e.keyCode + 
	                                " c_offset = " + c_offset  +
	                                " c_length = " + c_length  +
	                                " c_lines  = " + c_lines);
	                     */
	             
 		    	    	  
	  		    		  if ((c_lines == 1) || (c_lines == 0)) {
		  	          	    int line4 = rhcdoc.getLineOfOffset(c_offset); 
 					        IRegion irgn = rhcdoc.getLineInformation(line4);
		  	                rhcdoc.replace(irgn.getOffset(), 0, "#");
	  				      } else {
 	                        int c_lines_2 = c_lines;
	  	                    if (c_length > 1) {
	  	 		  	    	  c_lines_2 = rhcdoc.getNumberOfLines(c_offset,c_length-1);
	  	                    }
	  			  				 
	  		  	            int line1 = rhcdoc.getLineOfOffset(c_offset); 
	  				        for (int ii=0; ii<c_lines_2; ii++) {
	  					       IRegion irgn = rhcdoc.getLineInformation(line1 + ii);
	  		  	               rhcdoc.replace(irgn.getOffset(), 0, "#");
	  				        }
	  				      }
		  	        } catch (BadLocationException yyy) {
  		  		      System.out.println("BadLocationException");		  	  		  	    	  
		  	        }	
				  } catch (Exception ee) {
					System.out.println("47 Cast failed " + ee.getMessage());
				  }
	  		  }
   	  }
		 		
	
    private int traverse_line(int tr_offset, int tr_line, IDocument tr_doc ) {
      try { 
  	    // System.out.println(" Traverse  char: " + tr_doc.getChar(tr_offset) 
  	    //		+ " line: " + tr_line);
 	    int line1 = tr_doc.getLineOfOffset(tr_offset); 
        if (tr_offset > tr_doc.getLength()) { 
          return -2;
        } else {
          if (line1 != tr_line) { 
       		// System.out.println("Fell off edge of line ");		  	  		  	    	                  	           	  
            return -3;    	     	  
          } else { 
    	    if (isWhitespace(tr_doc.getChar(tr_offset))) { 
    		  return traverse_line(tr_offset+1,tr_line,tr_doc);
    	    } else {
              if (tr_doc.getChar(tr_offset) == '#') {         	
           		// System.out.println("Its a comment, so return " + tr_offset);		  	  		  	    	                  	   
                return tr_offset;
    	      } else { 
   	            return -2;    	     	      		  
    	      }
    	    }
          }
        }
   	  }  
      catch (BadLocationException yyy) {
  		System.out.println("BadLocationException in traverse_line.");		  	  		  	    	                  	   
	    return -1;    	     	      		  
      }		  	  	   
    }

    
    public boolean isWhitespace(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}
      
	
}
