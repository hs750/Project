agentName=$1

#!/bin/bash
basePath=../../../../
systemPath=$basePath/system
#Source a script that sets all important functions and variables
source $systemPath/rl-competition-includes.sh

className=HSProject.tileCoded.helicopterAgentTileCoded$agentName.HelicopterAgentTileCoded$agentName  #Fully Qualified Name of the agent class

# Find the memory avaiable on the system to use for the JVM, round down then subtract 1. eg. 8GB total will be found as 7.somehtingGB rounded down to 7 then minus 1 to give 6. 
maxMemory=$(echo -e 'import re\nmatched=re.search(r"^MemTotal:\s+(\d+)",open("/proc/meminfo").read())\nprint(int(int(matched.groups()[0])/(1024.**2))) - 1' | python)G

#Max amount of memory to give the agent (Java default is often too low)

# For Mac OSX use 6 gigabytes
if [ "$OSTYPE" = "darwin" ]; then #linux is "linux-gnu"
maxMemory=6G
fi

echo "JVM MEMORY: $maxMemory"

extraPath=./../../../bin/HelicopterAgentTileCoded$agentName:../../../../../YORLL.jar:../../../../../koloboke-api-jdk8-0.6.8.jar:../../../../../koloboke-impl-jdk8-0.6.8.jar		 #Item for the class path so your agent can be found
startJavaAgent $extraPath $className $maxMemory
