#!/bin/bash
# This is a quick and dirty script for prepping the release files for RB.  It can be 
# improved or a more robust build system that works with eclipse builder (and is windows
# friendly) can be used in the long run, but for now this saves a ton of time.
# 
# Build Step 1: Make sure that BNG and NFSim are at most recent version.
# Build Step 2: Make sure that BNGParser.jar is at most recent version.
# Build Step 3: Prepare the eclipse files for release through the plugin development environment.
# Build Step 4: Put the files in the right place (see below) and 
# Build Step 5: run this script.

# The release that eclipse generates should be in rbReleaseDir ("RB-Release" here) in the 
# root rulebender trunk, and the script should also be run in the root (e.g. parent of
# rbReleaseDir).

# This script does the following:
# 1. Remove the jre folders from the eclipse output.
# 2. Create a 'zip' directory and move all of the eclipse outputs to that directory
#    while renaming the package names to RuleBender-<version>-<platform>
# 3. Copy the simulation resources (BNG, NFSim, and SampleModels) to each of the packages,
#    and delete the irrelevant binaries for each platform.
# 4. COPY the CREDITS.txt, RB-README.txt, and LICENSE.txt files. 


echo "Stop !! The header lines of this script need to be changed."
exit



# version number only used for file names.  All other version branding is done in
# the .project file.
              version="2.2"
         rbReleaseDir="/home/roc60/workspace_rb_68/RuleBender"
distributionResources="/home/roc60/workspace_rb_68_git/rulebender/distributionResources"
           bngdirname="BioNetGen-2.3"
          dropboxroot="/home/roc60/BioNetGen.latest"
            java_root="/home/roc60/d_java"


echo "Creating zip dir"
# make an output folder so you don't eff up the dirs
chmod -R +w $rbReleaseDir/zips
rm -r       $rbReleaseDir/zips
mkdir       $rbReleaseDir/zips



echo "Copying release files to zip dir and renaming to platform and version names"
echo "This copy must come first, since it creates the directories."
# rename and move all of the RuleBender dirs
cp -r  $rbReleaseDir/linux.gtk.x86_64/eclipse/    \
         $rbReleaseDir/zips/RuleBender-$version-lin64/
cp -r  $rbReleaseDir/macosx.cocoa.x86_64/eclipse/ \
         $rbReleaseDir/zips/RuleBender-$version-osx64/
cp -r  $rbReleaseDir/win32.win32.x86_64/eclipse/     \
         $rbReleaseDir/zips/RuleBender-$version-win64/
cp -r  $rbReleaseDir/win32.win32.x86/eclipse/     \
         $rbReleaseDir/zips/RuleBender-$version-win32/

echo "Copying BioNetGen " 

cp -r $dropboxroot/$bngdirname-Linux     \
             $rbReleaseDir/zips/RuleBender-$version-lin64/$bngdirname
chmod +w -R  $rbReleaseDir/zips/RuleBender-$version-lin64/$bngdirname
cp -r $dropboxroot/$bngdirname-MacOSX    \
             $rbReleaseDir/zips/RuleBender-$version-osx64/$bngdirname
chmod +w -R  $rbReleaseDir/zips/RuleBender-$version-osx64/$bngdirname
cp -r $dropboxroot/$bngdirname-Win64   \
             $rbReleaseDir/zips/RuleBender-$version-win64/$bngdirname
chmod +w -R  $rbReleaseDir/zips/RuleBender-$version-win64/$bngdirname
cp -r $dropboxroot/$bngdirname-Win32   \
             $rbReleaseDir/zips/RuleBender-$version-win32/$bngdirname
chmod +w -R  $rbReleaseDir/zips/RuleBender-$version-win32/$bngdirname


echo "Copying NFsim Source"

cp -r $dropboxroot/NFsim_source        \
             $rbReleaseDir/zips/RuleBender-$version-lin64/$bngdirname
chmod +w -R  $rbReleaseDir/zips/RuleBender-$version-lin64/$bngdirname/NFsim_source
cp -r $dropboxroot/NFsim_source        \
             $rbReleaseDir/zips/RuleBender-$version-osx64/$bngdirname
chmod +w -R  $rbReleaseDir/zips/RuleBender-$version-osx64/$bngdirname/NFsim_source
cp -r $dropboxroot/NFsim_source        \
             $rbReleaseDir/zips/RuleBender-$version-win64/$bngdirname
chmod +w -R  $rbReleaseDir/zips/RuleBender-$version-win64/$bngdirname/NFsim_source
cp -r $dropboxroot/NFsim_source        \
             $rbReleaseDir/zips/RuleBender-$version-win32/$bngdirname
chmod +w -R  $rbReleaseDir/zips/RuleBender-$version-win32/$bngdirname/NFsim_source


echo "Copying RB-README.txt, LICENSE.txt, and CREDITS.txt"

