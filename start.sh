#!/bin/bash


if [ -z "$1" ]
then
  DEFAULT_CLASSNAME="com.appdynamics.extensions.extensionsstarter.ExtStarterMonitor"
  read -p "Classname of your extension (default: ${DEFAULT_CLASSNAME}): " INPUT_CLASSNAME
  CLASSNAME=${INPUT_CLASSNAME:-$DEFAULT_CLASSNAME}
else
  CLASSNAME="$1"
fi;

DIRECTORY_AND_FILEPREFIX=${CLASSNAME//./\/}

DIRECTORY=`dirname ${DIRECTORY_AND_FILEPREFIX}`
NAME=`basename ${DIRECTORY_AND_FILEPREFIX}`

GROUP_DIRECTORY=`dirname ${DIRECTORY}`
ARTIFACT=`basename ${DIRECTORY}`

GROUP=${GROUP_DIRECTORY//\//.}

PACKAGE=${DIRECTORY//\//.}

if [ -z "$2" ]
then
  DEFAULT_OUTPUT_DIRECTORY="./${NAME}"
  read -p "Output directory (default ${DEFAULT_OUTPUT_DIRECTORY}): " INPUT_OUTPUT_DIRECTORY
  OUTPUT_DIRECTORY=${INPUT_OUTPUT_DIRECTORY:-$DEFAULT_OUTPUT_DIRECTORY}
else
  OUTPUT_DIRECTORY="$2"
fi

if [ -d "${OUTPUT_DIRECTORY}" ]
then
  echo "${OUTPUT_DIRECTORY} already exists! Please provide a different name"
  exit 1;
fi

#echo Classname: $CLASSNAME
#echo Group: $GROUP
#echo Artifact: $ARTIFACT
#echo Dir: $DIRECTORY
#echo Name: $NAME
#echo Package: $PACKAGE

SRC_DIRECTORY="src/main/java/${DIRECTORY}"
TEST_DIRECTORY="src/test/java/${DIRECTORY}"

mkdir -p ${OUTPUT_DIRECTORY}

cp -rf src  pom.xml .gitignore ${OUTPUT_DIRECTORY}

(
  cd ${OUTPUT_DIRECTORY} || exit;
  touch LICENSE.txt
  touch NOTICE.txt
  touch README.md
  # The following makes sure that old and new directories also can share a prefix (com/appdynamics/...)
  mv ./src/main/java/com/appdynamics/extensions/extensionstarter/ ./src-files
  mv ./src/test/java/com/appdynamics/extensions/extensionstarter/ ./test-files
  rmdir -p ./src/test/java/com/appdynamics/extensions 2> /dev/null
  rmdir -p ./src/main/java/com/appdynamics/extensions 2> /dev/null
  mkdir -p "${SRC_DIRECTORY}" "${TEST_DIRECTORY}"
  (
    cd src-files || exit;
    mv ExtStarterMonitor.java "${NAME}.java"
    sed -i '' -e "s#ExtStarterMonitor#${NAME}#g" "${NAME}.java"
    sed -i '' -e "s#com.appdynamics.extensions.extensionstarter#${PACKAGE}#g" "${NAME}.java"
    mv ExtStarterMonitorTask.java "${NAME}Task".java
    sed -i '' -e "s#ExtStarterMonitor#${NAME}#g" "${NAME}Task.java"
    sed -i '' -e "s#com.appdynamics.extensions.extensionstarter#${PACKAGE}#g" "${NAME}Task.java"
    sed -i '' -e "s#com.appdynamics.extensions.extensionstarter#${PACKAGE}#g" "util/Constants.java"
  );
  (
    cd test-files || exit;
    mv ExtStarterTest.java "${NAME}Test.java"
    sed -i '' -e "s#ExtStarterMonitor#${NAME}#g" "${NAME}Test.java"
    sed -i '' -e "s#ExtStarterTest#${NAME}Test#g" "${NAME}Test.java"
    sed -i '' -e "s#com.appdynamics.extensions.extensionstarter#${PACKAGE}#g" "${NAME}Test.java"
  );
  (
    sed -i '' -e "s#<groupId>com.appdynamics.extensions</groupId>#<groupId>${GROUP}</groupId>#g" pom.xml
    sed -i '' -e "s#<artifactId>extensions-starter</artifactId>#<artifactId>${ARTIFACT}</artifactId>#g" pom.xml
    sed -i '' -e "s#<name>extensions-starter</name>#<name>${ARTIFACT}</name>#g" pom.xml
    sed -i '' -e "s#<target.dir>\${project.build.directory}/Extension-Starter</target.dir>#<target.dir>\${project.build.directory}/${NAME}</target.dir>#g" pom.xml
  );
  wait
  mv src-files/* ${SRC_DIRECTORY}
  mv test-files/* ${TEST_DIRECTORY}
  rmdir src-files test-files
  # find .
)

wait

echo "You will find your new project in ${OUTPUT_DIRECTORY}"
