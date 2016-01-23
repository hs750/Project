package HSProject.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;
import org.rlcommunity.rlglue.codec.types.Observation;

import HSProject.Tile;
import HSProject.TileCodedHelicopterState;
import HSProject.TileCoding;

public class TileCodeTest {

	@Test
	public void test() {
		Random r = new Random();
		ArrayList<TileCodedHelicopterState> states = new ArrayList<TileCodedHelicopterState>(1000000);
		ArrayList<Tile[]> tiles = new ArrayList<Tile[]>(1000000);

		for (int k = 0; k < 5; k++) {
			for (int j = 0; j < 6; j++) {
				System.out.println("Starting " + k + " " + j);
				Random r2 = new Random(r.nextLong());
				states.clear();
				;

				int numTiles = k == 0 && j ==0 ? 10 : (r.nextInt(20) + 1);
				int numFeatures = 12;
				int numStateTilings = (int) Math.pow(2, j);
				double[] featureMin = { -5, -5, -5, -20, -20, -20, -12.566, -12.566, -12.566, -1, -1, -1 };
				double[] featureMax = { 5, 5, 5, 20, 20, 20, 12.566, 12.566, 12.566, 1, 1, 1 };
				TileCoding tc = new TileCoding(numTiles, numFeatures, numStateTilings, featureMin, featureMax);

				tiles.clear();

				for (int i = 0; i < 1000000; i++) {
					Observation o = new Observation(0, 12);
					o.doubleArray[0] = r2.nextDouble() * 10 - 5;
					o.doubleArray[1] = r2.nextDouble() * 10 - 5;
					o.doubleArray[2] = r2.nextDouble() * 10 - 5;
					o.doubleArray[3] = r2.nextDouble() * 40 - 20;
					o.doubleArray[4] = r2.nextDouble() * 40 - 20;
					o.doubleArray[5] = r2.nextDouble() * 40 - 20;
					o.doubleArray[6] = r2.nextDouble() * (12.566 * 2) - 12.566;
					o.doubleArray[7] = r2.nextDouble() * (12.566 * 2) - 12.566;
					o.doubleArray[8] = r2.nextDouble() * (12.566 * 2) - 12.566;
					o.doubleArray[9] = r2.nextDouble() * 2 - 1;
					o.doubleArray[10] = r2.nextDouble() * 2 - 1;
					o.doubleArray[11] = r2.nextDouble() * 2 - 1;

					TileCodedHelicopterState state = new TileCodedHelicopterState(o);
					states.add(state);

					Tile[] tile = new Tile[numStateTilings];
					tc.getTiles(tile, state);

					tiles.add(tile);
				}

				for (int i = 0; i < 1000000; i++) {

					TileCodedHelicopterState state = states.get(i);

					Tile[] tile = new Tile[numStateTilings];
					tc.getTiles(tile, state);

					boolean same = Arrays.equals(tile, tiles.get(i));

					if (!same) {
						fail("tiles not same: numTiles=" + numTiles + " numTilings=" + numStateTilings + " tile1=" + Arrays.toString(tile) + " tile2=" + Arrays.toString(tiles.get(i)));
					}
				}
				System.out.println("finished " + k + " " + j);
			}
		}
	}

	@Test
	public void test2() {
		Random r = new Random();
		ArrayList<Tile[]> tiles = new ArrayList<Tile[]>(1000000);
		for (int k = 0; k < 10; k++) {
			System.out.println("Starting " + k);
			Random r2 = new Random(r.nextLong());

			int numTiles = 10;
			int numFeatures = 12;
			int numStateTilings = 16;
			double[] featureMin = { -5, -5, -5, -20, -20, -20, -12.566, -12.566, -12.566, -1, -1, -1 };
			double[] featureMax = { 5, 5, 5, 20, 20, 20, 12.566, 12.566, 12.566, 1, 1, 1 };
			TileCoding tc = new TileCoding(numTiles, numFeatures, numStateTilings, featureMin, featureMax);

			tiles.clear();

			Observation o = new Observation(0, 12);
			o.doubleArray[0] = r2.nextDouble() * 10 - 5;
			o.doubleArray[1] = r2.nextDouble() * 10 - 5;
			o.doubleArray[2] = r2.nextDouble() * 10 - 5;
			o.doubleArray[3] = r2.nextDouble() * 40 - 20;
			o.doubleArray[4] = r2.nextDouble() * 40 - 20;
			o.doubleArray[5] = r2.nextDouble() * 40 - 20;
			o.doubleArray[6] = r2.nextDouble() * (12.566 * 2) - 12.566;
			o.doubleArray[7] = r2.nextDouble() * (12.566 * 2) - 12.566;
			o.doubleArray[8] = r2.nextDouble() * (12.566 * 2) - 12.566;
			o.doubleArray[9] = r2.nextDouble() * 2 - 1;
			o.doubleArray[10] = r2.nextDouble() * 2 - 1;
			o.doubleArray[11] = r2.nextDouble() * 2 - 1;

			TileCodedHelicopterState state = new TileCodedHelicopterState(o);

			for (int i = 0; i < 1000000; i++) {

				Tile[] tile = new Tile[numStateTilings];
				tc.getTiles(tile, state);

				tiles.add(tile);
			}

			Tile[] firstTile = tiles.get(0);
			for (int i = 0; i < 1000000; i++) {

				boolean same = Arrays.equals(firstTile, tiles.get(i));

				if (!same) {
					fail("tiles not same");
				}
			}
			System.out.println("finished " + k);
		}
	}

}
