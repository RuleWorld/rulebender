#!/bin/bash
# #########################################################################
#   Build Script for BNGParser
# #########################################################################
echo ' '
echo ' '
echo ' '
echo ' This script should be called right after cloning the GitHub code.'
echo ' Building the parser before importing the code into Eclipse will'
echo ' ensure that Eclipse is using the right parser.'
echo ' '

/bin/rm -r -f  BNGParser
git clone https://github.com/RuleWorld/BNGParser.git
cd BNGParser/src/bngparser/grammars

echo '-------------------------------------------------------------------'
echo ' Generate grammars'
echo '-------------------------------------------------------------------'

java -jar  ../../antlr-3.3-complete.jar BNGLexer.g
java -jar  ../../antlr-3.3-complete.jar BNGGrammar.g

echo '-------------------------------------------------------------------'
echo ' Compile grammars'
echo '-------------------------------------------------------------------'

cd ../..
for jfile in $(ls bngparser/grammars/*.java)
do
echo $jfile
javac    -classpath  bngparser/grammars:bngparser/methods:bngparser/models:bngparser/dataType:bngparser/exceptions:antlr-3.3-complete.jar:../commons-lang3-3.1.jar:.  -sourcepath . $jfile
done

echo '-------------------------------------------------------------------'
echo ' Compiling methods '
echo '-------------------------------------------------------------------'

for jfile in $(ls bngparser/methods/*.java)
do
  echo $jfile
javac    -classpath  bngparser/grammars:bngparser/methods:bngparser/models:bngparser/dataType:bngparser/exceptions:antlr-3.3-complete.jar:../commons-lang3-3.1.jar:.  -sourcepath . $jfile
done

echo '-------------------------------------------------------------------'
echo ' Compiling dataType '
echo '-------------------------------------------------------------------'

for jfile in $(ls bngparser/dataType/*.java)
do
  echo $jfile
javac    -classpath  bngparser/grammars:bngparser/methods:bngparser/models:bngparser/dataType:bngparser/exceptions:antlr-3.3-complete.jar:../commons-lang3-3.1.jar:.  -sourcepath . $jfile
done

echo '-------------------------------------------------------------------'
echo ' Compiling exceptions '
echo '-------------------------------------------------------------------'

for jfile in $(ls bngparser/exceptions/*.java)
do
  echo $jfile
javac    -classpath  bngparser/grammars:bngparser/methods:bngparser/models:bngparser/dataType:bngparser/exceptions:antlr-3.3-complete.jar:../commons-lang3-3.1.jar:.  -sourcepath . $jfile
done

echo '-------------------------------------------------------------------'
echo ' Compiling bngparser '
echo '-------------------------------------------------------------------'

for jfile in $(ls bngparser/*.java)
do
  if [ $jfile != 'bngparser/MCellTranslatorTester.java' ];
then
  if [ $jfile != 'bngparser/Tester.java' ];
then
echo $jfile
javac    -classpath  bngparser/grammars:bngparser/methods:bngparser/models:bngparser/dataType:bngparser/exceptions:antlr-3.3-complete.jar:../commons-lang3-3.1.jar:.  -sourcepath . $jfile
fi
fi
done

echo '-------------------------------------------------------------------'
echo ' Creating jar '
echo '-------------------------------------------------------------------'

cp ../xml.stg .
jar cf  BNGParser.jar bngparser  xml.stg

echo '-------------------------------------------------------------------'
echo ' Running a Test '
echo '-------------------------------------------------------------------'

cp ../commons-lang3-3.1.jar .
cat bngparser/Tester.java | sed 's/testModels/\.\.\/testModels/' > bngparser/Tester_new.java 
mv bngparser/Tester.java      bngparser/Tester.old.java
mv bngparser/Tester_new.java  bngparser/Tester.java

javac -cp antlr-3.3-complete.jar:BNGParser.jar:commons-lang3-3.1.jar:. \
    bngparser/Tester.java 
java  -cp antlr-3.3-complete.jar:BNGParser.jar:commons-lang3-3.1.jar:. \
    bngparser/Tester

echo '-------------------------------------------------------------------'
echo ' Deploy BNGParser.jar '
echo '-------------------------------------------------------------------'

cd ../..
echo ' '
echo ' Moving lib/BNGParser.jar to archive/BNGParser.old.jar '
rm -f archive/BNGParser.old.jar
mv    lib/BNGParser.jar  archive/BNGParser.old.jar

echo ' Copying the newly built parser to lib/BNGParser.jar '
cp   BNGParser/src/BNGParser.jar  lib/BNGParser.jar
echo ' '

echo '-------------------------------------------------------------------'
echo ' Done '
echo '-------------------------------------------------------------------'
