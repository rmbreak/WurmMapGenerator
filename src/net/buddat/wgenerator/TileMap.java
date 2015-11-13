package net.buddat.wgenerator;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JOptionPane;

import com.wurmonline.mesh.Tiles.Tile;

import net.buddat.wgenerator.util.Constants;
import net.buddat.wgenerator.util.ProgressHandler;

public class TileMap {

	private Random biomeRandom;
	private HeightMap heightMap;
	private Tile[][] typeMap;
	private Tile[][] oreTypeMap;
	private short[][] oreResourceMap;
	private short[][] dirtMap;
	private double singleDirt;
	private double waterHeight;
	private boolean hasOres;
	private long dirtDropProgress;
	private int biomeSeed;

	private HashMap<Point, Tile> lastBiomeChanges;

	public TileMap(HeightMap heightMap) {
		this.heightMap = heightMap;
		this.singleDirt = heightMap.getSingleDirt();

		this.typeMap = new Tile[heightMap.getMapSize()][heightMap.getMapSize()];
		this.oreTypeMap = new Tile[heightMap.getMapSize()][heightMap.getMapSize()];
		this.oreResourceMap = new short[heightMap.getMapSize()][heightMap.getMapSize()];
		this.dirtMap = new short[heightMap.getMapSize()][heightMap.getMapSize()];

		this.hasOres = false;

		this.lastBiomeChanges = new HashMap<Point, Tile>();
	}

	void dropDirt(final int dirtCount, final int maxSlope, final int maxDiagSlope, final int maxDirtHeight, final double cliffRatio, final boolean landSlide, final ProgressHandler progress) {
		final double maxSlopeHeight = maxSlope * singleDirt;
		final double maxDiagSlopeHeight = maxDiagSlope * singleDirt;
		final double maxHeight = maxDirtHeight * singleDirt;
		final double taperHeight = maxHeight - (dirtCount * singleDirt);
		final int mapSize = heightMap.getMapSize();
		final long startTime = System.currentTimeMillis();
		dirtDropProgress = 0;

		class Iteration implements Runnable {
			int sizex, sizey;
			int ix, iy;

			public Iteration(int ix, int iy, int sizex, int sizey) {
				this.ix = ix;
				this.iy = iy;
				this.sizex = sizex;
				this.sizey = sizey;
			}

			public void run() {
				for (int x = ix; x < ix+sizex; x++) {
					for (int y = iy; y < iy+sizey; y++) {
						int mod = heightMap.getMapSize()/32;
						if ( x%mod == 0 && y%mod == 0) {
							dirtDropProgress += mod*mod;
							int progressValue = (int)((float)dirtDropProgress/(heightMap.getMapSize()*heightMap.getMapSize()*dirtCount)*100f); 
							long predict = (int)((System.currentTimeMillis()-startTime)/1000.0*(100.0/progressValue-1));
							progress.update(progressValue, (progress.getText().substring(0, progress.getText().indexOf("("))+"("+predict+" secs)"));
						}

						if (dirtMap[x][y] >= findDropAmount(x, y, maxSlope, maxDiagSlope, dirtCount, cliffRatio)) {
							continue;
						}

						if (getTileHeight(x,y) > maxHeight) {
							continue;
						}

						if (getTileHeight(x,y) > taperHeight) {
							if (getTileHeight(x,y)/singleDirt + dirtCount/2 > maxDirtHeight) {
								continue;
							}
						}

						if (landSlide) {
							Point dropTile = findDropTile(x, y, maxSlopeHeight, maxDiagSlopeHeight);
							addDirt((int) dropTile.getX(), (int) dropTile.getY(), 1);
						} else {
							Point dropTile = new Point(x,y);
							addDirt((int) dropTile.getX(), (int) dropTile.getY(), 1);
						}
					}
				}
			}
		}

		Thread firstThreads[] = new Thread[dirtCount];
		for (int i = 0; i < dirtCount; i++) {
			firstThreads[i] = new Thread(new Iteration(0,0,mapSize,mapSize));
			firstThreads[i].start();
		}
		for (Thread thread : firstThreads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}

		// Reset seed due to drop dirt altering it
		setBiomeSeed(biomeSeed);
		MainWindow.log("Dirt Dropping (" + dirtCount + ") completed in " + (System.currentTimeMillis() - startTime) + "ms.");
	}


