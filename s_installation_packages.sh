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
version="2.1.0"
         rbReleaseDir="/home/roc60/workspace_rb_15/RuleBender"
distributionResources="/home/roc60/workspace_rb_15_git/rulebender/distributionResources"
             bngdirname="BioNetGen-2.2.6"
dropboxroot="/home/roc60/BioNetGen-2.2.6-safe"
#dropboxroot="/home/roc60/Dropbox/BioNetGen-2_2_6/Stable"


echo "Creating zip dir"
# make an output folder so you don't eff up the dirs
rm -r $rbReleaseDir/zips
mkdir $rbReleaseDir/zips



echo "Copying release files to zip dir and renaming to platform and version names"
echo "This copy must come first, since it creates the directories."
# rename and move all of the RuleBender dirs
cp -r  $rbReleaseDir/linux.gtk.x86_64/eclipse/    \
         $rbReleaseDir/zips/RuleBender-$version-lin64/
cp -r  $rbReleaseDir/macosx.cocoa.x86_64/eclipse/ \
         $rbReleaseDir/zips/RuleBender-$version-osx64/
cp -r  $rbReleaseDir/win32.win32.x86/eclipse/     \
         $rbReleaseDir/zips/RuleBender-$version-win32/

echo "Copying BioNetGen " 

cp -r $dropboxroot/Linux/BioNetGen-2.2.6-stable     \
             $rbReleaseDir/zips/RuleBender-$version-lin64/BioNetGen-2.2.6
chmod +w -R  $rbReleaseDir/zips/RuleBender-$version-lin64/BioNetGen-2.2.6
cp -r $dropboxroot/MacOSX/BioNetGen-2.2.6-stable    \
             $rbReleaseDir/zips/RuleBender-$version-osx64/BioNetGen-2.2.6
chmod +w -R  $rbReleaseDir/zips/RuleBender-$version-osx64/BioNetGen-2.2.6
cp -r $dropboxroot/Windows/BioNetGen-2.2.6-stable   \
             $rbReleaseDir/zips/RuleBender-$version-win32/BioNetGen-2.2.6
chmod +w -R  $rbReleaseDir/zips/RuleBender-$version-win32/BioNetGen-2.2.6



echo "Copying RB-README.txt, LICENSE.txt, and CREDITS.txt"

cp $distributionResources/*.txt $rbReleaseDir/zips/RuleBender-$version-lin64
cp $distributionResources/*.txt $rbReleaseDir/zips/RuleBender-$version-osx64
cp $distributionResources/*.txt $rbReleaseDir/zips/RuleBender-$version-win32


echo "Copying SampleModels"

cp -r $distributionResources/Simulation/SampleModels \
      $rbReleaseDir/zips/RuleBender-$version-lin64
cp -r $distributionResources/Simulation/SampleModels \
      $rbReleaseDir/zips/RuleBender-$version-osx64
cp -r $distributionResources/Simulation/SampleModels \
      $rbReleaseDir/zips/RuleBender-$version-win32


cd $rbReleaseDir/zips/RuleBender-$version-osx64/RuleBender.app/Contents/MacOS
ln -s ../../../$bngdirname   $bngdirname   

#cd to zips dir to avoid more dirs in zip
cd $rbReleaseDir/zips/

# The rm statements are not needed, since the whole zip directory is new.
#rm  RuleBender-$version-lin64.tar.gz 
#rm  RuleBender-$version-osx64.tar.gz  
#rm  RuleBender-$version-win32.zip     

tar -czf  RuleBender-$version-lin64.tar.gz  RuleBender-$version-lin64
tar -czf  RuleBender-$version-osx64.tar.gz  RuleBender-$version-osx64
zip -r -q RuleBender-$version-win32.zip     RuleBender-$version-win32


# remove the any svn files
# find . -type d -name .svn -exec rm -rf '{}' \;

