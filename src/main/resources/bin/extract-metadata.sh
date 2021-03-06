#!/bin/sh
ProgDir=`dirname "$0"`
# If JAVA_HOME is not set, use the java in the execution path
if [ ${JAVA_HOME} ] ; then
  JAVA="$JAVA_HOME/bin/java"
else
  JAVA=java
fi

# ${assembly.home.env.name} must point to home directory.
PRG="$0"

${assembly.home.env.name}=`dirname "$PRG"`/..

# make it fully qualified
${assembly.home.env.name}=`cd "${assembly.home.env.name.ref}" && pwd`

if [ -z "${assembly.conf.env.file.ref}" ] ; then
  ${assembly.conf.env.file}="${assembly.home.env.name.ref}/conf/ginnungagap.yml"
fi

# CP must contain a colon-separated list of resources used.
CP=${assembly.home.env.name.ref}/:${assembly.home.env.name.ref}/conf/
for i in `ls ${assembly.home.env.name.ref}/lib/*.jar`
do
  CP=${CP}:${i}
done
CP=${CP}:/usr/local/Cumulus_Java_SDK/CumulusJC.jar
if [ -z "${JAVA_OPTS}" ]; then
  JAVA_OPTS="-Xms256m -Xmx2048m"
fi

cd ${assembly.home.env.name.ref}

"${JAVA}" ${JAVA_OPTS} -D${assembly.home.env.name}="${assembly.home.env.name.ref}" -D${assembly.conf.env.file}="${assembly.conf.env.file.ref}" -cp "$CP" dk.kb.ginnungagap.MetadataExtraction "$@"