	private int findDropAmount(int x, int y, double maxSlope, double maxDiagSlope, int dirtCount, double cliffRatio) {
		double slope  = (heightMap.maxDiff(x, y)) / singleDirt * cliffRatio;
		double slopeMax = (maxSlope +maxDiagSlope) / 2.0;
		int dirtToDrop = Math.max(0,(int)(dirtCount - ((dirtCount / slopeMax) * slope)));
		return dirtToDrop;
	}

	void generateOres(double[] rates, ProgressHandler progress) {
		setBiomeSeed(biomeSeed);
		long startTime = System.currentTimeMillis();

		for (int x = 0; x < heightMap.getMapSize(); x++) {
			progress.update((int)((float)x/heightMap.getMapSize()*99f));

			for (int y = 0; y < heightMap.getMapSize(); y++) {
				double rand = biomeRandom.nextDouble() * 100;
				double total;

				if (rand < (total = rates[0]))
					setOreType(x, y, Tile.TILE_CAVE_WALL, biomeRandom.nextInt(20) + 40);
				else if (rand < (total += rates[1]))
					setOreType(x, y, Tile.TILE_CAVE_WALL_ORE_IRON, biomeRandom.nextInt(15000) + 90);
				else if (rand < (total += rates[2]))
					setOreType(x, y, Tile.TILE_CAVE_WALL_ORE_GOLD, biomeRandom.nextInt(15000) + 90);
				else if (rand < (total += rates[3]))
					setOreType(x, y, Tile.TILE_CAVE_WALL_ORE_SILVER, biomeRandom.nextInt(15000) + 90);
				else if (rand < (total += rates[4]))
					setOreType(x, y, Tile.TILE_CAVE_WALL_ORE_ZINC, biomeRandom.nextInt(15000) + 90);
				else if (rand < (total += rates[5]))
					setOreType(x, y, Tile.TILE_CAVE_WALL_ORE_COPPER, biomeRandom.nextInt(15000) + 90);
				else if (rand < (total += rates[6]))
					setOreType(x, y, Tile.TILE_CAVE_WALL_ORE_LEAD, biomeRandom.nextInt(15000) + 90);
				else if (rand < (total += rates[7]))
					setOreType(x, y, Tile.TILE_CAVE_WALL_ORE_TIN, biomeRandom.nextInt(15000) + 90);
				else if (rand < (total += rates[8]))
					setOreType(x, y, Tile.TILE_CAVE_WALL_ORE_ADAMANTINE, biomeRandom.nextInt(15000) + 90);
				else if (rand < (total += rates[9]))
					setOreType(x, y, Tile.TILE_CAVE_WALL_ORE_GLIMMERSTEEL, biomeRandom.nextInt(15000) + 90);
				else if (rand < (total += rates[10]))
					setOreType(x, y, Tile.TILE_CAVE_WALL_MARBLE, biomeRandom.nextInt(15000) + 90);
				else if (rand < (total += rates[11]))
					setOreType(x, y, Tile.TILE_CAVE_WALL_SLATE, biomeRandom.nextInt(15000) + 90);
				else
					setOreType(x, y, Tile.TILE_CAVE_WALL, biomeRandom.nextInt(20) + 40);
			}
		}

		hasOres = true;

		MainWindow.log("Ore Generation completed in " + (System.currentTimeMillis() - startTime) + "ms.");
	}

	void undoLastBiome() {
		for (Point p : lastBiomeChanges.keySet())
			setType(p, lastBiomeChanges.get(p));
	}