cp $distributionResources/*.txt  $rbReleaseDir/zips/RuleBender-$version-lin64
cp $distributionResources/*.txt  $rbReleaseDir/zips/RuleBender-$version-osx64
cp $distributionResources/*.txt  $rbReleaseDir/zips/RuleBender-$version-win64
cp $distributionResources/*.txt  $rbReleaseDir/zips/RuleBender-$version-win32

cp $distributionResources/Samples/*.bngl \
        $rbReleaseDir/zips/RuleBender-$version-lin64/$bngdirname/Models2
cp $distributionResources/Samples/*.bngl \
        $rbReleaseDir/zips/RuleBender-$version-osx64/$bngdirname/Models2
cp $distributionResources/Samples/*.bngl \
        $rbReleaseDir/zips/RuleBender-$version-win64/$bngdirname/Models2
cp $distributionResources/Samples/*.bngl \
        $rbReleaseDir/zips/RuleBender-$version-win32/$bngdirname/Models2


echo "Copying SampleModels"

cp -r $distributionResources/Simulation/SampleModels \
      $rbReleaseDir/zips/RuleBender-$version-lin64
cp -r $distributionResources/Simulation/SampleModels \
      $rbReleaseDir/zips/RuleBender-$version-osx64
cp -r $distributionResources/Simulation/SampleModels \
      $rbReleaseDir/zips/RuleBender-$version-win64
cp -r $distributionResources/Simulation/SampleModels \
      $rbReleaseDir/zips/RuleBender-$version-win32

# ###################################################################
#   Apple specific instructions
# ###################################################################
cd $rbReleaseDir/zips/RuleBender-$version-osx64/RuleBender.app/Contents/MacOS
ln -s ../../../$bngdirname   $bngdirname 
cp -r $java_root/java_jre_osx64 .
mv RuleBender.ini  RuleBender.ini.safe
head -7            RuleBender.ini.safe > RuleBender.ini
echo "-clearPersistedState"    >>        RuleBender.ini
echo "-vm"                     >>        RuleBender.ini
echo "./java_jre_osx64/jdk1.8.0_72.jdk/Contents/Home/jre/lib/jli/libjli.dylib" >> RuleBender.ini
tail -7           RuleBender.ini.safe >> RuleBender.ini


# ###################################################################
#   Linux specific instructions
# ###################################################################
cd $rbReleaseDir/zips/RuleBender-$version-lin64
cp -r $java_root/java_jre_lin64 .
mv RuleBender.ini  RuleBender.ini.safe
head -7            RuleBender.ini.safe > RuleBender.ini
echo "-clearPersistedState"    >>        RuleBender.ini
echo "-vm"                     >>        RuleBender.ini
echo "./java_jre_lin64/jdk1.8.0_131/jre/bin/java" >> RuleBender.ini
tail -3           RuleBender.ini.safe >> RuleBender.ini


# ###################################################################
#   Win32 specific instructions
# ###################################################################
cd $rbReleaseDir/zips/RuleBender-$version-win32
cp -r $java_root/java_jre_win32 .
mv RuleBender.ini  RuleBender.ini.safe
head -8            RuleBender.ini.safe > RuleBender.ini
echo "-clearPersistedState"    >>        RuleBender.ini
echo "-vm"                     >>        RuleBender.ini
echo ".\jre1.8.0_40\bin\java"  >> RuleBender.ini
tail -3           RuleBender.ini.safe >> RuleBender.ini


# ###################################################################
#   Win64 specific instructions
# ###################################################################
cd $rbReleaseDir/zips/RuleBender-$version-win64
cp -r $java_root/java_jre_win64 .
mv RuleBender.ini  RuleBender.ini.safe
head -8            RuleBender.ini.safe > RuleBender.ini
echo "-clearPersistedState"    >>        RuleBender.ini
echo "-vm"                     >>        RuleBender.ini
echo ".\jdk1.8.0_121\bin\java" >> RuleBender.ini
tail -3           RuleBender.ini.safe >> RuleBender.ini


#cd to zips dir to avoid more dirs in zip
cd $rbReleaseDir/zips/

# The rm statements are not needed, since the whole zip directory is new.
#rm  RuleBender-$version-lin64.tar.gz 
#rm  RuleBender-$version-osx64.tar.gz  
#rm  RuleBender-$version-win32.zip     

tar -czf  RuleBender-$version-lin64.tar.gz  RuleBender-$version-lin64
tar -czf  RuleBender-$version-osx64.tar.gz  RuleBender-$version-osx64
zip -r -q RuleBender-$version-win64.zip     RuleBender-$version-win64
zip -r -q RuleBender-$version-win32.zip     RuleBender-$version-win32


cd  RuleBender-$version-lin64 ; tar -czf  $bngdirname-lin64.tar.gz $bngdirname ; mv $bngdirname-lin64.tar.gz ../. ; cd ..
cd  RuleBender-$version-osx64 ; tar -czf  $bngdirname-osx64.tar.gz $bngdirname ; mv $bngdirname-osx64.tar.gz ../. ; cd ..
cd  RuleBender-$version-win64 ; zip -r -q $bngdirname-win64.zip    $bngdirname ; mv $bngdirname-win64.zip    ../. ; cd ..
cd  RuleBender-$version-win32 ; zip -r -q $bngdirname-win32.zip    $bngdirname ; mv $bngdirname-win32.zip    ../. ; cd ..
