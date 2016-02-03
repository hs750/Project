#Fully Qualified Name of the agent class
className=HSProject.tileCoded.helicopterAgentTileCodedSARSA.HelicopterAgentTileCodedSARSA

#Max amount of memory to give the agent (Java default is often too low)
maxMemory=4G

# For Mac OSX use 6 gigabytes, for linux use as much is avaiable
if [ "$OSTYPE" = "linux-gnu" ]; then #osx is "darwin15"

# Find the memory avaiable on the system to use for the JVM, round down then subtract 1. eg. 8GB total will be found as 7.somehtingGB rounded down to 7 then minus 1 to give 6.
maxMemory=$(echo -e 'import re\nmatched=re.search(r"^MemTotal:\s+(\d+)",open("/proc/meminfo").read())\nprint(int(int(matched.groups()[0])/(1024.**2))) - 1' | python)G

else
maxMemory=6G
fi

echo "JVM MEMORY: $maxMemory"

java -Xmx$maxMemory -jar HSProject.jar $className