	void plantBiome(int seedCount, int growthIterations, int density, int[] growthRate, boolean randomGrowth, int maxBiomeSlope, int minHeight, int maxHeight, Tile type, ProgressHandler progress) {
		long startTime = System.currentTimeMillis();

		lastBiomeChanges.clear();
		ArrayList<Point> nextList = new ArrayList<Point>();
		long totalSize;
		int totalSeeds = 0;

		for (long killCount = 0; totalSeeds < seedCount; killCount++) {
			nextList.clear();
			progress.update((int)((float)totalSeeds/seedCount*99f));

			nextList.add(new Point(biomeRandom.nextInt(heightMap.getMapSize()),biomeRandom.nextInt(heightMap.getMapSize())));
			totalSize = 1;

			int[] randomRate = new int[4];
			if (randomGrowth) {
				randomRate[0] = (biomeRandom.nextInt(growthRate[1]-growthRate[0])+growthRate[0]);
				randomRate[1] = (biomeRandom.nextInt(growthRate[1]-growthRate[0])+growthRate[0]);
				randomRate[2] = (biomeRandom.nextInt(growthRate[1]-growthRate[0])+growthRate[0]);
				randomRate[3] = (biomeRandom.nextInt(growthRate[1]-growthRate[0])+growthRate[0]);
			}

			for (int g = 0; g < growthIterations/density; g++) {
				nextList = growBiome(nextList, type, density, randomGrowth? randomRate:growthRate, maxBiomeSlope, minHeight, maxHeight);
				totalSize += nextList.size();
			}

			if (totalSize != 1) {
				totalSeeds++;
			}
			// Prevent infinite looping
			if (killCount > seedCount*Constants.BIOME_SEED_LIMIT_MULTIPLIER) {
				break;
			}
		}

		MainWindow.log("Biome Seeding (" + type.tilename + ") completed in " + (System.currentTimeMillis() - startTime) + "ms.");
	}

	void plantBiomeAt(int x, int y, int growthIterations, int density, int[] growthRate, boolean randomGrowth, int maxBiomeSlope, int minHeight, int maxHeight, Tile type, ProgressHandler progress) {
		long startTime = System.currentTimeMillis();

		ArrayList<Point> nextList = new ArrayList<Point>();

		lastBiomeChanges.clear();

		nextList.add(new Point(x, y));

		int[] randomRate = new int[]{};
		if (randomGrowth) {
			randomRate = new int[]{(biomeRandom.nextInt(growthRate[1]-growthRate[0])+growthRate[0]),
					(biomeRandom.nextInt(growthRate[1]-growthRate[0])+growthRate[0]),
					(biomeRandom.nextInt(growthRate[1]-growthRate[0])+growthRate[0]),
					(biomeRandom.nextInt(growthRate[1]-growthRate[0])+growthRate[0])};
		}

		for (int i = 0; i < growthIterations/density; i++) {
			progress.update((int)((float)i/growthIterations/density*99f));
			nextList = growBiome(nextList, type, density, randomGrowth? randomRate:growthRate, maxBiomeSlope, minHeight, maxHeight);
		}

		MainWindow.log("Biome Seeding (" + type.tilename + ") completed in " + (System.currentTimeMillis() - startTime) + "ms.");
	}

