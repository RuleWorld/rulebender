# Description #
RuleBender provides an easy-to-use interface for the popular rule-based
modeling software package, BioNetGen. It provides an editor with
context-sensitive highlighting and syntax checking along with automated model
visualization, simulation, and plotting capabilities. RuleBender also provides
an automatic journaling capability that keeps all model files and simulation
data in a common, chronologically-ordered workspace. RuleBender is written in
Java using the Eclipse Rich Client Platform. Installation packages for Windows,
Mac, and Linux operating systems can be found at http://bionetgen.org along
with installation instructions, documentation, and instructional videos. Below
are detailed instructions for building RuleBender using Eclipse. 

# Build Process #

## Dependencies

### Ubuntu

Run the following to install the JRE and JDK:

    sudo apt-get update
    sudo apt-get install default-jre
    sudo apt-get install default-jdk

## Downloading Eclipse ##

1. Go to https://www.eclipse.org/downloads/packages/release/neon/3.

2. Download Eclipse Neon RCP and RAP Developers for your system. 

## Downloading Rulebender, creating directories and building the parser ##

3. Go to your terminal create a directory `workspace_rb_1_git` and
   `workspace_rb_1` like this:

        mkdir ~/workspace_rb_1
        mkdir ~/workspace_rb_1_git

4. Go to `workspace_rb_1_git` directory like this:

        cd ~/workspace_rb_1_git

5. Clone the RuleBender repository with the following command:

        git clone https://github.com/RuleWorld/rulebender.git 

6. Go to the `rulebender` directory and build the parser: 

        cd ./rulebender
        ./s_build_BNGParser.sh 

## Creating and importing an Eclipse project ##

7. Go to eclipse directory and run eclipse from terminal (For mac you can add
   eclipse as an application)

8. Set your workspace to your recently created `workspace_rb_1`.

9. Close the welcome screen. 

10. Click `File -> New -> Project` 

11. Select `General -> Project` and click `Next`. Give project name
    `RuleBender`. Click `Finish`.

### For Linux ###

12. In the `Project Explorer Window`, right click on RuleBender and click
    `Import`. 

13. Select `General -> File System` and click `Next`. On the next screen locate
    the git repository you cloned (i.e. `~/workspace_rb_1_git/rulebender`).
    From the left panel, click the checkbox next to `rulebender` to import all
    files. Click `Finish` . 

### For MacOS ###

12. On a finder window, go to `workspace_rb_1_git/rulebender`. Select all and
    copy. 

13. Right click `RuleBender` in `Project Explorer`

## Setting the Icons

14. Double click `RuleBender` in Project Explorer. Then, double click
    `rulebender.product`. Near the bottom of the middle window, go to the
    `Launching` tab. 

15. At the `Program Launcher` menu, do the following:

  - Select linux and click `Browse`, pick `Rulebender/icons/system/linux/RB-128.xpm`
  - Select macosx and click `Browse`, pick `Rulebender/icons/system/osx/RB-128.icns`
  - Select windows and do the same for the following:
    - 16x16  (8 bit)
    - 16x16  (32 bit)
    - 32x32  (8 bit)
    - 32x32  (32 bit)
    - 48x48  (8 bit)
    - 48x48  (32 bit)

## Multiplatform building ##

16. For linux open `Window -> Preferences`, for mac go `Eclipse ->
    Preferences`. 

17. Go to `Plug-in Development -> Target Platform`. Select `Running Platform
    (Active)` and click on `Edit`. 

18. Click `Add...` and choose `Software Site`. Click `Next`. 

19. In `Work with` type: `http://download.eclipse.org/eclipse/updates/4.6`
    (replace 4.6 with current version of eclipse) and press Enter. 

20. **Important:** Check `Eclipse RCP Target Components` and `Equinox Target
    Components`

21. Uncheck `Include required software`

22. Check `Include all environments`. Click `Finish`, `Finish`, and `OK`. 

## Exporting ##

23. Near the bottom of the middle menu, go to `Contents` and click `Add
    Required Plug-ins`. 

24. In `Project Explorer`, right click `rulebender.product` and choose `Export` 

25. Select `Plug-in Development -> Eclipse product`. Click `Next`. 

26. Choose destination directory which is `workspace_rb_1/RuleBender` and check
    `Export for multiple platforms`. Click `Next`. 

27. Select macosx, linux(gtk/x86_64) and both win32 versions. Click `Finish.`
    If it asks you to save changes, click `Yes`.  

## Packaging ##

28. Download the [Java SE Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
    for Linux, OSX, and Windows (32-bit and 64-bit).

29. Create a directory called `~/d_java` and do the following:

    - Extract the linux archive to `~/d_java/java_jre_lin64/`
    - Extract the osx archive to `~/d_java/java_jre_osx64/`
    - Extract the win32 archive to `~/d_java/java_jre_win32/`
    - Extract the win64 archive to `~/d_java/java_jre_win64/`

30. Download the latest versions of
    [BioNetGen](https://bintray.com/jczech/bionetgen/bionetgen#files) for
    Linux, OSX, and Windows.

31. Create a directory called `~/BioNetGen.latest` and do the following:

    - Extract the linux archive to `~/BioNetGen.latest/Linux/`
    - Extract the osx archive to `~/BioNetGen.latest/OSX/`
    - Extract the win32 archive to `~/BioNetGen.latest/Win32/`
    - Extract the win64 archive to `~/BioNetGen.latest/Win64/`

31. Run the `s_installation_packages.sh` script. This will create RuleBender
    distributions in `~/workspace_rb_1/RuleBender/zips`.
