# Introduction #

This wiki includes instructions for setting up your development environment, checking out the code, and setting up the RuleBender project on you machine.

## Install Eclipse ##
  1. Download Eclipse version 3.7 from eclipse.org.
  1. Unzip Eclipse wherever you want it to be installed.
  1. Run Eclipse.
  1. See the Eclipse documentation if you have trouble.

## Install svn for Eclipse ##
  1. In the Eclipse menu bar, click 'Help' and then 'Install New Software...'
  1. In the 'Work with:' field, select the update site for your Eclipse version (e.g. Indigo - http://download.eclipse.org/releases/indigo)
  1. Expand the 'Collaboration' row, and select the checkbox for 'Subversive SVN Team Provider'.  Click next and follow all instructions and accept all agreements until it asks you to restart Eclipse.
  1. When Eclipse restarts, right click on the project explorer and select 'Import...'.  Click on the option to import from SVN.  Before you can actually import you will need to follow the direction for installing the SVN connectors, and then restart Eclipse again.

## Import project from SVN ##
  1. After installing the team provider and the SVN connectors you can actually import from SVN.  Use the repository location that is displayed in the 'Source' tab of the RuleBender googlecode page (https://rulebender.googlecode.com/svn/trunk/)
  1. If you have commit rights, you can enter your username and password to authenticate with the system.
  1. Eclipse may ask you to truncate the svn location, but this will force you to checkout the entire repository when you really only want to get the trunk, a branch, or a tag depending on what you're doing.
  1. Import it as a Java project using the wizard or any other method that you are comfortable with.

## Set up the project ##
  1. Right click on the project and select properties->java build path
  1. Add lib/`*`.jar to build path.
  1. Click 'Add Library...' and select plugin dependencies.

At this point there should not be any compile errors.  To run the project, open the plugin.xml file and click the run button in the top right corner.