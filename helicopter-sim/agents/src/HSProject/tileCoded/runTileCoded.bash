agentName=$1

#!/bin/bash
basePath=../../../../
systemPath=$basePath/system
#Source a script that sets all important functions and variables
source $systemPath/rl-competition-includes.sh

className=HSProject.tileCoded.helicopterAgentTileCoded$agentName.HelicopterAgentTileCoded$agentName  #Fully Qualified Name of the agent class

maxMemory=4G			 #Max amount of memory to give the agent (Java default is often too low)
if [ "$OSTYPE" = "linux-gnu" ]; then
maxMemory=16G
else
maxMemory=6G
fi

echo "JVM MEMORY: $maxMemory"

extraPath=./../../../bin/HelicopterAgentTileCoded$agentName:../../../../../YORLL.jar:../../../../../koloboke-api-jdk8-0.6.8.jar:../../../../../koloboke-impl-jdk8-0.6.8.jar		 #Item for the class path so your agent can be found
startJavaAgent $extraPath $className $maxMemory