	private ArrayList<Point> growBiome(ArrayList<Point> fromList, Tile type, int density, int[] growthRate, int maxBiomeSlope, int minHeight, int maxHeight) {
		ArrayList<Point> nextList = new ArrayList<Point>();

		int dirMod;
		int mapSize = heightMap.getMapSize();

		for (Point p : fromList) {
			
			dirMod = (type.isTree() ? biomeRandom.nextInt(4*density) + 3 : (type.isBush() ? biomeRandom.nextInt(2*density) + 2 : biomeRandom.nextInt(density)+1));
			if (biomeRandom.nextInt(100) < growthRate[0]) { //North
				Point nT = new Point((int) p.getX(), HeightMap.clamp((int) (p.getY() - dirMod), 0, mapSize - 1));
				if (setBiome(p, nT, maxBiomeSlope * dirMod, type, minHeight, maxHeight))
					nextList.add(nT);
			}

			if (biomeRandom.nextInt(100) < growthRate[1]) { //South
				Point nT = new Point((int) p.getX(), HeightMap.clamp((int) (p.getY() + dirMod), 0, mapSize - 1));
				if (setBiome(p, nT, maxBiomeSlope * dirMod, type, minHeight, maxHeight))
					nextList.add(nT);
			}

			if (biomeRandom.nextInt(100) < growthRate[2]) { //East
				Point nT = new Point(HeightMap.clamp((int) (p.getX() + dirMod), 0, mapSize - 1), (int) p.getY());
				if (setBiome(p, nT, maxBiomeSlope * dirMod, type, minHeight, maxHeight))
					nextList.add(nT);
			}

			if (biomeRandom.nextInt(100) < growthRate[3]) { //West
				Point nT = new Point(HeightMap.clamp((int) (p.getX() - dirMod), 0, mapSize - 1), (int) p.getY());
				if (setBiome(p, nT, maxBiomeSlope * dirMod, type, minHeight, maxHeight))
					nextList.add(nT);
			}
		}

		return nextList;
	}

	private boolean setBiome(Point from, Point to, int maxBiomeSlope, Tile type, int minHeight, int maxHeight) {
		if (getType((int) to.getX(), (int) to.getY()) == Tile.TILE_ROCK) {
			return false;
		}

		if (from != null) {
			if (getDifference(from, to) > (singleDirt * maxBiomeSlope)) {
				return false;
			}
		}

		if (getTileHeight((int) to.getX(), (int) to.getY()) < (singleDirt * minHeight)) {
			return false;
		}

		if (getTileHeight((int) to.getX(), (int) to.getY()) > (singleDirt * maxHeight)) {
			return false;
		}

		Tile originalTileType = getType((int) to.getX(), (int) to.getY());
		if (originalTileType != type) {
			if((!type.isTree() && !type.isBush()) || originalTileType == Tile.TILE_GRASS) {
				lastBiomeChanges.put(to, getType(to));
				setType(to, type);
				return true;
			}
		}

		return false;
	}

	Tile getType(int x, int y) {
		if (typeMap[x][y] == null)
			return Tile.TILE_ROCK;

		return typeMap[x][y];
	}

	private Tile getType(Point p) {
		return getType((int) p.getX(), (int) p.getY());
	}

	private void setType(int x, int y, Tile newType) {
		typeMap[x][y] = newType;
	}

	private void setType(Point p, Tile newType) {
		setType((int) p.getX(), (int) p.getY(), newType);
	}

	Tile getOreType(int x, int y) {
		if (oreTypeMap[x][y] == null)
			return Tile.TILE_CAVE_WALL;

		return oreTypeMap[x][y];
	}

	private void setOreCount(int x, int y, int resourceCount) {
		oreResourceMap[x][y] = (short) resourceCount;
	}

	short getOreCount(int x, int y) {
		return oreResourceMap[x][y];
	}

	private void setOreType(int x, int y, Tile newType, int resourceCount) {
		if (!newType.isCave())
			newType = Tile.TILE_CAVE_WALL;

		oreTypeMap[x][y] = newType;
		setOreCount(x, y, resourceCount);
	}

	boolean hasOres() {
		return hasOres;
	}

	private short getDirt(int x, int y) {
		return dirtMap[x][y];
	}

	private void setDirt(int x, int y, short newDirt) {
		if (newDirt < 0) {
			newDirt = 0;
		}

		if (newDirt > 0) {
			if (getTileHeight(x, y) >= waterHeight)
				setType(x, y, Tile.TILE_GRASS);
			else
				setType(x, y, Tile.TILE_DIRT);
		}

		dirtMap[x][y] = newDirt;
	}

	void addDirt(int x, int y, int count) {
		synchronized(this) {
			setDirt(x, y, (short) (getDirt(x, y) + count));
		}
	}

