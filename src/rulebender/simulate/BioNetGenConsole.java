package rulebender.simulate;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.console.MessageConsoleStream;

import rulebender.core.utility.Console;
import rulebender.logging.Logger;
import rulebender.logging.Logger.LOG_LEVELS;
import rulebender.preferences.PreferencesClerk;

public class BioNetGenConsole {

	private static Process bngConsoleProcess = null;
	private static OutputStreamWriter writer = null;
	private static ConsoleReader out = null;
	public static long creationTimeOut = 5000;
	public static long check = 100;
	private static File currentModel = null;

	private static void invokeBNGConsole() {
		 String     bngPath   = PreferencesClerk.getFullDefaultBNGPath();
		 String     bngPath2  = PreferencesClerk.getFullUserBNGPath();
		//String     bngPath1 = PreferencesClerk.getBNGPath();
		//String     bngPath2 = PreferencesClerk.getBNGRoot();
		// String bngPath = bng.toString();
		 
//		 System.out.println(" bngPath " + bngPath);
//		 System.out.println(" bngPath2 " + bngPath2);

		boolean prereq = BioNetGenUtility.checkPreReq();
		boolean bng  = validateBNGPath(bngPath);
		boolean bng2 = validateBNGPath(bngPath2);

		if ((bng || bng2) && prereq) {

			Console.displayOutput(currentModel.toString(), 
		     "\nBioNetGen has been located on your system. ");


			List<String> commands = new ArrayList<String>();
			commands.add("perl");
			if (bng) { commands.add(bngPath); }
			else     { commands.add(bngPath2); }
			commands.add("-console");
			ProcessBuilder builder = new ProcessBuilder(commands);
			try {
				bngConsoleProcess = builder.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
			writer = new OutputStreamWriter(bngConsoleProcess.getOutputStream());
			out = new ConsoleReader(bngConsoleProcess.getInputStream());
			out.start();
		}

		else if (bng) {
			MessageBox errorMessage = new MessageBox(Display.getDefault()
			    .getActiveShell(), SWT.ICON_ERROR | SWT.OK);
			errorMessage.setText("Perl Not Found");
			errorMessage
			    .setMessage("Warning: Was not able to locate Perl on your system.");
			errorMessage.open();
		} else {
			Console.displayOutput(currentModel.toString(), "BioNetGen Not Found\n\n"
			    + "Warning: Was not able to locate BioNetGen in " + bngPath
			    + ".\nThe Contact Map cannot be displayed if BioNetGen is not "
			    + "included in the RuleBender path.\n"
			    + "To add BioNetGen to the path click on "
			    + "'Simulator' under 'Preferences'.\n\n\n");


			
			//			Console.displayOutput(currentModel.toString(), 
 //                              "FullPath " + bngPath + "\n\n");
//			Console.displayOutput(currentModel.toString(), 
 //                              "Path " + bngPath1 + "\n\n");
//			Console.displayOutput(currentModel.toString(), 
 //                              "Root " + bngPath2 + "\n\n");
		}

	}

	private static boolean validateBNGPath(String path) {
		if ((new File(path)).exists()) {
			return true;
		}
		return false;
	}

	public static File generateXML(File bngModel, MessageConsoleStream errorStream) {
		currentModel = bngModel;
		if (!prepareConsole()) {
			return null;
		} 
		if (out != null) { out.clearLineNumbers(); }
		
		
		// If a previous error was thrown, but not reported yet.
		out.reportError();
		String fileName = bngModel.getParentFile().toString() + "/"
		    + bngModel.getName().substring(0, bngModel.getName().indexOf(".bngl"));
		File xmlFile = new File(fileName + ".xml");
		if (xmlFile.exists()) {
			xmlFile.delete();
		}
		String writeXML = "writeXML({prefix=>\"" + fileName.replace("\\", "/")
		    + "\"})";
		// String net = xmlFile.toString();
		// net = net.substring(0, net.length() - 3) + "net";
		xmlFile.deleteOnExit();
		clearModel();
		readModel(bngModel);
		// String networkGen = "generate_network({" + "overwrite=>1,file=>\"" + net
		// + "\"})";
		// executeAction(networkGen);
		executeAction(writeXML);
		long waitTime = 0;
		while (!xmlFile.exists()) {
			try {
				waitTime += check;
				Thread.sleep(check);
				if (out.hadError()) {
					String err = out.getError();
					errorStream.println(err);
					// throw new Error("An error occurred while processing the file: " +
					// err);
					out.reportError();
					xmlFile = null;
					break;
				} else if (waitTime > creationTimeOut) {
					Logger.log(LOG_LEVELS.ERROR, BioNetGenConsole.class,
					    "Wasn't able to create the xml-file for the model located at: \n"
					        + bngModel.toString() + "\n"
					        + "You should use BioNetGen 2.2.5 or later."
					        + " Also check for errors in your model.");
					Console.getMessageConsoleStream(bngModel.toString()).print(
					    "Wasn't able to create the xml-file for the model located at: \n"
					        + bngModel.toString() + "\n"
					        + "You should use BioNetGen 2.2.5 or later."
					        + " Also check for errors in your model.");
					// throw new Error(
					// "Wasn't able to create the xml-file for the model located at: "
					// + bngModel.toString());
					xmlFile = null;
					break;
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		String warning = out.getWarnings();
		out.reportWarnings();
		errorStream.println(warning);
		return xmlFile;
	}

	public static void clearModel() {
		if (prepareConsole()) {
			write("clear");
			currentModel = null;
		}

		out.reportWarnings();
	}

	public static void readModel(File bngModel) {
		if (prepareConsole()) {
			currentModel = bngModel;
			write("load " + bngModel.toString());
		}
	}

	public static void executeAction(String action) {
		if (prepareConsole()) {
			write("action " + action);
		}
	}

	private static boolean prepareConsole() {
		if (bngConsoleProcess == null) {
			invokeBNGConsole();
			if (bngConsoleProcess == null) {
				return false;
			}
		}
		return true;
	}

	public static void write(String s) {
		try {
			writer.write(s + "\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

        public static String getLineNumbers() {
          if (out != null) {
            return out.getLineNumbers();
          } else {
            return ""; 
          }
        }

}
