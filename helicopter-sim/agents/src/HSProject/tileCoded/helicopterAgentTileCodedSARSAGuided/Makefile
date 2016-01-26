RLVizPath = ../../system/libraries/rl-viz

all:
	javac -source 8 -d ../../../bin/HelicopterAgentTileCodedSARSAGuided -cp $(RLVizPath)/RLVizLib.jar -cp ../../../../../YORLL.jar:../../../../../koloboke-api-jdk8-0.6.8.jar HelicopterAgentTileCodedSARSAGuided.java ../TileCodedHelicopterState.java ../TileCodedHelicopterAction.java ../TileCodeQTableInterface.java ../TileCodeQTableFlat.java ../TileCodeQTable.java ../Tile.java ../TileCoding.java


clean:
	rm -Rf ../../../bin/HelicopterAgentTileCodedSARSAGuided/*
