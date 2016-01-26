#!/bin/bash
basePath=../../../../
systemPath=$basePath/system
#Source a script that sets all important functions and variables
source $systemPath/rl-competition-includes.sh

className=HSProject.helicopterAgentTileCodedSARSAGuided.HelicopterAgentTileCodedSARSAGuided  #Fully Qualified Name of the agent class
maxMemory=6144M			 #Max amount of memory to give the agent (Java default is often too low)
extraPath=./../../../bin/HelicopterAgentTileCodedSARSAGuided:../../../../../YORLL.jar:../../../../../koloboke-api-jdk8-0.6.8.jar:../../../../../koloboke-impl-jdk8-0.6.8.jar		 #Item for the class path so your agent can be found
startJavaAgent $extraPath $className $maxMemory
