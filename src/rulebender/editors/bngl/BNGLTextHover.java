package rulebender.editors.bngl;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.graphics.Point;
import org.eclipse.jface.text.Region;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class BNGLTextHover implements ITextHover
{
  private ScriptEngineManager mgr = new ScriptEngineManager();
  private ScriptEngine engine = mgr.getEngineByName("JavaScript");
  private Pattern eqPattern = Pattern.compile("\\w\\s+[-+]? ?(\\d|\\w|\\()");
          
  public BNGLTextHover() 
  {
    super();
  }
  
  
  @Override
  public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
    if (hoverRegion != null) {
      try {
        if (hoverRegion.getLength() > -1) {
          String res, rawtext;
          String htext = "";
          rawtext = textViewer.getDocument().get(hoverRegion.getOffset(), hoverRegion.getLength());
          System.out.println("We are hovering over: " + rawtext);
          // now remove all BNGL comments, we need to split each new line if we have them
          String lines[] = rawtext.split("\\r?\\n");
          if (lines.length>1) {
        	  String line;
              for (int x=0;x<lines.length;x++) {
            	  // remove comments in each line
            	  line = lines[x];
            	  // skip this line if empty or only a comment
            	  String tline;
            	  int toffset = line.indexOf("#");
            	  if (toffset!=-1) {
            		  tline = line.substring(0, toffset);
            		  tline = tline.replaceAll("\\s+", "");
            	  } else {
            		  tline = line.replaceAll("\\s+", "");
            	  }
            	  if (tline.length()==0) {
            		  continue;
            	  }
            	  
            	  // if we want to try to attempt adding "="s, this is the place to do it
            	  // ONLY do this if we are starting with a character post-whitespace removal
            	  if (!line.replaceAll("\\s+","").substring(0,1).matches("\\d")) {
                	  int eqind = line.indexOf("="); // we would prefer a regex to not include "==" probs
                	  if (eqind==-1) {
                		  System.out.println("didn't find =");
                		  // now we want to find the first instance of whitespace followed by numerical values
                		  Matcher m = eqPattern.matcher(line);
                		  if (m.find()) {
                			  int st = m.start();
                			  if (st!=-1) {
                				  System.out.println("found \\s\\d at: " + st);
                				  line = line.substring(0,st+1) + "=" + line.substring(st+1);
                				  System.out.println("new line is: " + line);
                			  }
                		  }
                	  } 
            	  }
            	  // done with "="?
            	  line = line.replaceAll("\\s+", "");
            	  System.out.println("Removed white space: " + line);
            	  // find where the comment starts
            	  int offset = line.indexOf("#");
            	  if (offset!=-1) {
            		  line = line.substring(0, offset);
            	  }
            	  // if the comment exists, remove the comment
            	  if (x>0) {
            		  htext += ";" + line;
            	  } else {
            		  htext = line;
            	  }
            	  System.out.println("in loop, cleaned up line: " + htext);
              }  
          } else {
        	  // we don't have multiple lines, remove comments and clean up whitespace
        	  int offset = rawtext.indexOf("#");
        	  if (offset!=-1) {
        		  htext = rawtext.substring(0, offset);
        	  } else {
        		  htext = rawtext;
        	  }
        	  // if we want to try to attempt adding "="s, this is the place to do it
        	  // ONLY do this if we are starting with a character post-whitespace removal
        	  if (!htext.replaceAll("\\s+","").substring(0,1).matches("\\d")) {
            	  int eqind = htext.indexOf("="); // we would prefer a regex to not include "==" probs
            	  if (eqind==-1) {
            		  System.out.println("didn't find =");
            		  // now we want to find the first instance of whitespace followed by numerical values
            		  Matcher m = eqPattern.matcher(htext);
            		  if (m.find()) {
            			  int st = m.start();
            			  if (st!=-1) {
            				  System.out.println("found \\s\\d at: " + st);
            				  htext = htext.substring(0,st+1) + "=" + htext.substring(st+1);
            				  System.out.println("new line is: " + htext);
            			  }
            		  }
            	  } 
        	  }
        	  // done w/ =
        	  htext = htext.replaceAll("\\s+", "");
        	  System.out.println("have a single line: " + htext);
          }
          // let's try to evaluate the string
          try {
            res = engine.eval(htext).toString();
          } catch (ScriptException e) {
            res = htext;
          }
          System.out.println("Eval result is: " + res);
          return res;
        }
      } catch (BadLocationException x) {
      }
    }
    return "empty info"; 
  }

  @Override
  public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
  Point selection = textViewer.getSelectedRange();
  if (selection.x <= offset && offset < selection.x + selection.y)
    return new Region(selection.x, selection.y);
  return new Region(offset, 0);
  }
}
