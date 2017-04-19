# RuleBender and RuleBased Modeling #
Rule-based modeling (RBM) is a powerful and increasingly popular approach to modeling intracellular biochemistry. Current interfaces for RBM are predominantly text-based and command-line driven. Better visual tools are needed to make RBM accessible to a broad range of users, to make specification of models less error prone, and to improve workflows. We present `RuleBender`, an open-source visual interface that facilitates interactive debugging, simulation, and analysis of RBMs.

For more information and links to the executables visit http://rulebender.org.


#  Upgrading Java on OS X (Apple Macintosh) #

If you start RuleBender and you see a dialogue box that indicates that you
need to upgrade Java on your system, you may first want to be aware that
when you do the upgrade, your upgraded version of Java will co-exist with
the Java 1.6 runtime environment that is mandatory for the Macintosh.  There
are certain utilities that need to access the old version.  Fortunately,
following the usual instructions for installing either the Java Runtime
Environment (JRE) or the Java Development Kit (JDK) will not have any
effect on the Java 1.6 that's already installed on your system.

The website:   java.org  has the installation packages that you need.

The installation package for the JRE is smaller than
the installation package for the JDK.  This means that you
can save some space on your hard disk by installing the JRE rather than the
JDK.  But the JDK has an installer program that may eliminate some of the
installation steps.

Instructions for installing the JRE can be found here (Note: these require admin/root access to your machine):
https://oliverdowling.com.au/2015/10/09/oracles-jre-8-on-mac-os-x-el-capitan/

Instructions for installing the JDK can be found here (Note: installing JDK from java.org should work for running RuleBender - these additional steps are not required.):
https://oliverdowling.com.au/2015/10/09/oracles-jdk-8-on-mac-os-x-el-capitan/



# Installing Perl #

If you run RuleBender on Windows (or on any sytem for that matter) you may
get a "Perl Not Found" dialogue box when you firstr bring up RuleBender. Perl
is needed to process the simulation script files.

For Windows users, we recommend that you download Perl from

strawberryperl.org

Linux and Mac users can go to perl.org to find Perl for their system.

