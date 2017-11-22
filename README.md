# Description #
RuleBender provides an easy-to-use interface for the popular rule-based modeling software package, BioNetGen. It provides an editor with context-sensitive highlighting and syntax checking along with automated model visualization, simulation, and plotting capabilities. RuleBender also provides an automatic journaling capability that keeps all model files and simulation data in a common, chronologically-ordered workspace. RuleBender is written in Java using the Eclipse Rich Client Platform. Installation packages for Windows, Mac, and Linux operating systems can be found at http://bionetgen.org along with installation instructions, documentation, and instructional videos. Below are detailed instructions for building RuleBender using Eclipse. 

# Build Process #

Downloading Eclipse

1-) Go to https://www.eclipse.org/downloads/packages/release/neon/3 .

2-) Download Eclipse Neon RCP and RAP Developers for your system. 

Downloading Rulebender, creating directories and building parser

3-) Go to your terminal create a directory “workspace_rb_1_git” and “workspace_rb_1”. 

4-) Go to “workspace_rb_1_git” directory. 

5-) Clone the repository with the following: (Slow step)

git clone https://github.com/RuleWorld/rulebender.git 

6-) Go to “rulebender” directory and run the following: 

./s_build_BNGParser.sh 

Creating and importing project

7-) Go to eclipse directory and run eclipse from terminal. (For mac you can add eclipse as an application)

8-) Set your workspace to your recently created “workspace_rb_1”.

9-) Close welcome screen. 

10-) Click “File -> New -> Project” 

11-) Select “General -> Project” and click “Next”. Give project name “RuleBender”. Click “Finish”

For Linux, 

12-) In Project Explorer Window, right click on the RuleBender and click “Import” . 

13-) Select “General -> File System” and click “Next”. On the next screen locate the git repository you downloaded. From left panel, check the rulebender to import all files. Click “Finish” . 

For macos, 

12-) On a finder window, go to “workspace_rb_1_git/rulebender”. Select all and copy. 

13-) Right click “RuleBender” in “Project Explorer”

14-) Double click “RuleBender” in Project Explorer. Then, double click “rulebender.product”. At the bottom of middle window, go to “Launching”. 

15-) On Program Launcher menu, 
Select linux and click “Browse”, pick “Rulebender/icons/system/linux/RB-128.xpm”
Select macosx and click “Browse”, pick “Rulebender/icons/system/osx/RB-128.icns”
Select windows and do the same for
16x16, 8 bit
16x16
32x32, 8 bit
32x32
48x48, 8 bit
48x48

Multiplatform building

16-) For linux open Window/Preferences, for mac go Eclipse/Preferences. 

17-) Go plug-in development -> Target Platform. Select Running Platform (Active) and click on “Edit”. 

18-) Click “add” and choose “Software Site”. Click “Next”. 

19-) In “Work with” type: 
http://download.eclipse.org/eclipse/updates/4.6 (replace 4.6 with current version of eclipse)
And press enter. 

20-) Check “Eclipse RCP Target Components”, “Equinox Target Components”!!!!!!!!

21-) Uncheck “Include required software”

22-) Check “Include all environments”. Click “Finish”, “Finish” and “OK”. 


Exporting 

23-) From the bottom of middle menu, go to “Contents” and click “Add Required  Plug-ins”. 

24-) In Project explorer, right click “rulebender.product” and choose “export” 

25-) Select “Plug-in Development -> Eclipse product”. Click “Next”. 

26-) Choose destination directory which is “workspace_rb_1/RuleBender” and check “Export for multiple platforms”. Click “Next”. 

27-) Select macosx, linux(gtk/x86_64) and both win32 versions. Click “Finish.” It may ask for save changes, click “Yes”.  

Packaging

28-) 
