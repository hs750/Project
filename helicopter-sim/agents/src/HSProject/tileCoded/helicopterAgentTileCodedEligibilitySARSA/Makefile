include ../Makefile.classPath

agentName=EligibilitySARSA
all:
	mkdir -p ../../../../bin/HelicopterAgentTileCoded$(agentName)
	javac -source 8 -d ../../../../bin/HelicopterAgentTileCoded$(agentName) -cp $(classPath) HelicopterAgentTileCoded$(agentName).java EligibilityQTable.java StateActionVisitQueue.java


clean:
	rm -Rf ../../../../bin/HelicopterAgentTileCoded$(agentName)/*