	private double getDirtHeight(int x, int y) {
		return getDirt(x, y) * singleDirt;
	}

	private double getTileHeight(int x, int y) {
		return heightMap.getHeight(x, y) + getDirtHeight(x, y);
	}

	short getSurfaceHeight(int x, int y) {
		return (short) ((getTileHeight(x, y) - waterHeight) * heightMap.getMaxHeight());
	}

	short getRockHeight(int x, int y) {
		return (short) ((heightMap.getHeight(x, y) - waterHeight) * heightMap.getMaxHeight());
	}

	int getMapHeight(int x, int y) {
		return (int)(getTileHeight(x, y) * heightMap.getMaxHeight());
	}

	private double getDifference(int x1, int y1, int x2, int y2) {
		return Math.abs(getTileHeight(x1, y1) - getTileHeight(x2, y2));
	}

	private double getDifference(Point p, Point p2) {
		return getDifference((int) p.getX(), (int) p.getY(), (int) p2.getX(), (int) p2.getY());
	}

	void setBiomeSeed(int newSeed) {
		biomeSeed = newSeed;
		biomeRandom = new Random(newSeed);
	}

	void setWaterHeight(int newHeight) {
		this.waterHeight = newHeight * singleDirt;
	}

	private Point findDropTile(int x, int y, double maxSlope, double maxDiagSlope) {
		ArrayList<Point> slopes = new ArrayList<Point>();
		double currentHeight = getTileHeight(x, y);

		for (int i = x + 1; i >= x - 1; i--) {
			for (int j = y + 1; j >= y - 1; j--) {
				if (i < 0 || j < 0 || i >= heightMap.getMapSize() || j >= heightMap.getMapSize())
					continue;

				double thisHeight = getTileHeight(i, j);
				if ((i == 0 && j != 0) || (i != 0 && j == 0))
					if (thisHeight <= currentHeight - maxSlope)
						slopes.add(new Point(i, j));

				if (i != 0 && y != 0)
					if (thisHeight <= currentHeight - maxDiagSlope)
						slopes.add(new Point(i, j));
			}
		}

		if (slopes.size() > 0) {
			int r = biomeRandom.nextInt(slopes.size());
			return findDropTile((int) slopes.get(r).getX(), (int) slopes.get(r).getY(), maxSlope, maxDiagSlope);
		} else {
			return new Point(x, y);
		}
	}

	void importBiomeImage(BufferedImage biomesImage) {
		if (biomesImage.getHeight() != biomesImage.getWidth())
		{
			JOptionPane.showMessageDialog(null, "The image must be square!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (biomesImage.getHeight() != heightMap.getMapSize() || biomesImage.getWidth() != heightMap.getMapSize()) {
			JOptionPane.showMessageDialog(null, "The image size does not match your map size! "+biomesImage.getHeight(), "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		long startTime = System.currentTimeMillis();
		int mapSize = heightMap.getMapSize();
		try {
			DataBufferByte buffer = (DataBufferByte) biomesImage.getRaster().getDataBuffer();

			for (int x = 0; x < mapSize; x++) {
				for (int y = 0; y < mapSize; y++) {
					int r = buffer.getElem((x+y*mapSize)*3+2);
					int g = buffer.getElem((x+y*mapSize)*3+1);
					int b = buffer.getElem((x+y*mapSize)*3+0);
					setType(x,y,getTileType(r,g,b));
				}
			}
			
			MainWindow.log("Biomes Import (" + mapSize + ") completed in " + (System.currentTimeMillis() - startTime) + "ms.");
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "The map must be a 24-bit color image.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public static Tile getTileType(int r, int g, int b) {
		for(Tile tile:Tile.getTiles()) {
			if (tile == null) {
				continue;
			}
			if (r == tile.getColor().getRed() && g == tile.getColor().getGreen() && b == tile.getColor().getBlue()) {
				return tile;
			}
		}
		return Tile.TILE_DIRT;
	}
	
	
}
