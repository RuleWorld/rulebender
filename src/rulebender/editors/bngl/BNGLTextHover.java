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


public class BNGLTextHover implements ITextHover
{
  private ScriptEngineManager mgr = new ScriptEngineManager();
  private ScriptEngine engine = mgr.getEngineByName("JavaScript");
          
  public BNGLTextHover() 
  {
    super();
  }
                     
                       
                          
  @Override
  public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
    if (hoverRegion != null) {
      try {
        if (hoverRegion.getLength() > -1) {
          String htext, res;
          htext = textViewer.getDocument().get(hoverRegion.getOffset(), hoverRegion.getLength());
          System.out.println("We are hovering over: " + htext);
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
