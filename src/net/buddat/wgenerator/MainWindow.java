package net.buddat.wgenerator;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicProgressBarUI;

import com.wurmonline.mesh.FoliageAge;
import com.wurmonline.mesh.GrassData.GrowthTreeStage;
import com.wurmonline.mesh.Tiles.Tile;
import com.wurmonline.wurmapi.api.MapData;
import com.wurmonline.wurmapi.api.WurmAPI;

import net.buddat.wgenerator.util.Constants;

import javax.swing.JTabbedPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JProgressBar;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.GridLayout;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.BorderLayout;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {

	private static final String version = "2.0.0";
	private static final Logger logger = Logger.getLogger(MainWindow.class.getName());
	private WurmAPI api;
	private HeightMap heightMap;
	private TileMap tileMap;
	private ArrayList<String> genHistory;
	private boolean apiClosed = true;
	private MapPanel mapPanel;
	private String mapName;
	private String actionsFileDirectory;

	private JPanel contentPane;
	private JTextField textField_mapSeed;
	private JTextField textField_mapResolution;
	private JTextField textField_mapMinEdge;
	private JTextField textField_mapBorderWeight;
	private JTextField textField_mapMaxHeight;
	private JTextField textField_mapIterations;
	private JTextField textField_erodeIterations;
	private JTextField textField_erodeMinSlope;
	private JTextField textField_erodeSediment;
	private JTextField textField_biomeSeed;
	private JTextField textField_dirtPerTile;
	private JTextField textField_maxDiagSlope;
	private JTextField textField_maxDirtHeight;
	private JTextField textField_waterHeight;
	private JTextField textField_seedCount;
	private JTextField textField_biomeSize;
	private JTextField textField_biomeMaxSlope;
	private JTextField textField_biomeMaxHeight;
	private JTextField textField_growthN;
	private JTextField textField_growthS;
	private JTextField textField_growthE;
	private JTextField textField_growthW;
	private JTextField textField_biomeMinHeight;
	private JTextField textField_Iron;
	private JTextField textField_Gold;
	private JTextField textField_Silver;
	private JTextField textField_Zinc;
	private JTextField textField_Copper;
	private JTextField textField_Lead;
	private JTextField textField_Tin;
	private JTextField textField_Marble;
	private JTextField textField_Slate;
	private JTextField textField_Addy;
	private JTextField textField_Glimmer;
	private JTextField textField_Rock;
	private JTextField textField_maxDirtSlope;
	private JTextField textField_mapName;
	private JProgressBar progressBar;
	private JComboBox<Integer> comboBox_mapSize;
	private JCheckBox checkbox_biomeRandomSeed;
	private JComboBox<Tile> comboBox_biomeType;
	private JCheckBox checkbox_moreLand;
	private JCheckBox checkbox_mapRandomSeed;
	private JButton btnGenerateHeightmap;
	private JButton btnErodeHeightmap;
	private JButton btnUpdateWater;
	private JButton btnDropDirt;
	private JButton btnGenerateOres;
	private JButton btnResetBiomes;
	private JButton btnUndoLastBiome;
	private JButton btnAddBiome;
	private JLabel lblWater;
	private JCheckBox checkbox_AroundWater;
	private JTextField textField_growthMin;
	private JTextField textField_growthMax;
	private JCheckBox checkbox_growthRandom;
	private JButton btnSaveMapFiles;
	private JButton btnSaveImageDumps;
	private JButton btnSaveActions;
	private JButton btnLoadActions;
	private JButton btnViewHeight;
	private JButton btnViewCave;
	private JButton btnTopo;
	private JButton btnViewMap;
	private JTextField textField_cliffRatio;
	private JButton btnSaveHeightmap;
	private JButton btnLoadHeightmap;
	private JTextField textField_erodeMaxSlope;
	private JCheckBox checkBox_landSlide;
	private JButton btnUpdateMapName;


	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MainWindow() {
		setTitle("Wurm Map Generator - v"+version);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 951, 650);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		mapPanel = new MapPanel();

		JTabbedPane optionsPane = new JTabbedPane(JTabbedPane.TOP);

		progressBar = new JProgressBar();
		Font loadingFont = new Font("Helvetica", Font.PLAIN, 16);
		progressBar.setFont(loadingFont);
		progressBar.setStringPainted(true);
		progressBar.setUI(new BasicProgressBarUI() {
			protected Color getSelectionBackground() { return Color.black; }
			protected Color getSelectionForeground() { return Color.black; }
		});
		progressBar.setString("");
		progressBar.setBackground(new Color(250,100,100));
		progressBar.setForeground(new Color(50, 205, 50));
		progressBar.setEnabled(true);
		progressBar.setValue(100);

		JPanel viewPanel = new JPanel();
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
				gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
								.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 862, Short.MAX_VALUE)
								.addGroup(gl_contentPane.createSequentialGroup()
										.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
												.addComponent(viewPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE)
												.addComponent(mapPanel, GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE))
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(optionsPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
												.addContainerGap())
				);
		gl_contentPane.setVerticalGroup(
				gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
										.addComponent(mapPanel, GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(viewPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addComponent(optionsPane, GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE))
										.addContainerGap())
				);

		btnViewMap = new JButton("View Map");
		viewPanel.add(btnViewMap);

		btnTopo = new JButton("View Topo");
		viewPanel.add(btnTopo);

		btnViewCave = new JButton("View Cave");
		viewPanel.add(btnViewCave);

		btnViewHeight = new JButton("View Heightmap");
		viewPanel.add(btnViewHeight);

		JPanel heightmapPanel = new JPanel();
		optionsPane.addTab("Heightmap", null, heightmapPanel, null);
		optionsPane.setEnabledAt(0, true);

		JPanel panel_6 = new JPanel();

		JPanel panel = new JPanel();
		panel_6.add(panel);
		panel.setLayout(new GridLayout(0, 2, 0, 0));

		JPanel labelPanel = new JPanel();
		panel.add(labelPanel);
		labelPanel.setLayout(new GridLayout(0, 1, 0, 2));

		JLabel lblMapSize = new JLabel("Map Size");
		labelPanel.add(lblMapSize);

		JLabel lblMapSeed = new JLabel("Map Seed");
		labelPanel.add(lblMapSeed);

		JLabel lblResolution = new JLabel("Resolution");
		labelPanel.add(lblResolution);

		JLabel lblItrerations = new JLabel("Iterations");
		labelPanel.add(lblItrerations);

		JLabel lblMinEdge = new JLabel("Min Edge");
		labelPanel.add(lblMinEdge);

		JLabel lblBorderWeight = new JLabel("Border Weight");
		labelPanel.add(lblBorderWeight);

		JLabel lblMaxHeight = new JLabel("Max Height");
		labelPanel.add(lblMaxHeight);

		checkbox_moreLand = new JCheckBox("More Land", Constants.MORE_LAND);
		labelPanel.add(checkbox_moreLand);

		checkbox_mapRandomSeed = new JCheckBox("Random Seed", true);
		labelPanel.add(checkbox_mapRandomSeed);

		JPanel inputPanel = new JPanel();
		panel.add(inputPanel);
		inputPanel.setLayout(new GridLayout(0, 1, 0, 2));

		comboBox_mapSize = new JComboBox<Integer>();
		inputPanel.add(comboBox_mapSize);
		comboBox_mapSize.setModel(new DefaultComboBoxModel<Integer>(new Integer[] {1024, 2048, 4096, 8192, 16384}));
		comboBox_mapSize.setSelectedIndex(1);

		textField_mapSeed = new JTextField("" + System.currentTimeMillis());
		inputPanel.add(textField_mapSeed);
		textField_mapSeed.setColumns(10);

		textField_mapResolution = new JTextField("" + (int) Constants.RESOLUTION);
		inputPanel.add(textField_mapResolution);
		textField_mapResolution.setColumns(10);

		textField_mapIterations = new JTextField("" + (int) Constants.HEIGHTMAP_ITERATIONS);
		inputPanel.add(textField_mapIterations);
		textField_mapIterations.setColumns(10);

		textField_mapMinEdge = new JTextField("" + (int) Constants.MIN_EDGE);
		inputPanel.add(textField_mapMinEdge);
		textField_mapMinEdge.setColumns(10);

		textField_mapBorderWeight = new JTextField("" + (int) Constants.BORDER_WEIGHT);
		inputPanel.add(textField_mapBorderWeight);
		textField_mapBorderWeight.setColumns(10);

		textField_mapMaxHeight = new JTextField("" + (int) Constants.MAP_HEIGHT);
		inputPanel.add(textField_mapMaxHeight);
		textField_mapMaxHeight.setColumns(10);

		JLabel label = new JLabel("");
		inputPanel.add(label);

		JLabel lblNewLabel = new JLabel("");
		inputPanel.add(lblNewLabel);

		JPanel panel_7 = new JPanel();

		btnGenerateHeightmap = new JButton("Generate Heightmap");
		panel_7.add(btnGenerateHeightmap);
		GroupLayout gl_heightmapPanel = new GroupLayout(heightmapPanel);
		gl_heightmapPanel.setHorizontalGroup(
				gl_heightmapPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_heightmapPanel.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_heightmapPanel.createParallelGroup(Alignment.TRAILING)
								.addComponent(panel_6, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
								.addComponent(panel_7, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE))
								.addContainerGap())
				);
		gl_heightmapPanel.setVerticalGroup(
				gl_heightmapPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_heightmapPanel.createSequentialGroup()
						.addContainerGap()
						.addComponent(panel_7, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(panel_6, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
						.addContainerGap())
				);
		heightmapPanel.setLayout(gl_heightmapPanel);

		JPanel erosionPanel = new JPanel();
		optionsPane.addTab("Erosion", null, erosionPanel, null);
		optionsPane.setEnabledAt(1, true);

		JPanel panel_1 = new JPanel();

		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2);
		panel_2.setLayout(new GridLayout(0, 2, 0, 0));

		JPanel panel_3 = new JPanel();
		panel_2.add(panel_3);
		panel_3.setLayout(new GridLayout(0, 1, 0, 2));

		JLabel lblIterations = new JLabel("Iterations");
		panel_3.add(lblIterations);

		JLabel lblMinSlope = new JLabel("Min Slope");
		panel_3.add(lblMinSlope);

		JLabel lblMaxSlope_1 = new JLabel("Max Slope");
		panel_3.add(lblMaxSlope_1);

		JLabel lblSedimentPer = new JLabel("Sediment per");
		panel_3.add(lblSedimentPer);

		JPanel panel_4 = new JPanel();
		panel_2.add(panel_4);
		panel_4.setLayout(new GridLayout(0, 1, 0, 2));

		textField_erodeIterations = new JTextField("" + Constants.EROSION_ITERATIONS);
		textField_erodeIterations.setColumns(10);
		panel_4.add(textField_erodeIterations);

		textField_erodeMinSlope = new JTextField("" + Constants.EROSION_MIN_SLOPE);
		textField_erodeMinSlope.setColumns(10);
		panel_4.add(textField_erodeMinSlope);

		textField_erodeMaxSlope = new JTextField("" + Constants.EROSION_MAX_SLOPE);
		panel_4.add(textField_erodeMaxSlope);
		textField_erodeMaxSlope.setColumns(10);

		textField_erodeSediment = new JTextField("" + Constants.EROSION_MAX_SEDIMENT);
		textField_erodeSediment.setColumns(10);
		panel_4.add(textField_erodeSediment);

		JPanel panel_5 = new JPanel();

		btnErodeHeightmap = new JButton("Erode Heightmap");
		panel_5.add(btnErodeHeightmap);
		GroupLayout gl_erosionPanel = new GroupLayout(erosionPanel);
		gl_erosionPanel.setHorizontalGroup(
				gl_erosionPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_erosionPanel.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_erosionPanel.createParallelGroup(Alignment.TRAILING)
								.addComponent(panel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
								.addComponent(panel_5, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE))
								.addContainerGap())
				);
		gl_erosionPanel.setVerticalGroup(
				gl_erosionPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_erosionPanel.createSequentialGroup()
						.addContainerGap()
						.addComponent(panel_5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
						.addContainerGap())
				);
		erosionPanel.setLayout(gl_erosionPanel);

		JPanel dropDirtPanel = new JPanel();
		optionsPane.addTab("Drop Dirt", null, dropDirtPanel, null);
		optionsPane.setEnabledAt(2, true);

		JPanel panel_9 = new JPanel();

		JPanel panel_10 = new JPanel();
		panel_9.add(panel_10);
		panel_10.setLayout(new GridLayout(0, 2, 0, 0));

		JPanel panel_11 = new JPanel();
		panel_10.add(panel_11);
		panel_11.setLayout(new GridLayout(0, 1, 0, 2));

		JLabel lblBiomeSeed = new JLabel("Biome Seed");
		panel_11.add(lblBiomeSeed);

		JLabel lblDirtPerTile = new JLabel("Dirt Per Tile");
		panel_11.add(lblDirtPerTile);

		JLabel lblNewLabel_1 = new JLabel("Max Dirt Slope");
		panel_11.add(lblNewLabel_1);

		JLabel lblMaxDirtSlope = new JLabel("Max Diagonal Slope");
		panel_11.add(lblMaxDirtSlope);

		JLabel lblMaxDirtHeight = new JLabel("Max Dirt Height");
		panel_11.add(lblMaxDirtHeight);

		JLabel lblWaterHeight = new JLabel("Water Height");
		panel_11.add(lblWaterHeight);

		JLabel lblCliffRatio = new JLabel("Cliff Ratio");
		panel_11.add(lblCliffRatio);

		JLabel label_8 = new JLabel("");
		panel_11.add(label_8);

		JLabel label_4 = new JLabel("");
		panel_11.add(label_4);

		JPanel panel_12 = new JPanel();
		panel_10.add(panel_12);
		panel_12.setLayout(new GridLayout(0, 1, 0, 2));

		textField_biomeSeed = new JTextField("" + System.currentTimeMillis());
		textField_biomeSeed.setColumns(10);
		panel_12.add(textField_biomeSeed);

		textField_dirtPerTile = new JTextField("" + Constants.DIRT_DROP_COUNT);
		textField_dirtPerTile.setColumns(10);
		panel_12.add(textField_dirtPerTile);

		textField_maxDirtSlope = new JTextField("" + Constants.MAX_DIRT_SLOPE);
		panel_12.add(textField_maxDirtSlope);
		textField_maxDirtSlope.setColumns(10);

		textField_maxDiagSlope = new JTextField("" + Constants.MAX_DIRT_DIAG_SLOPE);
		textField_maxDiagSlope.setColumns(10);
		panel_12.add(textField_maxDiagSlope);

		textField_maxDirtHeight = new JTextField("" + Constants.ROCK_WEIGHT);
		textField_maxDirtHeight.setColumns(10);
		panel_12.add(textField_maxDirtHeight);

		textField_waterHeight = new JTextField("" + Constants.WATER_HEIGHT);
		textField_waterHeight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblWater.setText("Water: "+textField_waterHeight.getText());
			}
		});
		textField_waterHeight.setColumns(10);
		panel_12.add(textField_waterHeight);

		textField_cliffRatio = new JTextField("" + Constants.CLIFF_RATIO);
		panel_12.add(textField_cliffRatio);
		textField_cliffRatio.setColumns(10);

		checkBox_landSlide = new JCheckBox("Land Slide");
		checkBox_landSlide.setSelected(true);
		panel_12.add(checkBox_landSlide);

		checkbox_biomeRandomSeed = new JCheckBox("Random Seed");
		panel_12.add(checkbox_biomeRandomSeed);
		checkbox_biomeRandomSeed.setSelected(true);

		JPanel panel_13 = new JPanel();

		btnDropDirt = new JButton("Drop Dirt");
		panel_13.add(btnDropDirt);
		GroupLayout gl_dropDirtPanel = new GroupLayout(dropDirtPanel);
		gl_dropDirtPanel.setHorizontalGroup(
				gl_dropDirtPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_dropDirtPanel.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_dropDirtPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(panel_9, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
								.addComponent(panel_13, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE))
								.addContainerGap())
				);
		gl_dropDirtPanel.setVerticalGroup(
				gl_dropDirtPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_dropDirtPanel.createSequentialGroup()
						.addContainerGap()
						.addComponent(panel_13, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(panel_9, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
						.addContainerGap())
				);

		btnUpdateWater = new JButton("Update Water");
		panel_13.add(btnUpdateWater);
		dropDirtPanel.setLayout(gl_dropDirtPanel);

		JPanel biomePanel = new JPanel();
		optionsPane.addTab("Biomes", null, biomePanel, null);
		optionsPane.setEnabledAt(3, true);

		JPanel panel_14 = new JPanel();

		JPanel panel_8 = new JPanel();
		panel_14.add(panel_8);
		panel_8.setLayout(new BorderLayout(0, 0));

		JPanel panel_15 = new JPanel();
		panel_8.add(panel_15);
		panel_15.setLayout(new GridLayout(0, 2, 0, 0));

		JPanel panel_16 = new JPanel();
		panel_15.add(panel_16);
		panel_16.setLayout(new GridLayout(0, 1, 0, 2));

		JLabel lblSeedCount = new JLabel("Seed Count");
		panel_16.add(lblSeedCount);

		JLabel lblBiomeSize = new JLabel("Biome Size");
		panel_16.add(lblBiomeSize);

		JLabel lblMaxSlope = new JLabel("Max Slope");
		panel_16.add(lblMaxSlope);

		JLabel lblMinHeight = new JLabel("Min Height");
		panel_16.add(lblMinHeight);

		JLabel lblMaxHeight_1 = new JLabel("Max Height");
		panel_16.add(lblMaxHeight_1);

		lblWater = new JLabel("Water: "+textField_waterHeight.getText());
		panel_16.add(lblWater);

		JLabel lblGrowth = new JLabel("Growth %");
		panel_16.add(lblGrowth);

		JLabel lblNorth = new JLabel(" - North");
		panel_16.add(lblNorth);

		JLabel lblSouth = new JLabel(" - South");
		panel_16.add(lblSouth);

		JLabel lblEast = new JLabel(" - East");
		panel_16.add(lblEast);

		JLabel lblWest = new JLabel(" - West");
		panel_16.add(lblWest);

		JLabel label_3 = new JLabel("");
		panel_16.add(label_3);

		JLabel lblRandomMin = new JLabel("Random Min");
		panel_16.add(lblRandomMin);

		JLabel lblRandomMax = new JLabel("Random Max");
		panel_16.add(lblRandomMax);

		JPanel panel_17 = new JPanel();
		panel_15.add(panel_17);
		panel_17.setLayout(new GridLayout(0, 1, 0, 2));

		textField_seedCount = new JTextField("" + Constants.BIOME_SEEDS);
		textField_seedCount.setColumns(10);
		panel_17.add(textField_seedCount);

		textField_biomeSize = new JTextField("" + Constants.BIOME_SIZE);
		textField_biomeSize.setColumns(10);
		panel_17.add(textField_biomeSize);

		textField_biomeMaxSlope = new JTextField("" + Constants.BIOME_MAX_SLOPE);
		textField_biomeMaxSlope.setColumns(10);
		panel_17.add(textField_biomeMaxSlope);

		textField_biomeMinHeight = new JTextField("" + Constants.BIOME_MIN_HEIGHT);
		panel_17.add(textField_biomeMinHeight);
		textField_biomeMinHeight.setColumns(10);

		textField_biomeMaxHeight = new JTextField("" + Constants.BIOME_MAX_HEIGHT);
		textField_biomeMaxHeight.setColumns(10);
		panel_17.add(textField_biomeMaxHeight);

		checkbox_AroundWater = new JCheckBox("Around Water", true);
		panel_17.add(checkbox_AroundWater);

		JLabel label_6 = new JLabel("");
		panel_17.add(label_6);

		textField_growthN = new JTextField("" + Constants.BIOME_RATE / 2);
		textField_growthN.setEnabled(false);
		panel_17.add(textField_growthN);
		textField_growthN.setColumns(10);

		textField_growthS = new JTextField("" + (int) (Constants.BIOME_RATE * 1.3));
		textField_growthS.setEnabled(false);
		panel_17.add(textField_growthS);
		textField_growthS.setColumns(10);

		textField_growthE = new JTextField("" + (int) (Constants.BIOME_RATE * 0.6));
		textField_growthE.setEnabled(false);
		panel_17.add(textField_growthE);
		textField_growthE.setColumns(10);

		textField_growthW = new JTextField("" + Constants.BIOME_RATE);
		textField_growthW.setEnabled(false);
		panel_17.add(textField_growthW);
		textField_growthW.setColumns(10);

		checkbox_growthRandom = new JCheckBox("Randomize");
		checkbox_growthRandom.setSelected(true);
		checkbox_growthRandom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (checkbox_growthRandom.isSelected()) {
					textField_growthN.setEnabled(false);
					textField_growthS.setEnabled(false);
					textField_growthE.setEnabled(false);
					textField_growthW.setEnabled(false);
					textField_growthMin.setEnabled(true);
					textField_growthMax.setEnabled(true);
				} else {
					textField_growthN.setEnabled(true);
					textField_growthS.setEnabled(true);
					textField_growthE.setEnabled(true);
					textField_growthW.setEnabled(true);
					textField_growthMin.setEnabled(false);
					textField_growthMax.setEnabled(false);
				}
			}
		});
		panel_17.add(checkbox_growthRandom);

		textField_growthMin = new JTextField("" + Constants.BIOME_RANDOM_MIN);
		panel_17.add(textField_growthMin);
		textField_growthMin.setColumns(10);

		textField_growthMax = new JTextField("" + Constants.BIOME_RANDOM_MAX);
		panel_17.add(textField_growthMax);
		textField_growthMax.setColumns(10);

		JPanel panel_27 = new JPanel();
		panel_8.add(panel_27, BorderLayout.NORTH);

		comboBox_biomeType = new JComboBox(new Tile[] { Tile.TILE_CLAY, Tile.TILE_DIRT, Tile.TILE_DIRT_PACKED, Tile.TILE_GRASS, Tile.TILE_GRAVEL, Tile.TILE_KELP,
				Tile.TILE_LAVA, Tile.TILE_MARSH, Tile.TILE_MOSS, Tile.TILE_MYCELIUM, Tile.TILE_PEAT, Tile.TILE_REED, Tile.TILE_SAND, Tile.TILE_STEPPE, 
				Tile.TILE_TAR, Tile.TILE_TUNDRA, Tile.TILE_TREE_APPLE, Tile.TILE_TREE_BIRCH, Tile.TILE_TREE_CEDAR, Tile.TILE_TREE_CHERRY, Tile.TILE_TREE_CHESTNUT, 
				Tile.TILE_TREE_FIR, Tile.TILE_TREE_LEMON, Tile.TILE_TREE_LINDEN, Tile.TILE_TREE_MAPLE, Tile.TILE_TREE_OAK, Tile.TILE_TREE_OLIVE, Tile.TILE_TREE_PINE,
				Tile.TILE_TREE_WALNUT, Tile.TILE_TREE_WILLOW, Tile.TILE_BUSH_CAMELLIA, Tile.TILE_BUSH_GRAPE, Tile.TILE_BUSH_LAVENDER, Tile.TILE_BUSH_OLEANDER,
				Tile.TILE_BUSH_ROSE, Tile.TILE_BUSH_THORN
		});
		panel_27.add(comboBox_biomeType);
		comboBox_biomeType.setSelectedIndex(12);

		JPanel panel_18 = new JPanel();

		btnAddBiome = new JButton("Add Biome");
		panel_18.add(btnAddBiome);

		btnUndoLastBiome = new JButton("Undo Last");
		panel_18.add(btnUndoLastBiome);
		GroupLayout gl_biomePanel = new GroupLayout(biomePanel);
		gl_biomePanel.setHorizontalGroup(
				gl_biomePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_biomePanel.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_biomePanel.createParallelGroup(Alignment.LEADING)
								.addComponent(panel_14, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
								.addComponent(panel_18, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE))
								.addContainerGap())
				);
		gl_biomePanel.setVerticalGroup(
				gl_biomePanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_biomePanel.createSequentialGroup()
						.addContainerGap()
						.addComponent(panel_18, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(panel_14, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
						.addContainerGap())
				);

		btnResetBiomes = new JButton("Reset All");
		panel_18.add(btnResetBiomes);
		biomePanel.setLayout(gl_biomePanel);

		JPanel orePanel = new JPanel();
		optionsPane.addTab("Ores", null, orePanel, null);
		optionsPane.setEnabledAt(4, true);

		JPanel panel_19 = new JPanel();

		JPanel panel_20 = new JPanel();
		panel_19.add(panel_20);
		panel_20.setLayout(new GridLayout(0, 2, 0, 0));

		JPanel panel_21 = new JPanel();
		panel_20.add(panel_21);
		panel_21.setLayout(new GridLayout(0, 1, 0, 2));

		JLabel lblIron = new JLabel("Iron");
		panel_21.add(lblIron);

		JLabel lblGold = new JLabel("Gold");
		panel_21.add(lblGold);

		JLabel lblSilver = new JLabel("Silver");
		panel_21.add(lblSilver);

		JLabel lblZinc = new JLabel("Zinc");
		panel_21.add(lblZinc);

		JLabel lblCopper = new JLabel("Copper");
		panel_21.add(lblCopper);

		JLabel lblLead = new JLabel("Lead");
		panel_21.add(lblLead);

		JLabel lblTin = new JLabel("Tin");
		panel_21.add(lblTin);

		JLabel lblMarble = new JLabel("Marble");
		panel_21.add(lblMarble);

		JLabel lblSlate = new JLabel("Slate");
		panel_21.add(lblSlate);

		JLabel lblAddy = new JLabel("Addy");
		panel_21.add(lblAddy);

		JLabel lblGlimmer = new JLabel("Glimmer");
		panel_21.add(lblGlimmer);

		JLabel lblRock = new JLabel("Rock");
		panel_21.add(lblRock);

		JPanel panel_22 = new JPanel();
		panel_20.add(panel_22);
		panel_22.setLayout(new GridLayout(0, 1, 0, 2));

		textField_Iron = new JTextField("" + Constants.ORE_IRON);
		textField_Iron.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRockTotal();
			}
		});
		textField_Iron.setColumns(10);
		panel_22.add(textField_Iron);

		textField_Gold = new JTextField("" + Constants.ORE_GOLD);
		textField_Gold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRockTotal();
			}
		});
		textField_Gold.setColumns(10);
		panel_22.add(textField_Gold);

		textField_Silver = new JTextField("" + Constants.ORE_SILVER);
		textField_Silver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRockTotal();
			}
		});
		textField_Silver.setColumns(10);
		panel_22.add(textField_Silver);

		textField_Zinc = new JTextField("" + Constants.ORE_ZINC);
		textField_Zinc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRockTotal();
			}
		});
		textField_Zinc.setColumns(10);
		panel_22.add(textField_Zinc);

		textField_Copper = new JTextField("" + Constants.ORE_COPPER);
		textField_Copper.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRockTotal();
			}
		});
		textField_Copper.setColumns(10);
		panel_22.add(textField_Copper);

		textField_Lead = new JTextField("" + Constants.ORE_LEAD);
		textField_Lead.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRockTotal();
			}
		});
		panel_22.add(textField_Lead);
		textField_Lead.setColumns(10);

		textField_Tin = new JTextField("" + Constants.ORE_TIN);
		textField_Tin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRockTotal();
			}
		});
		textField_Tin.setColumns(10);
		panel_22.add(textField_Tin);

		textField_Marble = new JTextField("" + Constants.ORE_MARBLE);
		textField_Marble.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRockTotal();
			}
		});
		textField_Marble.setColumns(10);
		panel_22.add(textField_Marble);

		textField_Slate = new JTextField("" + Constants.ORE_SLATE);
		textField_Slate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRockTotal();
			}
		});
		textField_Slate.setColumns(10);
		panel_22.add(textField_Slate);

		textField_Addy = new JTextField("" + Constants.ORE_ADDY);
		textField_Addy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRockTotal();
			}
		});
		textField_Addy.setColumns(10);
		panel_22.add(textField_Addy);

		textField_Glimmer = new JTextField("" + Constants.ORE_GLIMMER);
		textField_Glimmer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRockTotal();
			}
		});
		textField_Glimmer.setColumns(10);
		panel_22.add(textField_Glimmer);

		textField_Rock = new JTextField("");
		textField_Rock.setEditable(false);
		textField_Rock.setColumns(10);
		panel_22.add(textField_Rock);

		JPanel panel_23 = new JPanel();

		btnGenerateOres = new JButton("Generate Ores");
		panel_23.add(btnGenerateOres);
		GroupLayout gl_orePanel = new GroupLayout(orePanel);
		gl_orePanel.setHorizontalGroup(
				gl_orePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_orePanel.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_orePanel.createParallelGroup(Alignment.TRAILING)
								.addComponent(panel_19, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
								.addComponent(panel_23, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE))
								.addContainerGap())
				);
		gl_orePanel.setVerticalGroup(
				gl_orePanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_orePanel.createSequentialGroup()
						.addContainerGap()
						.addComponent(panel_23, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(panel_19, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
						.addContainerGap())
				);
		orePanel.setLayout(gl_orePanel);

		JPanel actionPanel = new JPanel();
		optionsPane.addTab("Import/Export", null, actionPanel, null);
		optionsPane.setEnabledAt(5, true);

		JPanel panel_24 = new JPanel();

		JPanel panel_26 = new JPanel();
		panel_24.add(panel_26);
		panel_26.setLayout(new GridLayout(0, 1, 0, 2));

		JLabel lblNewLabel_2 = new JLabel("Map Name");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		panel_26.add(lblNewLabel_2);

		textField_mapName = new JTextField(textField_mapSeed.getText());
		mapName = textField_mapSeed.getText();
		panel_26.add(textField_mapName);
		textField_mapName.setColumns(10);

		btnUpdateMapName = new JButton("Update Map Name");
		btnUpdateMapName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mapName = textField_mapName.getText();
				if (!apiClosed) {
					getAPI().close();
				}
				apiClosed = true;
				updateAPIMap();
			}
		});
		panel_26.add(btnUpdateMapName);

		JLabel label_1 = new JLabel("");
		panel_26.add(label_1);

		btnSaveImageDumps = new JButton("Save Image Dumps");
		panel_26.add(btnSaveImageDumps);

		btnSaveMapFiles = new JButton("Save Map Files");
		panel_26.add(btnSaveMapFiles);

		btnSaveActions = new JButton("Save Actions");

		JLabel label_2 = new JLabel("");
		panel_26.add(label_2);
		panel_26.add(btnSaveActions);

		btnLoadActions = new JButton("Load Actions");
		btnLoadActions.setEnabled(true);
		panel_26.add(btnLoadActions);

		JButton btnClearActions = new JButton("Clear Actions");
		btnClearActions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				genHistory.clear();
			}
		});
		panel_26.add(btnClearActions);

		JLabel lblNewLabel_3 = new JLabel("");
		panel_26.add(lblNewLabel_3);

		btnSaveHeightmap = new JButton("Save Heightmap");
		panel_26.add(btnSaveHeightmap);

		btnLoadHeightmap = new JButton("Load Heightmap");
		panel_26.add(btnLoadHeightmap);
		GroupLayout gl_actionPanel = new GroupLayout(actionPanel);
		gl_actionPanel.setHorizontalGroup(
				gl_actionPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_actionPanel.createSequentialGroup()
						.addContainerGap()
						.addComponent(panel_24, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
						.addContainerGap())
				);
		gl_actionPanel.setVerticalGroup(
				gl_actionPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_actionPanel.createSequentialGroup()
						.addContainerGap()
						.addComponent(panel_24, GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
						.addContainerGap())
				);
		actionPanel.setLayout(gl_actionPanel);
		contentPane.setLayout(gl_contentPane);

		init();
	}

	private void init() {
		setupButtonActions();
		setRockTotal();
	}

	private void setupButtonActions() {

		btnViewMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionViewMap();
					}
				}.start();
			}
		});
		btnTopo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionViewTopo();
					}
				}.start();
			}
		});
		btnViewCave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionViewCave();
					}
				}.start();
			}
		});
		btnViewHeight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionViewHeightmap();
					}
				}.start();
			}
		});
		btnGenerateHeightmap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionGenerateHeightmap();
					}
				}.start();
			}
		});
		btnErodeHeightmap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionErodeHeightmap();
					}
				}.start();
			}
		});
		btnDropDirt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionDropDirt();
					}
				}.start();
			}
		});
		btnUpdateWater.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionUpdateWater();
					}
				}.start();
			}
		});
		btnAddBiome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionSeedBiome();
					}
				}.start();
			}
		});
		btnUndoLastBiome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionUndoBiome();
					}
				}.start();
			}
		});
		btnResetBiomes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionResetBiomes();
					}
				}.start();
			}
		});
		btnGenerateOres.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionGenerateOres();
					}
				}.start();
			}
		});
		btnSaveImageDumps.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionSaveImages();
					}
				}.start();
			}
		});
		btnSaveMapFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionSaveMap();
					}
				}.start();
			}
		});
		btnSaveActions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionSaveActions();
					}
				}.start();
			}
		});
		btnLoadActions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionLoadActions();
					}
				}.start();
			}
		});
		btnSaveHeightmap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionSaveHeightmap();
					}
				}.start();
			}
		});
		btnLoadHeightmap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!actionReady())
					return;
				new Thread() {
					@Override
					public void run() {
						actionLoadHeightmap();
					}
				}.start();
			}
		});

	}



	private void startLoading(String task) {
		progressBar.setValue(0);
		progressBar.setString(task);
	}

	private void stopLoading() {
		progressBar.setValue(100);
		progressBar.setString("");
	}

	private boolean actionReady() {
		return progressBar.getValue() == 100;
	}


	public void actionGenerateHeightmap () {

		startLoading("Generating Height Map ()");
		try {
			api = null;
			genHistory = new ArrayList<String>();

			if (checkbox_mapRandomSeed.isSelected()) {
				textField_mapSeed.setText("" + System.currentTimeMillis());
			}

			mapPanel.setMapSize((int) comboBox_mapSize.getSelectedItem());

			newHeightMap(textField_mapSeed.getText().hashCode(), (int) comboBox_mapSize.getSelectedItem(), 
					Double.parseDouble(textField_mapResolution.getText()), Integer.parseInt(textField_mapIterations.getText()), 
					Integer.parseInt(textField_mapMinEdge.getText()), Double.parseDouble(textField_mapBorderWeight.getText()), 
					Integer.parseInt(textField_mapMaxHeight.getText()), checkbox_moreLand.isSelected());

			genHistory.add("HEIGHTMAP:" + textField_mapSeed.getText() + "," + comboBox_mapSize.getSelectedIndex() + "," + textField_mapResolution.getText() + "," +
					textField_mapIterations.getText() + "," + textField_mapMinEdge.getText() + "," + textField_mapBorderWeight.getText() + "," +
					textField_mapMaxHeight.getText() + "," + checkbox_moreLand.isSelected());
		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Generating HeightMap", JOptionPane.ERROR_MESSAGE);
		} finally {
			stopLoading();
		}
	}

	public void actionErodeHeightmap () {
		if (heightMap == null) {
			JOptionPane.showMessageDialog(null, "HeightMap does not exist", "Error Eroding HeightMap", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Eroding Height Map ()");
		try {
			heightMap.erode(Integer.parseInt(textField_erodeIterations.getText()), Integer.parseInt(textField_erodeMinSlope.getText()), 
					Integer.parseInt(textField_erodeMaxSlope.getText()), Integer.parseInt(textField_erodeSediment.getText()), progressBar);

			updateMapView(false, 0);

			genHistory.add("ERODE:" + textField_erodeIterations.getText() + "," + textField_erodeMinSlope.getText() + "," + textField_erodeSediment.getText());
		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Eroding HeightMap", JOptionPane.ERROR_MESSAGE);
		} finally {
			stopLoading();
		}
	}

	public void actionDropDirt () {
		if (heightMap == null) {
			JOptionPane.showMessageDialog(null, "HeightMap does not exist", "Error Dropping Dirt", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Dropping Dirt ()");
		try {
			if (checkbox_biomeRandomSeed.isSelected()) {
				textField_biomeSeed.setText("" + System.currentTimeMillis());
			}
			lblWater.setText("Water: "+textField_waterHeight.getText());

			tileMap = new TileMap(heightMap);
			tileMap.setBiomeSeed(textField_biomeSeed.getText().hashCode());
			tileMap.setWaterHeight(Integer.parseInt(textField_waterHeight.getText()));
			tileMap.dropDirt(Integer.parseInt(textField_dirtPerTile.getText()), Integer.parseInt(textField_maxDirtSlope.getText()), 
					Integer.parseInt(textField_maxDiagSlope.getText()), Integer.parseInt(textField_maxDirtHeight.getText()), 
					Double.parseDouble(textField_cliffRatio.getText()), checkBox_landSlide.isSelected(), progressBar);

			updateMapView(true, 0);

			genHistory.add("DROPDIRT:" + textField_biomeSeed.getText() + "," + textField_waterHeight.getText() + "," + textField_dirtPerTile.getText() + "," +
					textField_maxDirtSlope.getText() + "," + textField_maxDiagSlope.getText() + "," + textField_maxDirtHeight.getText());
		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Dropping Dirt", JOptionPane.ERROR_MESSAGE);
		} finally {
			stopLoading();
		}
	}

	public void actionUpdateWater () {
		if (heightMap == null) {
			JOptionPane.showMessageDialog(null, "HeightMap does not exist", "Error Updating Water", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (tileMap == null) {
			JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first", "Error Updating Water", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Updating Water");
		try {
			lblWater.setText("Water: "+textField_waterHeight.getText());
			tileMap.setWaterHeight(Integer.parseInt(textField_waterHeight.getText()));

			updateMapView(true, 0);

			genHistory.add("UPDATEWATER:" + textField_waterHeight.getText());
		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Updating Water", JOptionPane.ERROR_MESSAGE);
		} finally {
			stopLoading();
		}
	}

	public void actionSeedBiome () {
		if (tileMap == null) {
			JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first", "Error Adding Biome", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Seeding Biome");
		try {
			double[] rates = new double[4];

			if (checkbox_growthRandom.isSelected()) {
				int min = Integer.parseInt(textField_growthMin.getText());
				int max = Integer.parseInt(textField_growthMax.getText());
				rates[0] = (int)(Math.random()*(max-min)+min) / 100.0;
				rates[1] = (int)(Math.random()*(max-min)+min) / 100.0;
				rates[2] = (int)(Math.random()*(max-min)+min) / 100.0;
				rates[3] = (int)(Math.random()*(max-min)+min) / 100.0;
			} else {
				rates[0] = Integer.parseInt(textField_growthN.getText()) / 100.0;
				rates[1] = Integer.parseInt(textField_growthS.getText()) / 100.0; 
				rates[2] = Integer.parseInt(textField_growthE.getText()) / 100.0; 
				rates[3] = Integer.parseInt(textField_growthW.getText()) / 100.0; 
			}

			int minHeight = checkbox_AroundWater.isSelected()
					? Integer.parseInt(textField_waterHeight.getText())-Integer.parseInt(textField_biomeMinHeight.getText())
							: Integer.parseInt(textField_biomeMinHeight.getText());
					int maxHeight = checkbox_AroundWater.isSelected()
							? Integer.parseInt(textField_waterHeight.getText())+Integer.parseInt(textField_biomeMaxHeight.getText())
									: Integer.parseInt(textField_biomeMaxHeight.getText());

							tileMap.plantBiome(Integer.parseInt(textField_seedCount.getText()), Integer.parseInt(textField_biomeSize.getText()), rates, 
									Integer.parseInt(textField_biomeMaxSlope.getText()), minHeight, maxHeight, (Tile) comboBox_biomeType.getSelectedItem(), progressBar);

							updateMapView(true, 0);

							genHistory.add("SEEDBIOME("+comboBox_biomeType.getSelectedItem()+"):" + comboBox_biomeType.getSelectedIndex() + "," + 
									textField_seedCount.getText() + "," + textField_biomeSize.getText() + "," + textField_biomeMaxSlope.getText() + "," + 
									(int)(100*rates[0]) + "," + (int)(100*rates[1]) + "," + (int)(100*rates[2]) + "," + (int)(100*rates[3]) + "," + 
									textField_biomeMinHeight.getText() + "," + textField_biomeMaxHeight.getText() + "," + checkbox_AroundWater.isSelected());
		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Dropping Dirt", JOptionPane.ERROR_MESSAGE);
		} finally {
			stopLoading();
		}
	}

	public void actionUndoBiome () {
		if (tileMap == null) {
			JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first", "Error Resetting Biomes", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Undoing Biome");
		try {

			tileMap.undoLastBiome();

			updateMapView(true, 0);

			genHistory.add("UNDOBIOME:null");
		} finally {
			stopLoading();
		}
	}

	public void actionResetBiomes () {
		if (tileMap == null) {
			JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first", "Error Resetting Biomes", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Resetting Biomes");
		try {

			for (int i = 0; i < heightMap.getMapSize(); i++) {
				for (int j = 0; j < heightMap.getMapSize(); j++) {
					progressBar.setValue((int)((float)(i*heightMap.getMapSize()+j)/(heightMap.getMapSize()*heightMap.getMapSize())*90f));
					tileMap.addDirt(i, j, 0);
				}
			}

			updateMapView(true, 0);

			genHistory.add("RESETBIOMES:null"); 
		} finally {
			stopLoading();
		}
	}

	public void actionGenerateOres () {
		if (tileMap == null) {
			JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first", "Error Resetting Biomes", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Generating Ores");
		try {
			setRockTotal();
			if (Double.parseDouble(textField_Rock.getText()) < 0.0 || Double.parseDouble(textField_Rock.getText()) > 100.0) {
				JOptionPane.showMessageDialog(null, "Ore values out of range", "Error Generating Ore", JOptionPane.ERROR_MESSAGE);
				return;
			}

			double[] rates = { Double.parseDouble(textField_Rock.getText()), Double.parseDouble(textField_Iron.getText()), Double.parseDouble(textField_Gold.getText()),
					Double.parseDouble(textField_Silver.getText()), Double.parseDouble(textField_Zinc.getText()), Double.parseDouble(textField_Copper.getText()),
					Double.parseDouble(textField_Lead.getText()), Double.parseDouble(textField_Tin.getText()), Double.parseDouble(textField_Addy.getText()),
					Double.parseDouble(textField_Glimmer.getText()), Double.parseDouble(textField_Marble.getText()), Double.parseDouble(textField_Slate.getText())					
			};

			tileMap.generateOres(rates, progressBar);

			updateMapView(true, 2);

			genHistory.add("GENORES:" + textField_Rock.getText() + "," + textField_Iron.getText() + "," + textField_Gold.getText() + "," +
					textField_Silver.getText() + "," + textField_Zinc.getText() + "," + textField_Copper.getText() + "," +
					textField_Lead.getText() + "," + textField_Tin.getText() + "," + textField_Addy.getText() + "," +
					textField_Glimmer.getText() + "," + textField_Marble.getText() + "," + textField_Slate.getText());
		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Generating Ores", JOptionPane.ERROR_MESSAGE);
		} finally {
			stopLoading();
		}
	}

	public void actionViewMap () {
		if (tileMap == null) {
			JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first", "Error Showing Map", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Loading");
		try {
			updateMapView(true, 0);
		} finally {
			stopLoading();
		}
	}

	public void actionViewTopo () {
		if (tileMap == null) {
			JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first", "Error Showing Map", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Loading");
		try {
			updateMapView(true, 1);
		} finally {
			stopLoading();
		}
	}

	public void actionViewCave () {
		if (tileMap == null) {
			JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first", "Error Showing Map", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (!tileMap.hasOres()) {
			JOptionPane.showMessageDialog(null, "No Cave Map - Generate Ores first", "Error Showing Map", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Loading");
		try {
			updateMapView(true, 2);
		} finally {
			stopLoading();
		}
	}

	public void actionViewHeightmap () {
		if (heightMap == null) {
			JOptionPane.showMessageDialog(null, "HeightMap does not exist", "Error Showing Map", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Loading");
		try {
			updateMapView(false, 0);
		} finally {
			stopLoading();
		}
	}

	public void actionSaveImages () {
		if (tileMap == null) {
			JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first", "Error Saving Images", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Saving Images");
		try {
			updateAPIMap();
			MapData map = getAPI().getMapData();

			ImageIO.write(map.createMapDump(), "png", new File("./maps/" + mapName + "/map.png"));
			ImageIO.write(map.createTopographicDump(true, (short) 250), "png", new File("./maps/" + mapName + "/topography.png"));
			ImageIO.write(map.createCaveDump(true), "png", new File("./maps/" + mapName + "/cave.png"));
		} catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex);
		} finally {
			stopLoading();
		}
	}

	public void actionSaveMap () {
		if (tileMap == null) {
			JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first", "Error Saving Map", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Saving Map");
		try {
			updateAPIMap();

			getAPI().getMapData().saveChanges();
			getAPI().close();
			apiClosed = true;
		} finally {
			stopLoading();
		}
	}

	public void actionSaveActions () {
		if (tileMap == null) {
			JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first", "Error Saving Map", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Saving Actions");
		try {
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File("./maps/" + mapName));
			fc.setSelectedFile(new File("map_actions.act"));
			fc.setFileFilter(new TextFileView());
			fc.setAcceptAllFileFilterUsed(false);
			int returnVal = fc.showSaveDialog(this);
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				return;
			}

			File actionsFile = fc.getSelectedFile();
			actionsFile.createNewFile();

			BufferedWriter bw = new BufferedWriter(new FileWriter(actionsFile));
			for (String s : genHistory)
				bw.write(s + "\r\n");

			bw.close();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex);
		} finally {
			stopLoading();
		}
	}

	public void actionLoadActions () {

		startLoading("Loading Actions");
		try {
			File actionsFile;

			JFileChooser fc = new JFileChooser();
			fc.addChoosableFileFilter(new TextFileView());
			fc.setAcceptAllFileFilterUsed(false);
			fc.setCurrentDirectory(new File("./maps/"));

			int returnVal = fc.showDialog(null, "Load Actions");

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				actionsFile = fc.getSelectedFile();
				textField_mapName.setText(actionsFile.getParentFile().getName());
				actionsFileDirectory = actionsFile.getParentFile().getAbsolutePath();

				BufferedReader br = new BufferedReader(new FileReader(actionsFile));
				String line;
				while ((line = br.readLine()) != null) {
					parseAction(line);
				}

				br.close();
			}
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, "Unable to load actions file", "Error Loading Map", JOptionPane.ERROR_MESSAGE);
			logger.log(Level.WARNING, "Error loading actions file: " + ex.getMessage());
		} finally {
			stopLoading();
		}
	}

	public void actionLoadHeightmap () {

		startLoading("Loading Heightmap");
		try {
			File heightImageFile;

			JFileChooser fc = new JFileChooser();
			fc.addChoosableFileFilter(new ImageFileView());
			fc.setAcceptAllFileFilterUsed(false);
			fc.setCurrentDirectory(new File("./maps/"));

			int returnVal = fc.showDialog(this, "Load Heightmap");
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				return;
			}

			heightImageFile = fc.getSelectedFile();

			api = null;
			genHistory = new ArrayList<String>();

			newHeightMap(heightImageFile, (int)comboBox_mapSize.getSelectedItem(), Integer.parseInt(textField_mapMaxHeight.getText()));

			genHistory.add("IMPORTHEIGHTMAP:" + fc.getSelectedFile().getName() + 
					"," + comboBox_mapSize.getSelectedIndex() + "," + textField_mapMaxHeight.getText());

		} catch (NumberFormatException | IOException nfe) {
			JOptionPane.showMessageDialog(this, "Error loading file " + nfe.getMessage().toLowerCase(), "Error Loading Heightmap", JOptionPane.ERROR_MESSAGE);
		} finally {
			stopLoading();
		}
	}

	public void actionSaveHeightmap () {
		if (heightMap == null) {
			JOptionPane.showMessageDialog(this, "Heightmap does not exist - Generate one first", "Error Saving Heightmap", JOptionPane.ERROR_MESSAGE);
			return;
		}

		startLoading("Loading Heightmap");
		heightMap.exportHeightImage(mapName);
		stopLoading();
	}

	public void newHeightMap(File heightImageFile, int mapSize, int maxHeight) throws IOException {

		BufferedImage heightImage = new BufferedImage(mapSize, mapSize, BufferedImage.TYPE_USHORT_GRAY);
		heightImage = ImageIO.read(heightImageFile);
		mapPanel.setMapSize(mapSize);
		heightMap = new HeightMap(heightImage, mapSize, maxHeight);
		heightMap.importHeightImage();

		updateMapView(false, 0);
	}

	private WurmAPI getAPI() {
		if (apiClosed)
			api = null;

		if (api == null)
			try {
				api = WurmAPI.create("./maps/" + mapName + "/", (int) (Math.log(heightMap.getMapSize()) / Math.log(2)));
				apiClosed = false;
			} catch (IOException e) {
				e.printStackTrace();
			}

		return api;
	}

	public void newHeightMap(long seed, int mapSize, double resolution, int iterations, int minEdge, double borderWeight, int maxHeight, boolean moreLand) {
		heightMap = new HeightMap(seed, mapSize, resolution, iterations, minEdge, borderWeight, maxHeight, moreLand);

		heightMap.generateHeights(progressBar); 

		updateMapView(false, 0);
	}

	private void updateMapView(boolean apiView, int viewType) {
		if (!apiView) {
			startLoading("Loading View");
			Graphics g = mapPanel.getMapImage().getGraphics();

			for (int i = 0; i < heightMap.getMapSize(); i++) {
				progressBar.setValue((int)((float)i/heightMap.getMapSize()*98f));
				for (int j = 0; j < heightMap.getMapSize(); j++) {
					g.setColor(new Color((float) heightMap.getHeight(i, j), (float) heightMap.getHeight(i, j), (float) heightMap.getHeight(i, j)));
					g.fillRect(i, j, 1, 1);
				}
			}
		} else {
			updateAPIMap();

			if (viewType == 1)
				mapPanel.setMapImage(getAPI().getMapData().createTopographicDump(true, (short) 250));
			else if (viewType == 2)
				mapPanel.setMapImage(getAPI().getMapData().createCaveDump(true));
			else
				mapPanel.setMapImage(getAPI().getMapData().createMapDump());
		}

		mapPanel.updateScale();
		mapPanel.checkBounds();
		mapPanel.repaint();
		stopLoading();
	}

	private void updateAPIMap() {
		startLoading("Updating Map");
		MapData map = getAPI().getMapData();
		Random treeRand = new Random(System.currentTimeMillis());

		for (int i = 0; i < heightMap.getMapSize(); i++) {
			progressBar.setValue((int)((float)i/heightMap.getMapSize()*98f));
			for (int j = 0; j < heightMap.getMapSize(); j++) {
				map.setSurfaceHeight(i, j, tileMap.getSurfaceHeight(i, j));
				map.setRockHeight(i, j, tileMap.getRockHeight(i, j));

				if (tileMap.hasOres())
					map.setCaveTile(i, j, tileMap.getOreType(i, j), tileMap.getOreCount(i, j));

				if (tileMap.getType(i, j).isTree())
					map.setTree(i, j, tileMap.getType(i, j).getTreeType((byte) 0), 
							FoliageAge.values()[treeRand.nextInt(FoliageAge.values().length)], GrowthTreeStage.MEDIUM);
				else if (tileMap.getType(i, j).isBush())
					map.setBush(i, j, tileMap.getType(i, j).getBushType((byte) 0), 
							FoliageAge.values()[treeRand.nextInt(FoliageAge.values().length)], GrowthTreeStage.MEDIUM);
				else 
					map.setSurfaceTile(i, j, tileMap.getType(i, j));
			}
		}
		stopLoading();
	}

	private void parseAction(String action) {
		String[] parts = action.split(":");
		if (parts.length < 2)
			return;

		String[] options = parts[1].split(",");
		switch (parts[0]) {
		case "HEIGHTMAP":
			if (options.length != 8) {
				JOptionPane.showMessageDialog(null, "Not enough options for HEIGHTMAP", "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
				return;
			}

			try {
				textField_mapSeed.setText(options[0]);
				comboBox_mapSize.setSelectedIndex(Integer.parseInt(options[1]));
				textField_mapResolution.setText(options[2]);
				textField_mapIterations.setText(options[3]);
				textField_mapMinEdge.setText(options[4]);
				textField_mapBorderWeight.setText(options[5]);
				textField_mapMaxHeight.setText(options[6]);
				checkbox_moreLand.setSelected(Boolean.parseBoolean(options[7]));
				checkbox_mapRandomSeed.setSelected(false);

				actionGenerateHeightmap();
			} catch (Exception nfe) {
				JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
			}
			break;
		case "ERODE":
			if (options.length != 3) {
				JOptionPane.showMessageDialog(null, "Not enough options for ERODE", "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
				return;
			}

			textField_erodeIterations.setText(options[0]);
			textField_erodeMinSlope.setText(options[1]);
			textField_erodeSediment.setText(options[2]);

			actionErodeHeightmap();
			break;
		case "DROPDIRT":
			if (options.length != 6) {
				JOptionPane.showMessageDialog(null, "Not enough options for DROPDIRT", "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
				return;
			}

			textField_biomeSeed.setText(options[0]);
			textField_waterHeight.setText(options[1]);
			textField_dirtPerTile.setText(options[2]);
			textField_maxDirtSlope.setText(options[3]);
			textField_maxDiagSlope.setText(options[4]);
			textField_maxDirtHeight.setText(options[5]);
			checkbox_biomeRandomSeed.setSelected(false);

			actionDropDirt();
			break;
		case "UPDATEWATER":
			if (options.length != 1) {
				JOptionPane.showMessageDialog(null, "Not enough options for DROPDIRT", "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
				return;
			}

			textField_waterHeight.setText(options[0]);

			actionUpdateWater();
			break;
		case "UNDOBIOME":
			actionUndoBiome();
			break;
		case "RESETBIOMES":
			actionResetBiomes();
			break;
		case "GENORES":
			if (options.length != 12) {
				JOptionPane.showMessageDialog(null, "Not enough options for GENORES", "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
				return;
			}

			textField_Rock.setText(options[0]);
			textField_Iron.setText(options[1]);
			textField_Gold.setText(options[2]);
			textField_Silver.setText(options[3]);
			textField_Zinc.setText(options[4]);
			textField_Copper.setText(options[5]);
			textField_Lead.setText(options[6]);
			textField_Tin.setText(options[7]);
			textField_Addy.setText(options[8]);
			textField_Glimmer.setText(options[9]);
			textField_Marble.setText(options[10]);
			textField_Slate.setText(options[11]);

			actionGenerateOres();
			break;
		case "IMPORTHEIGHTMAP":
			if (options.length != 3) {
				JOptionPane.showMessageDialog(this, "Not enough options for HEIGHTMAP", "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
				return;
			}

			try{
				logger.log(Level.INFO, "1: " + options[0] + " 2: " + Integer.parseInt(options[1]) + " 3: " + options[2]);

				File heightImageFile = new File(actionsFileDirectory + "/" + options[0]);
				comboBox_mapSize.setSelectedIndex(Integer.parseInt(options[1]));
				textField_mapMaxHeight.setText(options[2]);

				api = null;
				genHistory = new ArrayList<String>();

				newHeightMap(heightImageFile, (int)comboBox_mapSize.getSelectedItem(), Integer.parseInt(textField_mapMaxHeight.getText()));

				genHistory.add("IMPORTHEIGHTMAP:" + heightImageFile.getName() + 
						"," + comboBox_mapSize.getSelectedIndex() + "," + textField_mapMaxHeight.getText());


			} catch (Exception nfe) {
				JOptionPane.showMessageDialog(this, "Error: " + nfe.getMessage().toLowerCase(), "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
			}
			break;
		default:
			if(parts[0].startsWith("SEEDBIOME")){
				if (options.length != 11) {
					JOptionPane.showMessageDialog(null, "Not enough options for SEEDBIOME", "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
					return;
				}

				try {
					comboBox_biomeType.setSelectedIndex(Integer.parseInt(options[0]));
					textField_seedCount.setText(options[1]);
					textField_biomeSize.setText(options[2]);
					textField_biomeMaxSlope.setText(options[3]);
					textField_growthN.setText(options[4]);
					textField_growthS.setText(options[5]);
					textField_growthE.setText(options[6]);
					textField_growthW.setText(options[7]);
					textField_biomeMinHeight.setText(options[8]);
					textField_biomeMaxHeight.setText(options[9]);
					checkbox_AroundWater.setSelected(Boolean.parseBoolean(options[10]));
					checkbox_growthRandom.setSelected(false);

					actionSeedBiome();
				} catch (Exception nfe) {
					JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
				}
			}
			break;
		}
	}

	public class TextFileView extends FileFilter {

		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}

			String extension = getExtension(f);
			if (extension != null)
				if (extension.equals("act"))
					return true;

			return false;
		}

		private String getExtension(File f) {
			String ext = null;
			String s = f.getName();
			int i = s.lastIndexOf('.');

			if (i > 0 &&  i < s.length() - 1) {
				ext = s.substring(i+1).toLowerCase();
			}
			return ext;
		}

		@Override
		public String getDescription() {
			return "Action Files (.act)";
		}
	}

	class ImageFileView extends FileFilter {

		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}

			String extension = getExtension(f);
			if (extension != null)
				if (extension.equals("png"))
					return true;

			return false;
		}

		private String getExtension(File f) {
			String ext = null;
			String s = f.getName();
			int i = s.lastIndexOf('.');

			if (i > 0 &&  i < s.length() - 1) {
				ext = s.substring(i+1).toLowerCase();
			}
			return ext;
		}

		@Override
		public String getDescription() {
			return "Heightmap Image (.png)";
		}
	}

	private void setRockTotal() {
		try {
			double[] rates = { Double.parseDouble(textField_Iron.getText()), Double.parseDouble(textField_Gold.getText()),
					Double.parseDouble(textField_Silver.getText()), Double.parseDouble(textField_Zinc.getText()), Double.parseDouble(textField_Copper.getText()),
					Double.parseDouble(textField_Lead.getText()), Double.parseDouble(textField_Tin.getText()), Double.parseDouble(textField_Addy.getText()),
					Double.parseDouble(textField_Glimmer.getText()), Double.parseDouble(textField_Marble.getText()), Double.parseDouble(textField_Slate.getText())					
			};

			float total = 0;
			for (int i = 0; i < rates.length; i++)
				total += rates[i];

			textField_Rock.setText("" + (100.0f - total));
		} catch (NumberFormatException nfe) {

		}
	}

}
