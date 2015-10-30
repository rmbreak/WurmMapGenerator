package net.buddat.wgenerator;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JProgressBar;

import com.wurmonline.mesh.Tiles.Tile;

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
	private JProgressBar progressBar;

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

	public void dropDirt(final int dirtCount, final int maxSlope, final int maxDiagSlope, final int maxDirtHeight, final double cliffRatio, final boolean landSlide, JProgressBar progress) {
		final double maxSlopeHeight = maxSlope * singleDirt;
		final double maxDiagSlopeHeight = maxDiagSlope * singleDirt;
		final double maxHeight = maxDirtHeight * singleDirt;
		final double taperHeight = maxHeight - ((dirtCount / 2) * singleDirt);
		final int mapSize = heightMap.getMapSize();

		final long startTime = System.currentTimeMillis();
		int slice = 30;
		int total = 64;
		dirtDropProgress = 0;
		progressBar = progress;

		class Iteration implements Runnable {
			int index, sizex, sizey;
			int ix[], iy[];

			public Iteration(int i, int[] ix, int[] iy, int sizex, int sizey) {
				index = i;
				this.ix = ix;
				this.iy = iy;
				this.sizex = sizex;
				this.sizey = sizey;
			}

			public void run() {
				//				for (int x = 0; x < heightMap.getMapSize(); x++) {
				//				for (int y = 0; y < heightMap.getMapSize(); y++) {
				for (int x = ix[index]; x < ix[index]+sizex; x++) {
					for (int y = iy[index]; y < iy[index]+sizey; y++) {

						int mod = heightMap.getMapSize()/32;
						if ( x%mod == 0 && y%mod == 0) {
							dirtDropProgress += mod*mod;
							int progressValue = (int)((float)dirtDropProgress/(heightMap.getMapSize()*heightMap.getMapSize())*100f); 
							long predict = (int)((System.currentTimeMillis()-startTime)/1000.0*(100.0/progressValue-1));
							progressBar.setValue(progressValue);
							progressBar.setString(progressBar.getString().substring(0, progressBar.getString().indexOf("("))+"("+predict+" secs)");
						}

						for (int i = 0; i < findDropAmount(x, y, maxSlopeHeight, maxDiagSlopeHeight, dirtCount, cliffRatio); i++) {
							if (heightMap.getHeight(x, y) > maxHeight)
								continue;

							if (heightMap.getHeight(x, y) > taperHeight)
								if ((maxHeight - heightMap.getHeight(x, y)) * heightMap.getMaxHeight() < i)
									continue;

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
		}

		Thread firstThreads[] = new Thread[4];
		for (int i = 0; i < 4; i++) {
			firstThreads[i] = new Thread(new Iteration(i,new int[]{0,mapSize*(total-slice)/total,0,mapSize*(total-slice)/total},
					new int[]{0,0,mapSize*(total-slice)/total,mapSize*(total-slice)/total},mapSize*slice/total,mapSize*slice/total));
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

		Thread secondThreads[] = new Thread[2];
		for (int i = 0; i < 2; i++) {
			secondThreads[i] = new Thread(new Iteration(i,new int[]{0,mapSize*(total-slice)/total},
					new int[]{mapSize*slice/total,mapSize*slice/total},mapSize*slice/total,mapSize*(total-slice*2)/total));
			secondThreads[i].start();
		}
		for (Thread thread : secondThreads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}

		Thread thirdThread = new Thread(new Iteration(0,new int[]{mapSize*slice/total},new int[]{0},mapSize*(total-slice*2)/total,mapSize));
		thirdThread.start();

		try {
			thirdThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}

		log("Dirt Dropping (" + dirtCount + ") completed in " + (System.currentTimeMillis() - startTime) + "ms.");
	}


	private int findDropAmount(int x, int y, double maxSlope, double maxDiagSlope, int dirtCount, double cliffRatio) {

		double slope  = (heightMap.maxDiff(x, y) * cliffRatio);
		double slopeMax = (maxSlope + maxDiagSlope / 2);

		int dirtToDrop = (int)(dirtCount - ((dirtCount / slopeMax) * slope));
		if (dirtToDrop < 0)
			dirtToDrop = 0;
		return dirtToDrop;
	}

	public void generateOres(double[] rates, JProgressBar progress) {
		long startTime = System.currentTimeMillis();

		for (int x = 0; x < heightMap.getMapSize(); x++) {
			progress.setValue((int)((float)x/heightMap.getMapSize()*90f));

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

		log("Ore Generation completed in " + (System.currentTimeMillis() - startTime) + "ms.");
	}

	public void undoLastBiome() {
		for (Point p : lastBiomeChanges.keySet())
			setType(p, lastBiomeChanges.get(p));
	}

	public void plantBiome(int seedCount, int growthIterations, double[] growthRate, int maxBiomeSlope, int minHeight, int maxHeight, Tile type, JProgressBar progress) {
		long startTime = System.currentTimeMillis();

		ArrayList<Point> grassList = new ArrayList<Point>();
		ArrayList<Point> nextList = new ArrayList<Point>();

		lastBiomeChanges.clear();

		for (int i = 0; i < seedCount; i++) {
			grassList.add(new Point(biomeRandom.nextInt(heightMap.getMapSize()), biomeRandom.nextInt(heightMap.getMapSize())));
		}

		for (int i = 0; i < growthIterations; i++) {
			progress.setValue((int)((float)i/growthIterations*90f));
			nextList = growBiome(grassList, type, growthRate, maxBiomeSlope, minHeight, maxHeight);
			grassList = growBiome(nextList, type, growthRate, maxBiomeSlope, minHeight, maxHeight);
		}

		log("Biome Seeding (" + type.tilename + ") completed in " + (System.currentTimeMillis() - startTime) + "ms.");
	}

	private ArrayList<Point> growBiome(ArrayList<Point> fromList, Tile type, double[] growthRate, int maxBiomeSlope, int minHeight, int maxHeight) {
		ArrayList<Point> nextList = new ArrayList<Point>();

		int dirMod = (type.isTree() ? biomeRandom.nextInt(6) + 2 : (type.isBush() ? biomeRandom.nextInt(3) + 2 : 1));

		for (Point p : fromList) {
			if (biomeRandom.nextDouble() < growthRate[0]) { //North
				Point nT = new Point((int) p.getX(), HeightMap.clamp((int) (p.getY() - dirMod), 0, heightMap.getMapSize() - 1));
				if (setBiome(p, nT, maxBiomeSlope * dirMod, type, minHeight, maxHeight))
					nextList.add(nT);
			}

			if (biomeRandom.nextDouble() < growthRate[1]) { //South
				Point nT = new Point((int) p.getX(), HeightMap.clamp((int) (p.getY() + dirMod), 0, heightMap.getMapSize() - 1));
				if (setBiome(p, nT, maxBiomeSlope * dirMod, type, minHeight, maxHeight))
					nextList.add(nT);
			}

			if (biomeRandom.nextDouble() < growthRate[2]) { //East
				Point nT = new Point(HeightMap.clamp((int) (p.getX() + dirMod), 0, heightMap.getMapSize() - 1), (int) p.getY());
				if (setBiome(p, nT, maxBiomeSlope * dirMod, type, minHeight, maxHeight))
					nextList.add(nT);
			}

			if (biomeRandom.nextDouble() < growthRate[3]) { //West
				Point nT = new Point(HeightMap.clamp((int) (p.getX() - dirMod), 0, heightMap.getMapSize() - 1), (int) p.getY());
				if (setBiome(p, nT, maxBiomeSlope * dirMod, type, minHeight, maxHeight))
					nextList.add(nT);
			}
		}

		return nextList;
	}

	private boolean setBiome(Point from, Point to, int maxBiomeSlope, Tile type, int minHeight, int maxHeight) {
		if (getType((int) to.getX(), (int) to.getY()) == Tile.TILE_ROCK)
			return false;

		if (from != null)
			if (getDifference(from, to) > (singleDirt * maxBiomeSlope))
				return false;

		if (getTileHeight((int) to.getX(), (int) to.getY()) < (singleDirt * minHeight))
			return false;

		if (getTileHeight((int) to.getX(), (int) to.getY()) > (singleDirt * maxHeight))
			return false;

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

	public Tile getType(int x, int y) {
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

	public Tile getOreType(int x, int y) {
		if (oreTypeMap[x][y] == null)
			return Tile.TILE_CAVE_WALL;

		return oreTypeMap[x][y];
	}

	private void setOreCount(int x, int y, int resourceCount) {
		oreResourceMap[x][y] = (short) resourceCount;
	}

	public short getOreCount(int x, int y) {
		return oreResourceMap[x][y];
	}

	private void setOreType(int x, int y, Tile newType, int resourceCount) {
		if (!newType.isCave())
			newType = Tile.TILE_CAVE_WALL;

		oreTypeMap[x][y] = newType;
		setOreCount(x, y, resourceCount);
	}

	public boolean hasOres() {
		return hasOres;
	}

	private short getDirt(int x, int y) {
		return dirtMap[x][y];
	}

	private void setDirt(int x, int y, short newDirt) {
		if (newDirt < 0)
			newDirt = 0;

		if (newDirt > 0) {
			if (getTileHeight(x, y) >= waterHeight)
				setType(x, y, Tile.TILE_GRASS);
			else
				setType(x, y, Tile.TILE_DIRT);
		}

		dirtMap[x][y] = newDirt;
	}

	public void addDirt(int x, int y, int count) {
		setDirt(x, y, (short) (getDirt(x, y) + count));
	}

	private double getDirtHeight(int x, int y) {
		return getDirt(x, y) * singleDirt;
	}

	private double getTileHeight(int x, int y) {
		return heightMap.getHeight(x, y) + getDirtHeight(x, y);
	}

	public short getSurfaceHeight(int x, int y) {
		return (short) ((getTileHeight(x, y) - getWaterHeight()) * heightMap.getMaxHeight());
	}

	public short getRockHeight(int x, int y) {
		return (short) ((heightMap.getHeight(x, y) - getWaterHeight()) * heightMap.getMaxHeight());
	}

	private double getDifference(int x1, int y1, int x2, int y2) {
		return Math.abs(getTileHeight(x1, y1) - getTileHeight(x2, y2));
	}

	private double getDifference(Point p, Point p2) {
		return getDifference((int) p.getX(), (int) p.getY(), (int) p2.getX(), (int) p2.getY());
	}

	public void setBiomeSeed(int newSeed) {
		biomeRandom = new Random(newSeed);
	}

	private double getWaterHeight() {
		return waterHeight;
	}

	public void setWaterHeight(int newHeight) {
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

	private static void log (String s) {
		System.out.println(s);
	}
}
