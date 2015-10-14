#!/bin/bash
basePath=../../../../

systemPath=$basePath/system
#Source a script that sets all important functions and variables
source $systemPath/rl-competition-includes.sh

className=HSProject.helicopterAgentJava.HelicopterAgent    #Fully Qualified Name of the agent class
maxMemory=128M			 #Max amount of memory to give the agent (Java default is often too low)
extraPath=./../../../bin/HelicopterAgentJava		 #Item for the class path so your agent can be found
startJavaAgent $extraPath $className $maxMemory