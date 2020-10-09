package net.buddat.wgenerator;

import com.wurmonline.mesh.FoliageAge;
import com.wurmonline.mesh.GrassData.FlowerType;
import com.wurmonline.mesh.GrassData.GrowthStage;
import com.wurmonline.mesh.GrassData.GrowthTreeStage;
import com.wurmonline.mesh.Tiles.Tile;
import com.wurmonline.wurmapi.api.MapData;
import com.wurmonline.wurmapi.api.WurmAPI;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import lombok.extern.slf4j.Slf4j;
import net.buddat.wgenerator.util.Constants;
import net.buddat.wgenerator.util.ProgressHandler;
import net.buddat.wgenerator.util.StreamCapturer;

@Slf4j
public class MainWindow extends JFrame {

  private static final long serialVersionUID = -407206109473532425L;

  private WurmAPI api;
  private HeightMap heightMap;
  private TileMap tileMap;
  private ArrayList<String> genHistory;
  private boolean apiClosed = true;
  private final MapPanel mapPanel;
  private String mapName;
  private String actionsFileDirectory;
  private Constants.ViewType defaultView = Constants.ViewType.HEIGHT;
  private ProgressHandler progress;

  private final JProgressBar progressBar;
  private final JLabel lblMemory;
  private final JPanel contentPane;
  private final JTextField textFieldMapSeed;
  private final JTextField textFieldMapResolution;
  private final JTextField textFieldMapMinEdge;
  private final JTextField textFieldMapBorderWeight;
  private final JTextField textFieldMapMaxHeight;
  private final JTextField textFieldMapIterations;
  private final JTextField textFieldErodeIterations;
  private final JTextField textFieldErodeMinSlope;
  private final JTextField textFieldErodeSediment;
  private final JTextField textFieldBiomeSeed;
  private final JTextField textFieldDirtPerTile;
  private final JTextField textFieldMaxDiagSlope;
  private final JTextField textFieldMaxDirtHeight;
  private final JTextField textFieldWaterHeight;
  private final JTextField textFieldSeedCount;
  private final JTextField textFieldBiomeSize;
  private final JTextField textFieldBiomeMaxSlope;
  private final JTextField textFieldBiomeMaxHeight;
  private final JTextField textFieldGrowthN;
  private final JTextField textFieldGrowthS;
  private final JTextField textFieldGrowthE;
  private final JTextField textFieldGrowthW;
  private final JTextField textFieldBiomeMinHeight;
  private final JTextField textFieldIron;
  private final JTextField textFieldGold;
  private final JTextField textFieldSilver;
  private final JTextField textFieldZinc;
  private final JTextField textFieldCopper;
  private final JTextField textFieldLead;
  private final JTextField textFieldTin;
  private final JTextField textFieldMarble;
  private final JTextField textFieldSlate;
  private final JTextField textFieldAddy;
  private final JTextField textFieldGlimmer;
  private final JTextField textFieldRock;
  private final JTextField textFieldMaxDirtSlope;
  private final JTextField textFieldMapName;
  private final JComboBox<Integer> comboBoxMapSize;
  private final JCheckBox checkboxBiomeRandomSeed;
  private final JComboBox<Tile> comboBoxBiomeType;
  private final JCheckBox checkboxMoreLand;
  private final JCheckBox checkboxMapRandomSeed;
  private final JButton btnGenerateHeightmap;
  private final JButton btnErodeHeightmap;
  private final JButton btnUpdateWater;
  private final JButton btnDropDirt;
  private final JButton btnGenerateOres;
  private final JButton btnResetBiomes;
  private final JButton btnUndoLastBiome;
  private final JButton btnAddBiome;
  private final JLabel lblWater;
  private final JCheckBox chckbxAroundWater;
  private final JTextField textFieldGrowthMin;
  private final JTextField textFieldGrowthMax;
  private final JCheckBox checkboxGrowthRandom;
  private final JButton btnSaveMapFiles;
  private final JButton btnSaveImageDumps;
  private final JButton btnSaveActions;
  private final JButton btnLoadActions;
  private final JButton btnViewHeight;
  private final JButton btnViewCave;
  private final JButton btnViewTopo;
  private final JButton btnViewMap;
  private final JTextField textFieldCliffRatio;
  private final JButton btnLoadHeightmap;
  private final JTextField textFieldErodeMaxSlope;
  private final JCheckBox checkBoxLandSlide;
  private final JButton btnUpdateMapName;
  private final JLabel lblMapCoords;
  private final JCheckBox chcekboxShowGrid;
  private final JTextField textFieldMapGridSize;
  private final JTextField textFieldBiomeDensity;
  private final JButton btnViewErrors;
  private final JTextArea textAreaErrors;
  private final CardLayout clMainPanel;
  private final JPanel mainPanel;
  private final JCheckBox checkboxPaintMode;
  private final JButton btnGenerateRivers;
  private final JCheckBox checkboxPaintRivers;
  private final JTextField textFieldRiverDepth;
  private final JTextField textFieldRiverWidth;
  private final JTextField textFieldRiverSlope;
  private final JButton btnResetRivers;
  private final JCheckBox checkboxAutoDropDirt;
  private final JTextField textFieldNormalizeRatio;
  private final JButton btnUndoRiver;

  //CHECKSTYLE:OFF
  private static final String[][] biomeOptionValue = {
      // Count,Size,MaxSlope,RateN,RateS,RateE,RateW,MinHeight,Maxheight,GrowtRandom,GrowMin,GrowMax,AroundWater,Density
      { "500", "5", "20", "70", "70", "70", "70", "20", "20", "true", "30", "70", "true", "1" }, // TILE_CLAY
      { "100", "3", "40", "70", "70", "70", "70", "50", "4000", "true", "30", "70", "true", "2" }, // TILE_DIRT
      { "10", "2", "10", "70", "70", "70", "70", "50", "1000", "true", "30", "70", "true", "1" }, // TILE_DIRT_PACKED
      { "100", "10", "40", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "2" }, // TILE_GRASS
      { "10", "2", "30", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "2" }, // TILE_GRAVEL
      { "100", "10", "20", "70", "70", "70", "70", "30", "0", "true", "30", "70", "true", "2" }, // TILE_KELP
      { "5", "1", "140", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "1" }, // TILE_LAVA
      { "100", "5", "20", "70", "70", "70", "70", "30", "2", "true", "30", "70", "true", "1" }, // TILE_MARSH
      { "100", "3", "20", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "1" }, // TILE_MOSS
      { "100", "3", "20", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "1" }, // TILE_MYCELIUM
      { "50", "5", "20", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "1" }, // TILE_PEAT
      { "100", "10", "20", "70", "70", "70", "70", "30", "0", "true", "30", "70", "true", "2" }, // TILE_REED
      { "200", "100", "30", "70", "70", "70", "70", "50", "50", "true", "30", "70", "true", "1" }, // TILE_SAND
      { "10", "50", "30", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "1" }, // TILE_STEPPE
      { "200", "1", "30", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "1" }, // TILE_TAR
      { "10", "50", "30", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "1" }, // TILE_TUNDRA
      { "250", "2", "20", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "2" }, // TILE_TREE_APPLE
      { "30", "20", "40", "40", "40", "40", "40", "0", "4000", "true", "30", "70", "true", "1" }, // TILE_TREE_BIRCH
      { "30", "20", "20", "40", "40", "40", "40", "0", "4000", "true", "30", "70", "true", "3" }, // TILE_TREE_CEDAR
      { "250", "2", "20", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "2" }, // TILE_TREE_CHERRY
      { "5", "100", "30", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "1" }, // TILE_TREE_CHESTNUT
      { "30", "20", "50", "40", "40", "40", "40", "0", "4000", "true", "30", "70", "true", "1" }, // TILE_TREE_FIR
      { "250", "2", "20", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "2" }, // TILE_TREE_LEMON
      { "30", "20", "20", "40", "40", "40", "40", "0", "4000", "true", "30", "70", "true", "1" }, // TILE_TREE_LINDEN
      { "30", "20", "30", "40", "40", "40", "40", "0", "4000", "true", "30", "70", "true", "1" }, // TILE_TREE_MAPLE
      { "250", "1", "20", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "1" }, // TILE_TREE_OAK
      { "30", "20", "30", "40", "40", "40", "40", "0", "4000", "true", "30", "70", "true", "2" }, // TILE_TREE_OLIVE
      { "30", "20", "50", "40", "40", "40", "40", "0", "4000", "true", "30", "70", "true", "3" }, // TILE_TREE_PINE
      { "30", "20", "20", "40", "40", "40", "40", "0", "4000", "true", "30", "70", "true", "1" }, // TILE_TREE_WALNUT
      { "250", "1", "20", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "2" }, // TILE_TREE_WILLOW
      { "250", "2", "20", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "2" }, // TILE_TREE_ORANGE
      { "500", "1", "40", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "1" }, // TILE_BUSH_CAMELLIA
      { "50", "10", "20", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "1" }, // TILE_BUSH_GRAPE
      { "500", "1", "30", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "1" }, // TILE_BUSH_LAVENDER
      { "500", "1", "30", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "1" }, // TILE_BUSH_OLEANDER
      { "500", "1", "30", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "1" }, // TILE_BUSH_ROSE
      { "500", "1", "50", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "1" }, // TILE_BUSH_THORN
      { "500", "1", "30", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "1" }, // TILE_BUSH_HAZELNET
      { "500", "1", "30", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "1" }, // TILE_BUSH_RASPBERRY
      { "500", "1", "50", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "1" }, // TILE_BUSH_BLUEBERRY
      { "500", "1", "50", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "1" }, // TILE_BUSH_LINGONBERRY
      { "30", "20", "30", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "1" }, // TILE_TREE
      { "500", "1", "30", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "1" }, // TILE_BUSH
      { "10", "20", "50", "70", "70", "70", "70", "0", "4000", "true", "30", "70", "true", "1" } }; // TILE_SNOW
  //CHECKSTYLE:ON
  private final JButton btnLoadBiomes;
  private final JButton btnExportBiomes;
  private final JButton btnImportBiomes;
  private final JTextField textFieldFlowerPercent;
  private final JComboBox<String> comboBoxFlowerType;
  private final JButton btnViewBiomes;
  private final JTextField textFieldSandstone;
  private final JTextField textFieldRocksalt;

  /** Entrypoint. */
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
      UIDefaults defaults = UIManager.getLookAndFeelDefaults();
      defaults.put("nimbusOrange", new Color(50, 205, 50));
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

  /** Construct the Window. */
  @SuppressWarnings({"unchecked"})
  public MainWindow() {
    setTitle(Constants.WINDOW_TITLE + " - v" + Constants.version);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 1000, 750);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setLocationRelativeTo(null);
    setContentPane(contentPane);

    final JTabbedPane optionsPane = new JTabbedPane(JTabbedPane.TOP);

    progressBar = new JProgressBar();
    progressBar.setStringPainted(true);
    progressBar.setString("");
    progressBar.setEnabled(true);
    progressBar.setValue(100);

    JPanel viewPanel = new JPanel();

    JPanel mapCoordsPanel = new JPanel();

    mainPanel = new JPanel();

    JPanel memoryPanel = new JPanel();
    final GroupLayout gl_contentPane = new GroupLayout(contentPane);
    gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
        .addGroup(gl_contentPane.createSequentialGroup().addContainerGap()
            .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                .addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)
                .addComponent(progressBar, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 641,
                    Short.MAX_VALUE)
                .addComponent(mapCoordsPanel, GroupLayout.DEFAULT_SIZE, 641, Short.MAX_VALUE)
                .addComponent(viewPanel, GroupLayout.DEFAULT_SIZE, 641, Short.MAX_VALUE))
            .addPreferredGap(ComponentPlacement.RELATED)
            .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
                .addComponent(memoryPanel, GroupLayout.PREFERRED_SIZE, 315,
                    GroupLayout.PREFERRED_SIZE)
                .addComponent(optionsPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                    GroupLayout.PREFERRED_SIZE))
            .addContainerGap()));
    gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
        .addGroup(gl_contentPane.createSequentialGroup().addGap(6)
            .addComponent(optionsPane, GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
            .addPreferredGap(ComponentPlacement.RELATED).addComponent(
                memoryPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                GroupLayout.PREFERRED_SIZE))
        .addGroup(gl_contentPane.createSequentialGroup()
            .addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(mapCoordsPanel, GroupLayout.PREFERRED_SIZE, 24,
                GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE)
            .addPreferredGap(ComponentPlacement.RELATED).addComponent(viewPanel,
                GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
    memoryPanel.setLayout(new GridLayout(0, 2, 5, 0));

    JLabel lblMemoryUsage = new JLabel("Memory Usage:");
    lblMemoryUsage.setHorizontalAlignment(SwingConstants.RIGHT);
    memoryPanel.add(lblMemoryUsage);

    lblMemory = new JLabel("xx% of xxgb");
    lblMemory.setFont(new Font("SansSerif", Font.PLAIN, 12));
    memoryPanel.add(lblMemory);
    lblMemory.setHorizontalAlignment(SwingConstants.CENTER);
    clMainPanel = new CardLayout(0, 0);
    mainPanel.setLayout(clMainPanel);

    mapPanel = new MapPanel(this);
    mainPanel.add(mapPanel, "MAP");
    mapPanel.setGridSize(Constants.GRID_SIZE);

    JPanel errorPanel = new JPanel();
    mainPanel.add(errorPanel, "ERRORS");
    errorPanel.setLayout(new GridLayout(0, 1, 0, 0));

    textAreaErrors = new JTextArea();
    errorPanel.add(new JScrollPane(textAreaErrors));
    textAreaErrors.setEditable(false);

    final JPanel panel25 = new JPanel();

    final JLabel lblNewLabel_4 = new JLabel("Map Coords:");
    lblNewLabel_4.setHorizontalAlignment(SwingConstants.CENTER);

    lblMapCoords = new JLabel("");
    lblMapCoords.setHorizontalAlignment(SwingConstants.LEFT);

    final JPanel panel28 = new JPanel();
    panel28.setLayout(new GridLayout(0, 3, 0, 0));

    chcekboxShowGrid = new JCheckBox("Grid");
    panel28.add(chcekboxShowGrid);
    chcekboxShowGrid.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        mapPanel.showGrid(chcekboxShowGrid.isSelected());
      }
    });
    chcekboxShowGrid.setHorizontalAlignment(SwingConstants.CENTER);

    JLabel lblSize = new JLabel("Size:");
    lblSize.setToolTipText("Grid cell count. Press enter to submit");
    lblSize.setHorizontalAlignment(SwingConstants.RIGHT);
    panel28.add(lblSize);

    textFieldMapGridSize = new JTextField("" + Constants.GRID_SIZE);
    textFieldMapGridSize.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        try {
          mapPanel.setGridSize(Integer.parseInt(textFieldMapGridSize.getText()));
        } catch (NumberFormatException ex) {
          JOptionPane.showMessageDialog(null, "Map size must be an integer", "Input Error",
              JOptionPane.WARNING_MESSAGE);
        }
      }
    });
    panel28.add(textFieldMapGridSize);
    textFieldMapGridSize.setColumns(10);
    final GroupLayout gl_mapCoordsPanel = new GroupLayout(mapCoordsPanel);
    gl_mapCoordsPanel
        .setHorizontalGroup(
            gl_mapCoordsPanel.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING,
                gl_mapCoordsPanel.createSequentialGroup()
                    .addComponent(panel25, GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.UNRELATED).addComponent(panel28,
                        GroupLayout.PREFERRED_SIZE, 179, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()));
    gl_mapCoordsPanel.setVerticalGroup(gl_mapCoordsPanel.createParallelGroup(Alignment.LEADING)
        .addComponent(panel25, GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
        .addComponent(panel28, GroupLayout.PREFERRED_SIZE, 24, Short.MAX_VALUE));
    final GroupLayout gl_panel_25 = new GroupLayout(panel25);
    gl_panel_25.setHorizontalGroup(gl_panel_25.createParallelGroup(Alignment.LEADING)
        .addGroup(gl_panel_25.createSequentialGroup()
            .addComponent(lblNewLabel_4, GroupLayout.PREFERRED_SIZE, 113,
                GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(lblMapCoords, GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
            .addContainerGap()));
    gl_panel_25.setVerticalGroup(gl_panel_25.createParallelGroup(Alignment.LEADING)
        .addComponent(lblNewLabel_4, GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE).addComponent(
            lblMapCoords, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE));
    panel25.setLayout(gl_panel_25);
    mapCoordsPanel.setLayout(gl_mapCoordsPanel);

    btnViewMap = new JButton("View Map");
    viewPanel.add(btnViewMap);

    btnViewTopo = new JButton("View Topo");
    viewPanel.add(btnViewTopo);

    btnViewBiomes = new JButton("View Biomes");
    viewPanel.add(btnViewBiomes);

    btnViewCave = new JButton("View Cave");
    viewPanel.add(btnViewCave);

    btnViewHeight = new JButton("View Heightmap");
    viewPanel.add(btnViewHeight);

    btnViewErrors = new JButton("View Errors");
    btnViewErrors.setVisible(false);
    btnViewErrors.setBackground(new Color(255, 51, 51));
    btnViewErrors.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        clMainPanel.show(mainPanel, "ERRORS");
      }
    });
    viewPanel.add(btnViewErrors);

    JPanel heightmapPanel = new JPanel();
    optionsPane.addTab("Heightmap", null, heightmapPanel, null);
    optionsPane.setEnabledAt(0, true);

    final JPanel panel_6 = new JPanel();

    JPanel panel = new JPanel();
    panel_6.add(panel);
    panel.setLayout(new GridLayout(0, 2, 0, 0));

    JPanel labelPanel = new JPanel();
    panel.add(labelPanel);
    labelPanel.setLayout(new GridLayout(0, 1, 0, 2));

    JLabel lblMapSize = new JLabel("Map Size");
    labelPanel.add(lblMapSize);

    JLabel lblMapSeed = new JLabel("Map Seed");
    lblMapSeed.setToolTipText("Determines the randomness of the map");
    labelPanel.add(lblMapSeed);

    JLabel lblResolution = new JLabel("Resolution");
    lblResolution.setToolTipText("Less = islands, More = one landmass");
    labelPanel.add(lblResolution);

    JLabel lblItrerations = new JLabel("Iterations");
    labelPanel.add(lblItrerations);

    JLabel lblMinEdge = new JLabel("Min Edge");
    lblMinEdge.setToolTipText("Size of water border around map");
    labelPanel.add(lblMinEdge);

    JLabel lblBorderWeight = new JLabel("Border Weight");
    lblBorderWeight
        .setToolTipText("How spread out mountains are. Less = more centralized mountains");
    labelPanel.add(lblBorderWeight);

    JLabel lblMaxHeight = new JLabel("Max Height");
    labelPanel.add(lblMaxHeight);

    JLabel lblNormalizeRatio = new JLabel("Normalize Ratio");
    labelPanel.add(lblNormalizeRatio);

    JLabel label = new JLabel("");
    labelPanel.add(label);

    JLabel lblNewLabel = new JLabel("");
    labelPanel.add(lblNewLabel);

    JPanel inputPanel = new JPanel();
    panel.add(inputPanel);
    inputPanel.setLayout(new GridLayout(0, 1, 0, 2));

    comboBoxMapSize = new JComboBox<Integer>();
    inputPanel.add(comboBoxMapSize);
    comboBoxMapSize.setModel(
        new DefaultComboBoxModel<Integer>(new Integer[] { 1024, 2048, 4096, 8192, 16384 }));
    comboBoxMapSize.setSelectedIndex(1);

    textFieldMapSeed = new JTextField("" + System.currentTimeMillis());
    textFieldMapSeed.setEnabled(false);
    inputPanel.add(textFieldMapSeed);
    textFieldMapSeed.setColumns(10);

    textFieldMapResolution = new JTextField("" + Constants.RESOLUTION);
    inputPanel.add(textFieldMapResolution);
    textFieldMapResolution.setColumns(10);

    textFieldMapIterations = new JTextField("" + Constants.HEIGHTMAP_ITERATIONS);
    inputPanel.add(textFieldMapIterations);
    textFieldMapIterations.setColumns(10);

    textFieldMapMinEdge = new JTextField("" + Constants.MIN_EDGE);
    inputPanel.add(textFieldMapMinEdge);
    textFieldMapMinEdge.setColumns(10);

    textFieldMapBorderWeight = new JTextField("" + Constants.BORDER_WEIGHT);
    inputPanel.add(textFieldMapBorderWeight);
    textFieldMapBorderWeight.setColumns(10);

    textFieldMapMaxHeight = new JTextField("" + Constants.MAP_HEIGHT);
    inputPanel.add(textFieldMapMaxHeight);
    textFieldMapMaxHeight.setColumns(10);

    textFieldNormalizeRatio = new JTextField("" + Constants.NORMALIZE_RATIO);
    inputPanel.add(textFieldNormalizeRatio);
    textFieldNormalizeRatio.setColumns(10);

    checkboxMoreLand = new JCheckBox("More Land", Constants.MORE_LAND);
    inputPanel.add(checkboxMoreLand);

    checkboxMapRandomSeed = new JCheckBox("Random Seed", true);
    inputPanel.add(checkboxMapRandomSeed);
    checkboxMapRandomSeed.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        textFieldMapSeed.setEnabled(!checkboxMapRandomSeed.isSelected());
      }
    });

    final JPanel panel_7 = new JPanel();

    btnGenerateHeightmap = new JButton("Generate Heightmap");
    panel_7.add(btnGenerateHeightmap);
    final GroupLayout gl_heightmapPanel = new GroupLayout(heightmapPanel);
    gl_heightmapPanel.setHorizontalGroup(
        gl_heightmapPanel.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING,
            gl_heightmapPanel.createSequentialGroup().addContainerGap()
                .addGroup(gl_heightmapPanel.createParallelGroup(Alignment.TRAILING)
                    .addComponent(panel_6, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 303,
                        Short.MAX_VALUE)
                    .addComponent(panel_7, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 303,
                        Short.MAX_VALUE))
                .addContainerGap()));
    gl_heightmapPanel.setVerticalGroup(
        gl_heightmapPanel.createParallelGroup(Alignment.TRAILING).addGroup(Alignment.LEADING,
            gl_heightmapPanel.createSequentialGroup().addContainerGap()
                .addComponent(panel_7, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                    GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(panel_6, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                .addContainerGap()));

    btnLoadHeightmap = new JButton("Import Heightmap");
    btnLoadHeightmap.setToolTipText("16 bit grayscale PNG");
    panel_7.add(btnLoadHeightmap);
    heightmapPanel.setLayout(gl_heightmapPanel);

    JPanel erosionPanel = new JPanel();
    optionsPane.addTab("Erosion", null, erosionPanel, null);
    optionsPane.setEnabledAt(1, true);

    final JPanel panel_1 = new JPanel();

    final JPanel panel_2 = new JPanel();
    panel_1.add(panel_2);
    panel_2.setLayout(new GridLayout(0, 2, 0, 0));

    final JPanel panel_3 = new JPanel();
    panel_2.add(panel_3);
    panel_3.setLayout(new GridLayout(0, 1, 0, 2));

    final JLabel lblIterations = new JLabel("Iterations");
    lblIterations.setToolTipText("How many times to pass over the map");
    panel_3.add(lblIterations);

    final JLabel lblMinSlope = new JLabel("Min Slope");
    lblMinSlope.setToolTipText("Only erode above this slope");
    panel_3.add(lblMinSlope);

    final JLabel lblMaxSlope_1 = new JLabel("Max Slope");
    lblMaxSlope_1.setToolTipText("Only erode below this slope");
    panel_3.add(lblMaxSlope_1);

    JLabel lblSedimentPer = new JLabel("Sediment per");
    lblSedimentPer.setToolTipText("How much dirt is dropped on each iteration");
    panel_3.add(lblSedimentPer);

    final JPanel panel_4 = new JPanel();
    panel_2.add(panel_4);
    panel_4.setLayout(new GridLayout(0, 1, 0, 2));

    textFieldErodeIterations = new JTextField("" + Constants.EROSION_ITERATIONS);
    textFieldErodeIterations.setColumns(10);
    panel_4.add(textFieldErodeIterations);

    textFieldErodeMinSlope = new JTextField("" + Constants.EROSION_MIN_SLOPE);
    textFieldErodeMinSlope.setColumns(10);
    panel_4.add(textFieldErodeMinSlope);

    textFieldErodeMaxSlope = new JTextField("" + Constants.EROSION_MAX_SLOPE);
    panel_4.add(textFieldErodeMaxSlope);
    textFieldErodeMaxSlope.setColumns(10);

    textFieldErodeSediment = new JTextField("" + Constants.EROSION_MAX_SEDIMENT);
    textFieldErodeSediment.setColumns(10);
    panel_4.add(textFieldErodeSediment);

    final JPanel panel_5 = new JPanel();

    btnErodeHeightmap = new JButton("Erode Heightmap");
    panel_5.add(btnErodeHeightmap);
    final GroupLayout gl_erosionPanel = new GroupLayout(erosionPanel);
    gl_erosionPanel
        .setHorizontalGroup(gl_erosionPanel.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, gl_erosionPanel.createSequentialGroup().addContainerGap()
                .addGroup(gl_erosionPanel.createParallelGroup(Alignment.TRAILING)
                    .addComponent(panel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 303,
                        Short.MAX_VALUE)
                    .addComponent(panel_5, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 303,
                        Short.MAX_VALUE))
                .addContainerGap()));
    gl_erosionPanel.setVerticalGroup(
        gl_erosionPanel.createParallelGroup(Alignment.TRAILING).addGroup(Alignment.LEADING,
            gl_erosionPanel.createSequentialGroup().addContainerGap()
                .addComponent(panel_5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                    GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                .addContainerGap()));
    erosionPanel.setLayout(gl_erosionPanel);

    JPanel dropDirtPanel = new JPanel();
    optionsPane.addTab("Dirt / Water", null, dropDirtPanel, null);
    optionsPane.setEnabledAt(2, true);

    final JPanel panel_9 = new JPanel();

    final JPanel panel_10 = new JPanel();
    panel_9.add(panel_10);
    panel_10.setLayout(new GridLayout(0, 2, 0, 0));

    final JPanel panel_11 = new JPanel();
    panel_10.add(panel_11);
    panel_11.setLayout(new GridLayout(0, 1, 0, 2));

    final JLabel lblBiomeSeed = new JLabel("Biome Seed");
    panel_11.add(lblBiomeSeed);

    final JLabel lblDirtPerTile = new JLabel("Dirt Per Tile");
    lblDirtPerTile.setToolTipText("How much dirt to drop per tile on the map");
    panel_11.add(lblDirtPerTile);

    final JLabel lblNewLabel_1 = new JLabel("Max Dirt Slope");
    panel_11.add(lblNewLabel_1);

    final JLabel lblMaxDirtSlope = new JLabel("Max Diagonal Slope");
    panel_11.add(lblMaxDirtSlope);

    final JLabel lblMaxDirtHeight = new JLabel("Max Dirt Height");
    lblMaxDirtHeight.setToolTipText("Dirt is not dropped above this height");
    panel_11.add(lblMaxDirtHeight);

    final JLabel lblWaterHeight = new JLabel("Water Height");
    lblWaterHeight.setToolTipText("Sea level");
    panel_11.add(lblWaterHeight);

    final JLabel lblCliffRatio = new JLabel("Cliff Ratio");
    lblCliffRatio
        .setToolTipText("How much cliffs protrude. Less = buried cliffs, More = lots of cliffs");
    panel_11.add(lblCliffRatio);

    final JLabel label_8 = new JLabel("");
    panel_11.add(label_8);

    final JLabel label_4 = new JLabel("");
    panel_11.add(label_4);

    final JSeparator separator = new JSeparator();
    panel_11.add(separator);

    checkboxPaintRivers = new JCheckBox("Paint Rivers");
    checkboxPaintRivers.setToolTipText("Click and drag on map to draw rivers");
    checkboxPaintRivers.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        mapPanel.setRiverPaintingMode(checkboxPaintRivers.isSelected());
      }
    });
    panel_11.add(checkboxPaintRivers);

    checkboxAutoDropDirt = new JCheckBox("Auto Drop Dirt");
    checkboxAutoDropDirt.setToolTipText("Drop dirt after generating rivers");
    checkboxAutoDropDirt.setSelected(true);
    panel_11.add(checkboxAutoDropDirt);

    final JLabel label_7 = new JLabel("");
    panel_11.add(label_7);

    final JLabel lblRiverDepth = new JLabel("River depth");
    lblRiverDepth.setToolTipText("Deepest part of the river");
    panel_11.add(lblRiverDepth);

    final JLabel lblRiverWidth = new JLabel("River width");
    lblRiverWidth.setToolTipText("Base size at the deepest part");
    panel_11.add(lblRiverWidth);

    final JLabel lblRiverSlope = new JLabel("River slope");
    lblRiverSlope.setToolTipText("Lower = gradual, Higher = steep edges");
    panel_11.add(lblRiverSlope);

    final JPanel panel_12 = new JPanel();
    panel_10.add(panel_12);
    panel_12.setLayout(new GridLayout(0, 1, 0, 2));

    textFieldBiomeSeed = new JTextField("" + System.currentTimeMillis());
    textFieldBiomeSeed.setEnabled(false);
    textFieldBiomeSeed.setColumns(10);
    panel_12.add(textFieldBiomeSeed);

    textFieldDirtPerTile = new JTextField("" + Constants.DIRT_DROP_COUNT);
    textFieldDirtPerTile.setColumns(10);
    panel_12.add(textFieldDirtPerTile);

    textFieldMaxDirtSlope = new JTextField("" + Constants.MAX_DIRT_SLOPE);
    panel_12.add(textFieldMaxDirtSlope);
    textFieldMaxDirtSlope.setColumns(10);

    textFieldMaxDiagSlope = new JTextField("" + Constants.MAX_DIRT_DIAG_SLOPE);
    textFieldMaxDiagSlope.setColumns(10);
    panel_12.add(textFieldMaxDiagSlope);

    textFieldMaxDirtHeight = new JTextField("" + Constants.ROCK_WEIGHT);
    textFieldMaxDirtHeight.setColumns(10);
    panel_12.add(textFieldMaxDirtHeight);

    textFieldWaterHeight = new JTextField("" + Constants.WATER_HEIGHT);
    textFieldWaterHeight.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        lblWater.setText("Water: " + textFieldWaterHeight.getText());
      }
    });
    textFieldWaterHeight.setColumns(10);
    panel_12.add(textFieldWaterHeight);

    textFieldCliffRatio = new JTextField("" + Constants.CLIFF_RATIO);
    panel_12.add(textFieldCliffRatio);
    textFieldCliffRatio.setColumns(10);

    checkBoxLandSlide = new JCheckBox("Land Slide");
    checkBoxLandSlide.setToolTipText("Pushes dirt down that is above max slope");
    checkBoxLandSlide.setSelected(false);
    panel_12.add(checkBoxLandSlide);

    checkboxBiomeRandomSeed = new JCheckBox("Random Seed");
    checkboxBiomeRandomSeed.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        textFieldBiomeSeed.setEnabled(!checkboxBiomeRandomSeed.isSelected());
      }
    });
    panel_12.add(checkboxBiomeRandomSeed);
    checkboxBiomeRandomSeed.setSelected(true);

    final JSeparator separator_1 = new JSeparator();
    panel_12.add(separator_1);

    btnGenerateRivers = new JButton("Generate Rivers");
    btnGenerateRivers.setToolTipText("Alters the heightmap");
    panel_12.add(btnGenerateRivers);

    btnResetRivers = new JButton("Reset Painting");
    btnResetRivers.setToolTipText("Clear the currently drawn rivers");
    btnResetRivers.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        mapPanel.clearRiverSeeds();
      }
    });

    btnUndoRiver = new JButton("Undo River");
    panel_12.add(btnUndoRiver);
    panel_12.add(btnResetRivers);

    textFieldRiverDepth = new JTextField("" + Constants.RIVER_DEPTH);
    panel_12.add(textFieldRiverDepth);
    textFieldRiverDepth.setColumns(10);

    textFieldRiverWidth = new JTextField("" + Constants.RIVER_WIDTH);
    panel_12.add(textFieldRiverWidth);
    textFieldRiverWidth.setColumns(10);

    textFieldRiverSlope = new JTextField("" + Constants.RIVER_SLOPE);
    panel_12.add(textFieldRiverSlope);
    textFieldRiverSlope.setColumns(10);

    final JPanel panel_13 = new JPanel();

    btnDropDirt = new JButton("Drop Dirt");
    panel_13.add(btnDropDirt);
    final GroupLayout gl_dropDirtPanel = new GroupLayout(dropDirtPanel);
    gl_dropDirtPanel.setHorizontalGroup(gl_dropDirtPanel.createParallelGroup(Alignment.LEADING)
        .addGroup(gl_dropDirtPanel.createSequentialGroup().addContainerGap()
            .addGroup(gl_dropDirtPanel.createParallelGroup(Alignment.LEADING)
                .addComponent(panel_9, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 303,
                    Short.MAX_VALUE)
                .addComponent(panel_13, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 303,
                    Short.MAX_VALUE))
            .addContainerGap()));
    gl_dropDirtPanel.setVerticalGroup(
        gl_dropDirtPanel.createParallelGroup(Alignment.TRAILING).addGroup(Alignment.LEADING,
            gl_dropDirtPanel.createSequentialGroup().addContainerGap()
                .addComponent(panel_13, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                    GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(panel_9, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                .addContainerGap()));

    btnUpdateWater = new JButton("Update Water");
    panel_13.add(btnUpdateWater);
    dropDirtPanel.setLayout(gl_dropDirtPanel);
    JPanel biomePanel = new JPanel();
    optionsPane.addTab("Biomes", null, biomePanel, null);

    final JPanel panel_14 = new JPanel();

    final JPanel panel_8 = new JPanel();
    panel_14.add(panel_8);
    panel_8.setLayout(new BorderLayout(0, 0));

    final JPanel panel_15 = new JPanel();
    panel_8.add(panel_15);
    panel_15.setLayout(new GridLayout(0, 2, 0, 0));

    final JPanel panel_16 = new JPanel();
    panel_15.add(panel_16);
    panel_16.setLayout(new GridLayout(0, 1, 0, 2));

    final JLabel lblSeedCount = new JLabel("Seed Count");
    lblSeedCount.setToolTipText("Amount of biomes to add to the map");
    panel_16.add(lblSeedCount);

    final JLabel label_5 = new JLabel("");
    panel_16.add(label_5);

    final JLabel lblBiomeSize = new JLabel("Biome Size");
    lblBiomeSize.setToolTipText("How big the biome should grow");
    panel_16.add(lblBiomeSize);

    final JLabel lblBiomeDensity = new JLabel("Biome Density");
    lblBiomeDensity.setToolTipText("Higher = more sparse biome");
    panel_16.add(lblBiomeDensity);

    final JLabel lblMaxSlope = new JLabel("Max Slope");
    lblMaxSlope.setToolTipText("Don't grow above this slope");
    panel_16.add(lblMaxSlope);

    final JLabel lblMinHeight = new JLabel("Min Height");
    lblMinHeight.setToolTipText("Negative offset if around water is set");
    panel_16.add(lblMinHeight);

    final JLabel lblMaxHeight_1 = new JLabel("Max Height");
    lblMaxHeight_1.setToolTipText("Positive offset if around water is checked");
    panel_16.add(lblMaxHeight_1);

    lblWater = new JLabel("Water: " + textFieldWaterHeight.getText());
    lblWater.setToolTipText("Current water height of the map");
    panel_16.add(lblWater);

    JLabel lblGrowth = new JLabel("Growth %");
    lblGrowth.setToolTipText("Chance for biome to grow in a particular direction");
    panel_16.add(lblGrowth);

    JLabel lblNorth = new JLabel(" - North / South");
    panel_16.add(lblNorth);

    JLabel lblEast = new JLabel(" - East / West");
    panel_16.add(lblEast);

    final JLabel label_3 = new JLabel("");
    panel_16.add(label_3);

    JLabel lblRandomMin = new JLabel("Growth Min");
    lblRandomMin.setToolTipText("Lower limit of random growth");
    panel_16.add(lblRandomMin);

    JLabel lblRandomMax = new JLabel("Growth Max");
    lblRandomMax.setToolTipText("Upper limit of random growth");
    panel_16.add(lblRandomMax);

    JLabel lblFlowerType = new JLabel("Flower Type");
    panel_16.add(lblFlowerType);

    JLabel lblFlowerPercent = new JLabel("Flower Percent");
    panel_16.add(lblFlowerPercent);

    final JPanel panel_17 = new JPanel();
    panel_15.add(panel_17);
    panel_17.setLayout(new GridLayout(0, 1, 0, 2));

    textFieldSeedCount = new JTextField("" + Constants.BIOME_SEEDS);
    textFieldSeedCount.setColumns(10);
    panel_17.add(textFieldSeedCount);

    checkboxPaintMode = new JCheckBox("Paint Mode");
    checkboxPaintMode.setToolTipText("Click on map to plant seed");
    checkboxPaintMode.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        mapPanel.setPaintingMode(checkboxPaintMode.isSelected());
        textFieldSeedCount.setEnabled(!checkboxPaintMode.isSelected());
      }
    });
    panel_17.add(checkboxPaintMode);

    textFieldBiomeSize = new JTextField("" + Constants.BIOME_SIZE);
    textFieldBiomeSize.setColumns(10);
    panel_17.add(textFieldBiomeSize);

    textFieldBiomeDensity = new JTextField("" + Constants.BIOME_DENSITY);
    panel_17.add(textFieldBiomeDensity);
    textFieldBiomeDensity.setColumns(10);

    textFieldBiomeMaxSlope = new JTextField("" + Constants.BIOME_MAX_SLOPE);
    textFieldBiomeMaxSlope.setColumns(10);
    panel_17.add(textFieldBiomeMaxSlope);

    textFieldBiomeMinHeight = new JTextField("" + Constants.BIOME_MIN_HEIGHT);
    panel_17.add(textFieldBiomeMinHeight);
    textFieldBiomeMinHeight.setColumns(10);

    textFieldBiomeMaxHeight = new JTextField("" + Constants.BIOME_MAX_HEIGHT);
    textFieldBiomeMaxHeight.setColumns(10);
    panel_17.add(textFieldBiomeMaxHeight);

    chckbxAroundWater = new JCheckBox("Around Water (-/+)", true);
    panel_17.add(chckbxAroundWater);

    final JLabel label_6 = new JLabel("");
    panel_17.add(label_6);

    final JPanel panel_29 = new JPanel();
    panel_17.add(panel_29);
    panel_29.setLayout(new GridLayout(0, 2, 0, 0));

    textFieldGrowthE = new JTextField("" + (int) (Constants.BIOME_RATE * 0.6));
    panel_29.add(textFieldGrowthE);
    textFieldGrowthE.setEnabled(false);
    textFieldGrowthE.setColumns(4);

    textFieldGrowthW = new JTextField("" + Constants.BIOME_RATE);
    panel_29.add(textFieldGrowthW);
    textFieldGrowthW.setEnabled(false);
    textFieldGrowthW.setColumns(4);

    final JPanel panel_30 = new JPanel();
    panel_17.add(panel_30);
    panel_30.setLayout(new GridLayout(0, 2, 0, 0));

    textFieldGrowthN = new JTextField("" + Constants.BIOME_RATE / 2);
    panel_30.add(textFieldGrowthN);
    textFieldGrowthN.setEnabled(false);
    textFieldGrowthN.setColumns(4);

    textFieldGrowthS = new JTextField("" + (int) (Constants.BIOME_RATE * 1.3));
    panel_30.add(textFieldGrowthS);
    textFieldGrowthS.setEnabled(false);
    textFieldGrowthS.setColumns(4);

    checkboxGrowthRandom = new JCheckBox("Randomize");
    checkboxGrowthRandom.setToolTipText("Randomly determine growth chance for each direction");
    checkboxGrowthRandom.setSelected(true);
    checkboxGrowthRandom.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (checkboxGrowthRandom.isSelected()) {
          textFieldGrowthN.setEnabled(false);
          textFieldGrowthS.setEnabled(false);
          textFieldGrowthE.setEnabled(false);
          textFieldGrowthW.setEnabled(false);
          textFieldGrowthMin.setEnabled(true);
          textFieldGrowthMax.setEnabled(true);
        } else {
          textFieldGrowthN.setEnabled(true);
          textFieldGrowthS.setEnabled(true);
          textFieldGrowthE.setEnabled(true);
          textFieldGrowthW.setEnabled(true);
          textFieldGrowthMin.setEnabled(false);
          textFieldGrowthMax.setEnabled(false);
        }
      }
    });
    panel_17.add(checkboxGrowthRandom);

    textFieldGrowthMin = new JTextField("" + Constants.BIOME_RANDOM_MIN);
    panel_17.add(textFieldGrowthMin);
    textFieldGrowthMin.setColumns(10);

    textFieldGrowthMax = new JTextField("" + Constants.BIOME_RANDOM_MAX);
    panel_17.add(textFieldGrowthMax);
    textFieldGrowthMax.setColumns(10);

    comboBoxFlowerType = new JComboBox(new String[] { "Random", "None", "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" });
    panel_17.add(comboBoxFlowerType);

    textFieldFlowerPercent = new JTextField("" + Constants.BIOME_FLOWER_PERCENT);
    panel_17.add(textFieldFlowerPercent);
    textFieldFlowerPercent.setColumns(10);

    final JPanel panel_27 = new JPanel();
    panel_8.add(panel_27, BorderLayout.NORTH);

    btnImportBiomes = new JButton("Import");
    panel_27.add(btnImportBiomes);

    // ArrayList<Tile> tiles = new ArrayList<Tile>();
    // for (Tile tile:Tile.getTiles()) {
    // if (tile == null) {
    // continue;
    // }
    // if (TileMap.getTileColor(tile) != null) {
    // tiles.add(tile);
    // }
    // }
    // comboBox_biomeType = new JComboBox(tiles.toArray());

    // TODO track new types
    comboBoxBiomeType = new JComboBox(new Tile[] { Tile.TILE_CLAY, Tile.TILE_DIRT,
        Tile.TILE_DIRT_PACKED, Tile.TILE_GRASS, Tile.TILE_GRAVEL, Tile.TILE_KELP, Tile.TILE_LAVA,
        Tile.TILE_MARSH, Tile.TILE_MOSS, Tile.TILE_MYCELIUM, Tile.TILE_PEAT, Tile.TILE_REED,
        Tile.TILE_SAND, Tile.TILE_STEPPE, Tile.TILE_TAR, Tile.TILE_TUNDRA, Tile.TILE_TREE_APPLE,
        Tile.TILE_TREE_BIRCH, Tile.TILE_TREE_CEDAR, Tile.TILE_TREE_CHERRY, Tile.TILE_TREE_CHESTNUT,
        Tile.TILE_TREE_FIR, Tile.TILE_TREE_LEMON, Tile.TILE_TREE_LINDEN, Tile.TILE_TREE_MAPLE,
        Tile.TILE_TREE_OAK, Tile.TILE_TREE_OLIVE, Tile.TILE_TREE_PINE, Tile.TILE_TREE_WALNUT,
        Tile.TILE_TREE_WILLOW, Tile.TILE_TREE_ORANGE, Tile.TILE_BUSH_CAMELLIA, Tile.TILE_BUSH_GRAPE,
        Tile.TILE_BUSH_LAVENDER, Tile.TILE_BUSH_OLEANDER, Tile.TILE_BUSH_ROSE, Tile.TILE_BUSH_THORN,
        Tile.TILE_BUSH_HAZELNUT, Tile.TILE_BUSH_RASPBERRYE, Tile.TILE_BUSH_BLUEBERRY,
        Tile.TILE_BUSH_LINGONBERRY, Tile.TILE_TREE, Tile.TILE_BUSH, Tile.TILE_SNOW });

    panel_27.add(comboBoxBiomeType);
    comboBoxBiomeType.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        textFieldSeedCount.setText(biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][0]);
        textFieldBiomeSize.setText(biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][1]);
        textFieldBiomeMaxSlope.setText(biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][2]);
        textFieldGrowthN.setText(biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][3]);
        textFieldGrowthS.setText(biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][4]);
        textFieldGrowthE.setText(biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][5]);
        textFieldGrowthW.setText(biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][6]);
        textFieldBiomeMinHeight
            .setText(biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][7]);
        textFieldBiomeMaxHeight
            .setText(biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][8]);

        checkboxGrowthRandom.setSelected(
            !Boolean.parseBoolean(biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][9]));
        checkboxGrowthRandom.doClick();

        textFieldGrowthMin.setText(biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][10]);
        textFieldGrowthMax.setText(biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][11]);

        chckbxAroundWater.setSelected(
            Boolean.parseBoolean(biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][12]));
        textFieldBiomeDensity.setText(biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][13]);

        if (comboBoxBiomeType.getSelectedItem() == Tile.TILE_GRASS) {
          comboBoxFlowerType.setEnabled(true);
          textFieldFlowerPercent.setEnabled(true);
        } else {
          comboBoxFlowerType.setEnabled(false);
          textFieldFlowerPercent.setEnabled(false);
        }
      }
    });
    final JPanel panel_18 = new JPanel();
    panel_18.setLayout(new GridLayout(0, 1, 0, 0));

    final JPanel panel_31 = new JPanel();
    panel_18.add(panel_31);

    btnAddBiome = new JButton("Add Biome");
    panel_31.add(btnAddBiome);

    btnUndoLastBiome = new JButton("Undo Last");
    panel_31.add(btnUndoLastBiome);
    btnUndoLastBiome.setToolTipText("Can only go back 1 action");

    btnResetBiomes = new JButton("Reset All");
    panel_31.add(btnResetBiomes);
    final GroupLayout gl_biomePanel = new GroupLayout(biomePanel);
    gl_biomePanel.setHorizontalGroup(gl_biomePanel.createParallelGroup(Alignment.LEADING)
        .addGroup(gl_biomePanel.createSequentialGroup().addContainerGap()
            .addGroup(gl_biomePanel.createParallelGroup(Alignment.LEADING)
                .addComponent(panel_14, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 303,
                    Short.MAX_VALUE)
                .addComponent(panel_18, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 303,
                    Short.MAX_VALUE))
            .addContainerGap()));
    gl_biomePanel.setVerticalGroup(
        gl_biomePanel.createParallelGroup(Alignment.TRAILING).addGroup(Alignment.LEADING,
            gl_biomePanel.createSequentialGroup().addContainerGap()
                .addComponent(panel_18, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                    GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(panel_14, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                .addContainerGap()));
    biomePanel.setLayout(gl_biomePanel);

    JPanel orePanel = new JPanel();
    optionsPane.addTab("Ores", null, orePanel, null);
    optionsPane.setEnabledAt(4, true);

    final JPanel panel_19 = new JPanel();

    final JPanel panel_20 = new JPanel();
    panel_19.add(panel_20);
    panel_20.setLayout(new GridLayout(0, 2, 0, 0));

    final JPanel panel_21 = new JPanel();
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

    JLabel lblSandstone = new JLabel("Sandstone");
    panel_21.add(lblSandstone);

    JLabel lblRocksalt = new JLabel("Rocksalt");
    panel_21.add(lblRocksalt);

    JLabel lblAddy = new JLabel("Adamantine");
    panel_21.add(lblAddy);

    JLabel lblGlimmer = new JLabel("Glimmer");
    panel_21.add(lblGlimmer);

    JLabel lblRock = new JLabel("Rock");
    lblRock.setToolTipText("Left over amount, adds up to 100%");
    panel_21.add(lblRock);

    final JPanel panel_22 = new JPanel();
    panel_20.add(panel_22);
    panel_22.setLayout(new GridLayout(0, 1, 0, 2));

    textFieldIron = new JTextField("" + Constants.ORE_IRON);
    textFieldIron.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setRockTotal();
      }
    });
    textFieldIron.setColumns(4);
    panel_22.add(textFieldIron);

    textFieldGold = new JTextField("" + Constants.ORE_GOLD);
    textFieldGold.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setRockTotal();
      }
    });
    textFieldGold.setColumns(4);
    panel_22.add(textFieldGold);

    textFieldSilver = new JTextField("" + Constants.ORE_SILVER);
    textFieldSilver.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setRockTotal();
      }
    });
    textFieldSilver.setColumns(4);
    panel_22.add(textFieldSilver);

    textFieldZinc = new JTextField("" + Constants.ORE_ZINC);
    textFieldZinc.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setRockTotal();
      }
    });
    textFieldZinc.setColumns(4);
    panel_22.add(textFieldZinc);

    textFieldCopper = new JTextField("" + Constants.ORE_COPPER);
    textFieldCopper.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setRockTotal();
      }
    });
    textFieldCopper.setColumns(4);
    panel_22.add(textFieldCopper);

    textFieldLead = new JTextField("" + Constants.ORE_LEAD);
    textFieldLead.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setRockTotal();
      }
    });
    panel_22.add(textFieldLead);
    textFieldLead.setColumns(4);

    textFieldTin = new JTextField("" + Constants.ORE_TIN);
    textFieldTin.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setRockTotal();
      }
    });
    textFieldTin.setColumns(4);
    panel_22.add(textFieldTin);

    textFieldMarble = new JTextField("" + Constants.ORE_MARBLE);
    textFieldMarble.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setRockTotal();
      }
    });
    textFieldMarble.setColumns(4);
    panel_22.add(textFieldMarble);

    textFieldSlate = new JTextField("" + Constants.ORE_SLATE);
    textFieldSlate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setRockTotal();
      }
    });
    textFieldSlate.setColumns(4);
    panel_22.add(textFieldSlate);

    textFieldSandstone = new JTextField("" + Constants.ORE_SANDSTONE);
    textFieldSandstone.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setRockTotal();
      }
    });
    textFieldSandstone.setColumns(4);
    panel_22.add(textFieldSandstone);

    textFieldRocksalt = new JTextField("" + Constants.ORE_ROCKSALT);
    textFieldRocksalt.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setRockTotal();
      }
    });
    textFieldRocksalt.setColumns(4);
    panel_22.add(textFieldRocksalt);

    textFieldAddy = new JTextField("" + Constants.ORE_ADDY);
    textFieldAddy.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setRockTotal();
      }
    });
    textFieldAddy.setColumns(4);
    panel_22.add(textFieldAddy);

    textFieldGlimmer = new JTextField("" + Constants.ORE_GLIMMER);
    textFieldGlimmer.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setRockTotal();
      }
    });
    textFieldGlimmer.setColumns(4);
    panel_22.add(textFieldGlimmer);

    textFieldRock = new JTextField("");
    textFieldRock.setEditable(false);
    textFieldRock.setColumns(4);
    panel_22.add(textFieldRock);

    final JPanel panel_23 = new JPanel();

    btnGenerateOres = new JButton("Generate Ores");
    panel_23.add(btnGenerateOres);
    final GroupLayout gl_orePanel = new GroupLayout(orePanel);
    gl_orePanel
        .setHorizontalGroup(gl_orePanel.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, gl_orePanel.createSequentialGroup().addContainerGap()
                .addGroup(gl_orePanel.createParallelGroup(Alignment.TRAILING)
                    .addComponent(panel_19, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 303,
                        Short.MAX_VALUE)
                    .addComponent(panel_23, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 303,
                        Short.MAX_VALUE))
                .addContainerGap()));
    gl_orePanel.setVerticalGroup(
        gl_orePanel.createParallelGroup(Alignment.TRAILING).addGroup(Alignment.LEADING,
            gl_orePanel.createSequentialGroup().addContainerGap()
                .addComponent(panel_23, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                    GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(panel_19, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                .addContainerGap()));
    orePanel.setLayout(gl_orePanel);

    JPanel actionPanel = new JPanel();
    optionsPane.addTab("Actions / Export", null, actionPanel, null);
    optionsPane.setEnabledAt(5, true);

    final JPanel panel_24 = new JPanel();

    final JPanel panel_26 = new JPanel();
    panel_24.add(panel_26);
    panel_26.setLayout(new GridLayout(0, 1, 0, 2));

    final JLabel lblNewLabel_2 = new JLabel("Map Name");
    lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
    panel_26.add(lblNewLabel_2);

    textFieldMapName = new JTextField(textFieldMapSeed.getText());
    mapName = textFieldMapSeed.getText();
    panel_26.add(textFieldMapName);
    textFieldMapName.setColumns(10);

    btnUpdateMapName = new JButton("Update Map Name");
    panel_26.add(btnUpdateMapName);

    final JLabel label_1 = new JLabel("");
    panel_26.add(label_1);

    btnSaveImageDumps = new JButton("Save Images");
    panel_26.add(btnSaveImageDumps);

    btnSaveMapFiles = new JButton("Save Map Files");
    panel_26.add(btnSaveMapFiles);

    btnSaveActions = new JButton("Save Actions");

    final JLabel label_2 = new JLabel("");
    panel_26.add(label_2);
    panel_26.add(btnSaveActions);

    btnLoadActions = new JButton("Load Actions");
    btnLoadActions.setEnabled(true);
    panel_26.add(btnLoadActions);

    JButton btnClearActions = new JButton("Clear Actions");
    btnClearActions.setToolTipText("Reset action history");
    btnClearActions.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        genHistory.clear();
      }
    });
    panel_26.add(btnClearActions);

    btnExportBiomes = new JButton("Export Biome Values");

    JButton btnSaveGlobalBiomes = new JButton("Save Global Values");
    btnSaveGlobalBiomes.setToolTipText("Values auto loaded at startup");
    btnSaveGlobalBiomes.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        actionSaveGlobalBiomeValues();
      }
    });

    final JLabel label_9 = new JLabel("");
    panel_26.add(label_9);

    final JSeparator separator_2 = new JSeparator();
    panel_26.add(separator_2);
    panel_26.add(btnSaveGlobalBiomes);
    btnExportBiomes.setToolTipText("Save biome input values to config file");
    panel_26.add(btnExportBiomes);

    btnLoadBiomes = new JButton("Load Biome Values");
    panel_26.add(btnLoadBiomes);
    final GroupLayout gl_actionPanel = new GroupLayout(actionPanel);
    gl_actionPanel.setHorizontalGroup(gl_actionPanel.createParallelGroup(Alignment.LEADING)
        .addGroup(gl_actionPanel.createSequentialGroup().addContainerGap()
            .addComponent(panel_24, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
            .addContainerGap()));
    gl_actionPanel.setVerticalGroup(
        gl_actionPanel.createParallelGroup(Alignment.TRAILING).addGroup(Alignment.LEADING,
            gl_actionPanel.createSequentialGroup().addContainerGap()
                .addComponent(panel_24, GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
                .addContainerGap()));
    actionPanel.setLayout(gl_actionPanel);
    contentPane.setLayout(gl_contentPane);

    init();
  }

  private void init() {
    setupButtonActions();
    setRockTotal();
    updateMapCoords(0, 0, false);
    progress = new ProgressHandler(progressBar, lblMemory);
    progress.update(100);
    StreamCapturer sc = new StreamCapturer(System.err, this);
    System.setErr(new PrintStream(sc, true, StandardCharsets.UTF_8));

    try {
      (new File(Constants.CONFIG_DIRECTORY)).mkdirs();
    } catch (SecurityException e) {
      /* No permission to write to directory. */
    }

    // Loads biome input values from config file
    String biomeValuesPath = Constants.CONFIG_DIRECTORY + "biome_values.txt";
    try (FileReader fr = new FileReader(biomeValuesPath, StandardCharsets.UTF_8);
         BufferedReader br = new BufferedReader(fr)) {
      String s;

      for (int bt = 0; bt < biomeOptionValue.length; bt++) {
        s = br.readLine();
        if (s != null) {
          String[] parts = s.split(",");
          for (int bv = 0; bv < 14; bv++) {
            biomeOptionValue[bt][bv] = parts[bv];
          }
        }
      }
    } catch (FileNotFoundException e) {
      /* File not found */
    } catch (IOException e) {
      /* IOException */
    }
    comboBoxBiomeType.setSelectedIndex(12);
  }

  private void setupButtonActions() {

    btnUpdateMapName.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (heightMap == null) {
          JOptionPane.showMessageDialog(null, "Heightmap does not exist - Generate one first",
              "Error Saving Map Api", JOptionPane.ERROR_MESSAGE);
          return;
        }
        if (tileMap == null) {
          JOptionPane.showMessageDialog(null, "Tile map does not exist - Generate one first",
              "Error Saving Map Api", JOptionPane.ERROR_MESSAGE);
          return;
        }
        new Thread() {
          @Override
          public void run() {
            mapName = textFieldMapName.getText();
            if (mapName.equals("")) {
              textFieldMapName.setText("empty");
              mapName = "empty";
            }
            if (!apiClosed) {
              getApi().close();
            }
            apiClosed = true;
            updateApiMap();
          }
        }.start();
      }
    });
    btnViewMap.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        clMainPanel.show(mainPanel, "MAP");
        if (!actionReady()) {
          return;
        }
        new Thread() {
          @Override
          public void run() {
            actionViewMap();
          }
        }.start();
      }
    });
    btnViewTopo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        clMainPanel.show(mainPanel, "MAP");
        if (!actionReady()) {
          return;
        }
        new Thread() {
          @Override
          public void run() {
            actionViewTopo();
          }
        }.start();
      }
    });
    btnViewBiomes.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        clMainPanel.show(mainPanel, "MAP");
        if (!actionReady()) {
          return;
        }
        new Thread() {
          @Override
          public void run() {
            actionViewBiomes();
          }
        }.start();
      }
    });
    btnViewCave.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        clMainPanel.show(mainPanel, "MAP");
        if (!actionReady()) {
          return;
        }
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
        clMainPanel.show(mainPanel, "MAP");
        if (!actionReady()) {
          return;
        }
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
        if (!actionReady()) {
          return;
        }
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
        if (!actionReady()) {
          return;
        }
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
        if (!actionReady()) {
          return;
        }
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
        if (!actionReady()) {
          return;
        }
        new Thread() {
          @Override
          public void run() {
            actionUpdateWater();
          }
        }.start();
      }
    });
    btnGenerateRivers.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (!actionReady()) {
          return;
        }
        new Thread() {
          @Override
          public void run() {
            actionGenerateRivers();
          }
        }.start();
      }
    });
    btnUndoRiver.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (!actionReady()) {
          return;
        }
        new Thread() {
          @Override
          public void run() {
            actionUndoRiver();
          }
        }.start();
      }
    });
    btnAddBiome.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (!actionReady()) {
          return;
        }
        new Thread() {
          @Override
          public void run() {
            actionSeedBiome(null);
          }
        }.start();
      }
    });
    btnUndoLastBiome.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (!actionReady()) {
          return;
        }
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
        if (!actionReady()) {
          return;
        }
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
        if (!actionReady()) {
          return;
        }
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
        if (!actionReady()) {
          return;
        }
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
        if (!actionReady()) {
          return;
        }
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
        if (!actionReady()) {
          return;
        }
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
        if (!actionReady()) {
          return;
        }
        new Thread() {
          @Override
          public void run() {
            actionLoadActions();
          }
        }.start();
      }
    });
    btnLoadHeightmap.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (!actionReady()) {
          return;
        }
        new Thread() {
          @Override
          public void run() {
            actionLoadHeightmap();
          }
        }.start();
      }
    });
    btnImportBiomes.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (!actionReady()) {
          return;
        }
        new Thread() {
          @Override
          public void run() {
            actionLoadBiomes();
          }
        }.start();
      }
    });
    btnExportBiomes.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (!actionReady()) {
          return;
        }
        new Thread() {
          @Override
          public void run() {
            actionSaveBiomeValues();
          }
        }.start();
      }
    });
    btnLoadBiomes.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (!actionReady()) {
          return;
        }
        new Thread() {
          @Override
          public void run() {
            actionLoadBiomeValues();
          }
        }.start();
      }
    });

  }

  private void startLoading(String task) {
    progress.update(0, task);
  }

  private void stopLoading() {
    progress.update(100, "");
  }

  boolean actionReady() {
    return progressBar.getValue() == 100;
  }

  void actionGenerateHeightmap() {

    startLoading("Generating Height Map ()");
    try {
      api = null;
      genHistory = new ArrayList<String>();

      if (checkboxMapRandomSeed.isSelected()) {
        textFieldMapSeed.setText("" + System.currentTimeMillis());
      }

      mapPanel.setMapSize((int) comboBoxMapSize.getSelectedItem());

      heightMap = new HeightMap(textFieldMapSeed.getText().hashCode(),
          (int) comboBoxMapSize.getSelectedItem(),
          Double.parseDouble(textFieldMapResolution.getText()),
          Integer.parseInt(textFieldMapIterations.getText()),
          Integer.parseInt(textFieldMapMinEdge.getText()),
          Integer.parseInt(textFieldMapBorderWeight.getText()),
          Integer.parseInt(textFieldMapMaxHeight.getText()),
          Integer.parseInt(textFieldNormalizeRatio.getText()), checkboxMoreLand.isSelected());

      heightMap.generateHeights(progress);

      defaultView = Constants.ViewType.HEIGHT;
      updateMapView();

      genHistory.add("HEIGHTMAP:" + textFieldMapSeed.getText() + ","
          + comboBoxMapSize.getSelectedIndex() + "," + textFieldMapResolution.getText() + ","
          + textFieldMapIterations.getText() + "," + textFieldMapMinEdge.getText() + ","
          + textFieldMapBorderWeight.getText() + "," + textFieldMapMaxHeight.getText() + ","
          + textFieldNormalizeRatio.getText() + "," + checkboxMoreLand.isSelected());
    } catch (NumberFormatException nfe) {
      JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(),
          "Error Generating HeightMap", JOptionPane.ERROR_MESSAGE);
    } catch (InterruptedException ie) {
      JOptionPane.showMessageDialog(null,
          "Multithreading interrupted " + ie.getMessage().toLowerCase(),
          "Error Generating HeightMap", JOptionPane.ERROR_MESSAGE);
    } finally {
      stopLoading();
    }
  }

  void actionErodeHeightmap() {
    if (heightMap == null) {
      JOptionPane.showMessageDialog(null, "HeightMap does not exist", "Error Eroding HeightMap",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    startLoading("Eroding Height Map ()");
    try {
      heightMap.erode(Integer.parseInt(textFieldErodeIterations.getText()),
          Integer.parseInt(textFieldErodeMinSlope.getText()),
          Integer.parseInt(textFieldErodeMaxSlope.getText()),
          Integer.parseInt(textFieldErodeSediment.getText()), progress);

      updateMapView();

      genHistory.add("ERODE:" + textFieldErodeIterations.getText() + ","
          + textFieldErodeMinSlope.getText() + "," + textFieldErodeSediment.getText());
    } catch (NumberFormatException nfe) {
      JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(),
          "Error Eroding HeightMap", JOptionPane.ERROR_MESSAGE);
    } finally {
      stopLoading();
    }
  }

  void actionDropDirt() {
    if (heightMap == null) {
      JOptionPane.showMessageDialog(null, "HeightMap does not exist", "Error Dropping Dirt",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    startLoading("Dropping Dirt ()");
    try {
      if (checkboxBiomeRandomSeed.isSelected()) {
        textFieldBiomeSeed.setText("" + System.currentTimeMillis());
      }
      lblWater.setText("Water: " + textFieldWaterHeight.getText());

      tileMap = new TileMap(heightMap);
      tileMap.setBiomeSeed(textFieldBiomeSeed.getText().hashCode());
      tileMap.setWaterHeight(Integer.parseInt(textFieldWaterHeight.getText()));
      tileMap.dropDirt(Integer.parseInt(textFieldDirtPerTile.getText()),
          Integer.parseInt(textFieldMaxDirtSlope.getText()),
          Integer.parseInt(textFieldMaxDiagSlope.getText()),
          Integer.parseInt(textFieldMaxDirtHeight.getText()),
          Double.parseDouble(textFieldCliffRatio.getText()), checkBoxLandSlide.isSelected(),
          progress);

      if (defaultView == Constants.ViewType.HEIGHT) {
        defaultView = Constants.ViewType.ISO;
      }
      updateMapView();

      genHistory
          .add("DROPDIRT:" + textFieldBiomeSeed.getText() + "," + textFieldWaterHeight.getText()
              + "," + textFieldDirtPerTile.getText() + "," + textFieldMaxDirtSlope.getText() + ","
              + textFieldMaxDiagSlope.getText() + "," + textFieldMaxDirtHeight.getText() + ","
              + Double.parseDouble(textFieldCliffRatio.getText()) + ","
              + checkBoxLandSlide.isSelected());
    } catch (NumberFormatException nfe) {
      JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(),
          "Error Dropping Dirt", JOptionPane.ERROR_MESSAGE);
    } finally {
      stopLoading();
    }
  }

  void actionUpdateWater() {
    if (heightMap == null) {
      JOptionPane.showMessageDialog(null, "HeightMap does not exist", "Error Updating Water",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (tileMap == null) {
      JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first",
          "Error Updating Water", JOptionPane.ERROR_MESSAGE);
      return;
    }

    startLoading("Updating Water");
    try {
      lblWater.setText("Water: " + textFieldWaterHeight.getText());
      tileMap.setWaterHeight(Integer.parseInt(textFieldWaterHeight.getText()));

      updateMapView();

      genHistory.add("UPDATEWATER:" + textFieldWaterHeight.getText());
    } catch (NumberFormatException nfe) {
      JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(),
          "Error Updating Water", JOptionPane.ERROR_MESSAGE);
    } finally {
      stopLoading();
    }
  }

  void actionGenerateRivers() {
    if (tileMap == null) {
      JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first",
          "Error Generating River", JOptionPane.ERROR_MESSAGE);
      return;
    }

    startLoading("Generating Rivers");
    try {
      heightMap.exportHeightImage(mapName, "river_heightmap.png");
      double water = (Integer.parseInt(textFieldWaterHeight.getText())
          - Integer.parseInt(textFieldDirtPerTile.getText())
          - Integer.parseInt(textFieldRiverDepth.getText()))
          / (double) Integer.parseInt(textFieldMapMaxHeight.getText());
      for (Point p : mapPanel.getRiverSeeds()) {
        heightMap.createPond(p.x, p.y, water, Integer.parseInt(textFieldRiverWidth.getText()),
            Integer.parseInt(textFieldRiverSlope.getText()));
      }

      mapPanel.setRiverPaintingMode(false);
      checkboxPaintRivers.setSelected(false);
      mapPanel.clearRiverSeeds();

      if (checkboxAutoDropDirt.isSelected()) {
        actionDropDirt();
      }

    } catch (NumberFormatException nfe) {
      JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(),
          "Error Generating River", JOptionPane.ERROR_MESSAGE);
    } finally {
      stopLoading();
    }
  }

  void actionUndoRiver() {
    if (tileMap == null) {
      JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first",
          "Error Undoing River", JOptionPane.ERROR_MESSAGE);
      return;
    }

    startLoading("Undoing River");
    try {
      Constants.ViewType oldView = defaultView;
      File heightImageFile = new File("./maps/" + mapName + "/river_heightmap.png");
      defaultView = Constants.ViewType.HEIGHT;
      actionLoadHeightmap(heightImageFile);

      if (checkboxAutoDropDirt.isSelected()) {
        defaultView = oldView;
        actionDropDirt();
      }

    } catch (NumberFormatException nfe) {
      JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(),
          "Error Undoing River", JOptionPane.ERROR_MESSAGE);
    } finally {
      stopLoading();
    }
  }

  void actionSeedBiome(Point origin) {
    if (tileMap == null) {
      JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first",
          "Error Adding Biome", JOptionPane.ERROR_MESSAGE);
      return;
    }

    startLoading("Seeding Biome");
    try {
      // Save the edited fields back into the biome input table
      biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][0] = textFieldSeedCount.getText();
      biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][1] = textFieldBiomeSize.getText();
      biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][2] = textFieldBiomeMaxSlope
          .getText();
      biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][3] = textFieldGrowthN.getText();
      biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][4] = textFieldGrowthS.getText();
      biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][5] = textFieldGrowthE.getText();
      biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][6] = textFieldGrowthW.getText();
      biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][7] = textFieldBiomeMinHeight
          .getText();
      biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][8] = textFieldBiomeMaxHeight
          .getText();
      biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][9] = Boolean
          .toString(checkboxGrowthRandom.isSelected());
      biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][10] = textFieldGrowthMin.getText();
      biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][11] = textFieldGrowthMax.getText();
      biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][12] = Boolean
          .toString(chckbxAroundWater.isSelected());
      biomeOptionValue[comboBoxBiomeType.getSelectedIndex()][13] = textFieldBiomeDensity
          .getText();

      int[] rates = new int[4];
      if (checkboxGrowthRandom.isSelected()) {
        int min = Integer.parseInt(textFieldGrowthMin.getText());
        int max = Integer.parseInt(textFieldGrowthMax.getText());
        if (min >= max) {
          min = max - 1;
        }
        rates[0] = min;
        rates[1] = max;
        rates[2] = 0;
        rates[3] = 0;
      } else {
        rates[0] = Integer.parseInt(textFieldGrowthN.getText());
        rates[1] = Integer.parseInt(textFieldGrowthS.getText());
        rates[2] = Integer.parseInt(textFieldGrowthE.getText());
        rates[3] = Integer.parseInt(textFieldGrowthW.getText());
      }

      if (Integer.parseInt(textFieldBiomeDensity.getText()) < 1) {
        textFieldBiomeDensity.setText("1");
      }
      if (Integer.parseInt(textFieldBiomeMinHeight.getText()) < 0) {
        textFieldBiomeMinHeight.setText("0");
      }
      if (Integer.parseInt(textFieldBiomeMaxHeight.getText()) > Integer
          .parseInt(textFieldMaxDirtHeight.getText())) {
        textFieldBiomeMaxHeight.setText(textFieldMaxDirtHeight.getText());
      }

      int minHeight = chckbxAroundWater.isSelected()
          ? Integer.parseInt(textFieldWaterHeight.getText())
              - Integer.parseInt(textFieldBiomeMinHeight.getText())
          : Integer.parseInt(textFieldBiomeMinHeight.getText());
      int maxHeight = chckbxAroundWater.isSelected()
          ? Integer.parseInt(textFieldWaterHeight.getText())
              + Integer.parseInt(textFieldBiomeMaxHeight.getText())
          : Integer.parseInt(textFieldBiomeMaxHeight.getText());

      if (origin == null) {
        tileMap.plantBiome(Integer.parseInt(textFieldSeedCount.getText()),
            Integer.parseInt(textFieldBiomeSize.getText()),
            Integer.parseInt(textFieldBiomeDensity.getText()), rates,
            checkboxGrowthRandom.isSelected(), Integer.parseInt(textFieldBiomeMaxSlope.getText()),
            minHeight, maxHeight, (Tile) comboBoxBiomeType.getSelectedItem(),
            comboBoxFlowerType.getSelectedIndex(),
            Integer.parseInt(textFieldFlowerPercent.getText()), progress);

        genHistory.add("SEEDBIOME(" + comboBoxBiomeType.getSelectedItem() + "):"
            + comboBoxBiomeType.getSelectedIndex() + "," + textFieldSeedCount.getText() + ","
            + textFieldBiomeSize.getText() + "," + textFieldBiomeDensity.getText() + ","
            + textFieldBiomeMaxSlope.getText() + "," + textFieldGrowthN.getText() + ","
            + textFieldGrowthS.getText() + "," + textFieldGrowthE.getText() + ","
            + textFieldGrowthW.getText() + "," + checkboxGrowthRandom.isSelected() + ","
            + textFieldGrowthMin.getText() + "," + textFieldGrowthMax.getText() + ","
            + textFieldBiomeMinHeight.getText() + "," + textFieldBiomeMaxHeight.getText() + ","
            + chckbxAroundWater.isSelected() + "," + comboBoxFlowerType.getSelectedIndex() + ","
            + textFieldFlowerPercent.getText());
      } else {
        tileMap.plantBiomeAt(origin.x, origin.y, Integer.parseInt(textFieldBiomeSize.getText()),
            Integer.parseInt(textFieldBiomeDensity.getText()), rates,
            checkboxGrowthRandom.isSelected(), Integer.parseInt(textFieldBiomeMaxSlope.getText()),
            minHeight, maxHeight, (Tile) comboBoxBiomeType.getSelectedItem(),
            comboBoxFlowerType.getSelectedIndex(),
            Integer.parseInt(textFieldFlowerPercent.getText()), progress);

        genHistory.add("PAINTBIOME(" + comboBoxBiomeType.getSelectedItem() + "):"
            + comboBoxBiomeType.getSelectedIndex() + "," + origin.x + "," + origin.y + ","
            + textFieldBiomeSize.getText() + "," + textFieldBiomeDensity.getText() + ","
            + textFieldBiomeMaxSlope.getText() + "," + textFieldGrowthN.getText() + ","
            + textFieldGrowthS.getText() + "," + textFieldGrowthE.getText() + ","
            + textFieldGrowthW.getText() + "," + checkboxGrowthRandom.isSelected() + ","
            + textFieldGrowthMin.getText() + "," + textFieldGrowthMax.getText() + ","
            + textFieldBiomeMinHeight.getText() + "," + textFieldBiomeMaxHeight.getText() + ","
            + chckbxAroundWater.isSelected() + "," + comboBoxFlowerType.getSelectedIndex() + ","
            + textFieldFlowerPercent.getText());
      }

      updateMapView();

    } catch (NumberFormatException nfe) {
      JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(),
          "Error Dropping Dirt", JOptionPane.ERROR_MESSAGE);
    } finally {
      stopLoading();
    }
  }

  void actionUndoBiome() {
    if (tileMap == null) {
      JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first",
          "Error Resetting Biomes", JOptionPane.ERROR_MESSAGE);
      return;
    }

    startLoading("Undoing Biome");
    try {

      tileMap.undoLastBiome();

      updateMapView();

      genHistory.add("UNDOBIOME:null");
    } finally {
      stopLoading();
    }
  }

  void actionResetBiomes() {
    if (tileMap == null) {
      JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first",
          "Error Resetting Biomes", JOptionPane.ERROR_MESSAGE);
      return;
    }

    startLoading("Resetting Biomes");
    try {

      for (int i = 0; i < heightMap.getMapSize(); i++) {
        for (int j = 0; j < heightMap.getMapSize(); j++) {
          progress.update((int) ((float) (i * heightMap.getMapSize() + j)
              / (heightMap.getMapSize() * heightMap.getMapSize()) * 100f));
          tileMap.addDirt(i, j, 0);
        }
      }

      updateMapView();

      genHistory.add("RESETBIOMES:null");
    } finally {
      stopLoading();
    }
  }

  void actionGenerateOres() {
    if (tileMap == null) {
      JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first",
          "Error Resetting Biomes", JOptionPane.ERROR_MESSAGE);
      return;
    }

    startLoading("Generating Ores");
    try {
      setRockTotal();
      if (Double.parseDouble(textFieldRock.getText()) < 0.0
          || Double.parseDouble(textFieldRock.getText()) > 100.0) {
        JOptionPane.showMessageDialog(null, "Ore values out of range", "Error Generating Ore",
            JOptionPane.ERROR_MESSAGE);
        return;
      }

      double[] rates = { Double.parseDouble(textFieldRock.getText()),
          Double.parseDouble(textFieldIron.getText()),
          Double.parseDouble(textFieldGold.getText()),
          Double.parseDouble(textFieldSilver.getText()),
          Double.parseDouble(textFieldZinc.getText()),
          Double.parseDouble(textFieldCopper.getText()),
          Double.parseDouble(textFieldLead.getText()), Double.parseDouble(textFieldTin.getText()),
          Double.parseDouble(textFieldAddy.getText()),
          Double.parseDouble(textFieldGlimmer.getText()),
          Double.parseDouble(textFieldMarble.getText()),
          Double.parseDouble(textFieldSlate.getText()),
          Double.parseDouble(textFieldSandstone.getText()),
          Double.parseDouble(textFieldRocksalt.getText()) };

      tileMap.generateOres(rates, progress);

      defaultView = Constants.ViewType.CAVE;
      updateMapView();

      genHistory.add("GENORES:" + textFieldRock.getText() + "," + textFieldIron.getText() + ","
          + textFieldGold.getText() + "," + textFieldSilver.getText() + ","
          + textFieldZinc.getText() + "," + textFieldCopper.getText() + ","
          + textFieldLead.getText() + "," + textFieldTin.getText() + ","
          + textFieldAddy.getText() + "," + textFieldGlimmer.getText() + ","
          + textFieldMarble.getText() + "," + textFieldSlate.getText() + ","
          + textFieldSandstone.getText() + "," + textFieldRocksalt.getText());
    } catch (NumberFormatException nfe) {
      JOptionPane.showMessageDialog(null, "Error parsing number " + nfe.getMessage().toLowerCase(),
          "Error Generating Ores", JOptionPane.ERROR_MESSAGE);
    } finally {
      stopLoading();
    }
  }

  void actionViewMap() {
    if (tileMap == null) {
      JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first",
          "Error Showing Map", JOptionPane.ERROR_MESSAGE);
      return;
    }

    startLoading("Loading");
    try {
      defaultView = Constants.ViewType.ISO;
      updateMapView();
    } finally {
      stopLoading();
    }
  }

  void actionViewTopo() {
    if (tileMap == null) {
      JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first",
          "Error Showing Map", JOptionPane.ERROR_MESSAGE);
      return;
    }

    startLoading("Loading");
    try {
      defaultView = Constants.ViewType.TOPO;
      updateMapView();
    } finally {
      stopLoading();
    }
  }

  void actionViewBiomes() {
    if (tileMap == null) {
      JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first",
          "Error Showing Map", JOptionPane.ERROR_MESSAGE);
      return;
    }

    startLoading("Loading");
    try {
      defaultView = Constants.ViewType.BIOMES;
      updateMapView();
    } finally {
      stopLoading();
    }
  }

  void actionViewCave() {
    if (tileMap == null) {
      JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first",
          "Error Showing Map", JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (!tileMap.hasOres()) {
      JOptionPane.showMessageDialog(null, "No Cave Map - Generate Ores first", "Error Showing Map",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    startLoading("Loading");
    try {
      defaultView = Constants.ViewType.CAVE;
      updateMapView();
    } finally {
      stopLoading();
    }
  }

  void actionViewHeightmap() {
    if (heightMap == null) {
      JOptionPane.showMessageDialog(null, "HeightMap does not exist", "Error Showing Map",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    startLoading("Loading");
    try {
      defaultView = Constants.ViewType.HEIGHT;
      updateMapView();
    } finally {
      stopLoading();
    }
  }

  void actionSaveImages() {
    if (tileMap == null) {
      JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first",
          "Error Saving Images", JOptionPane.ERROR_MESSAGE);
      return;
    }

    startLoading("Saving Images");
    try {
      updateApiMap();
      MapData map = getApi().getMapData();
      ImageIO.write(map.createMapDump(), "png", new File("./maps/" + mapName + "/map.png"));
      ImageIO.write(map.createTopographicDump(true, (short) 250), "png",
          new File("./maps/" + mapName + "/topography.png"));
      ImageIO.write(map.createCaveDump(true), "png", new File("./maps/" + mapName + "/cave.png"));

      heightMap.exportHeightImage(mapName, "heightmap.png");
      saveBiomesImage();

    } catch (IOException ex) {
      ex.printStackTrace();
    } finally {
      stopLoading();
    }
  }

  private void saveBiomesImage() {
    try {
      BufferedImage bufferedImage = getBiomeImage();

      File imageFile = new File("./maps/" + mapName + "/" + "biomes.png");
      if (!imageFile.exists()) {
        boolean created = imageFile.mkdirs();
        if (!created) {
          log.warn("Failed to create imageFile directory");
        }
      }
      ImageIO.write(bufferedImage, "png", imageFile);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  void actionSaveMap() {
    if (tileMap == null) {
      JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first",
          "Error Saving Map", JOptionPane.ERROR_MESSAGE);
      return;
    }

    startLoading("Saving Map");
    try {
      updateApiMap();
      getApi().getMapData().saveChanges();
    } finally {
      stopLoading();
    }
  }

  void actionSaveGlobalBiomeValues() {
    try {
      String biomeValuesPath = Constants.CONFIG_DIRECTORY + "biome_values.txt";
      FileWriter fw = new FileWriter(biomeValuesPath, StandardCharsets.UTF_8);
      for (int bt = 0; bt < biomeOptionValue.length; bt++) {
        for (int bv = 0; bv < biomeOptionValue[0].length; bv++) {
          fw.write(biomeOptionValue[bt][bv]);
          if (bv < 13) {
            fw.write(",");
          }
        }
        fw.write("\r\n");
      }
      fw.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  void actionSaveBiomeValues() {
    startLoading("Saving Biome Values");
    try {
      JFileChooser fc = new JFileChooser();
      fc.setCurrentDirectory(new File(Constants.CONFIG_DIRECTORY));
      fc.setSelectedFile(new File("biome_values.txt"));
      fc.setFileFilter(new TextFileView());
      fc.setAcceptAllFileFilterUsed(false);
      int returnVal = fc.showSaveDialog(this);
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }

      File biomeValueFile = fc.getSelectedFile();
      boolean created = biomeValueFile.createNewFile();
      if (!created) {
        log.warn("Failed to create biome value file");
      }

      String biotxt;
      try {
        FileWriter fw = new FileWriter(biomeValueFile, StandardCharsets.UTF_8);
        for (int bt = 0; bt < 36; bt++) {
          for (int bv = 0; bv < 14; bv++) {
            biotxt = biomeOptionValue[bt][bv];
            fw.write(biotxt);
            if (bv < 13) {
              fw.write(",");
            }
          }
          fw.write("\r\n");
        }
        fw.close();
      } catch (IOException ex) {
        System.err.println("Saving BiomeValues.txt failed: " + ex.toString());
      }
    } catch (IOException ex) {
      System.err.println("Saving Biome values failed: " + ex.toString());
    } finally {
      stopLoading();
    }
  }

  /** Load Biome Values. */
  public void actionLoadBiomeValues() {
    startLoading("Loading Biome Values");
    try {
      File biomeValueFile;

      JFileChooser fc = new JFileChooser();
      fc.setCurrentDirectory(new File(Constants.CONFIG_DIRECTORY));
      fc.setFileFilter(new TextFileView());
      fc.setAcceptAllFileFilterUsed(false);

      int returnVal = fc.showDialog(null, "Load Biome Values");

      if (returnVal == JFileChooser.APPROVE_OPTION) {
        biomeValueFile = fc.getSelectedFile();
        textFieldMapName.setText(biomeValueFile.getParentFile().getName());
        actionsFileDirectory = biomeValueFile.getParentFile().getAbsolutePath();

        try (FileReader fr = new FileReader(biomeValueFile, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(fr)) {
          String s;

          for (int bt = 0; bt < 36; bt++) {
            s = br.readLine();
            if (s != null) {
              String[] parts = s.split(",");
              for (int bv = 0; bv < 14; bv++) {
                biomeOptionValue[bt][bv] = parts[bv];
              }
            }
          }
        }
        comboBoxBiomeType.setSelectedIndex(12);
      }
    } catch (IOException ex) {
      System.err.println("Loading Biome Values failed: " + ex.toString());
    } finally {
      stopLoading();
    }
  }

  void actionSaveActions() {
    if (tileMap == null) {
      JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first",
          "Error Saving Map", JOptionPane.ERROR_MESSAGE);
      return;
    }

    startLoading("Saving Actions");
    try {
      JFileChooser fc = new JFileChooser();
      fc.setCurrentDirectory(new File("./maps/" + mapName));
      fc.setSelectedFile(new File("map_actions.act"));
      fc.setFileFilter(new ActionFileView());
      fc.setAcceptAllFileFilterUsed(false);
      int returnVal = fc.showSaveDialog(this);
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }

      File actionsFile = fc.getSelectedFile();
      if (!actionsFile.createNewFile()) {
        log.warn("Overwrote old actionsFile");
      }

      BufferedWriter bw = new BufferedWriter(new FileWriter(actionsFile, StandardCharsets.UTF_8));
      for (String s : genHistory) {
        bw.write(s + "\r\n");
      }

      bw.close();

      heightMap.exportHeightImage(mapName, "heightmap.png");
    } catch (IOException ex) {
      System.err.println("Saving actions failed: " + ex.toString());
    } finally {
      stopLoading();
    }
  }

  void actionLoadActions() {

    startLoading("Loading Actions");
    try {
      File actionsFile;

      JFileChooser fc = new JFileChooser();
      fc.addChoosableFileFilter(new ActionFileView());
      fc.setAcceptAllFileFilterUsed(false);
      fc.setCurrentDirectory(new File("./maps/"));

      int returnVal = fc.showDialog(null, "Load Actions");

      if (returnVal == JFileChooser.APPROVE_OPTION) {
        actionsFile = fc.getSelectedFile();
        textFieldMapName.setText(actionsFile.getParentFile().getName());
        actionsFileDirectory = actionsFile.getParentFile().getAbsolutePath();

        FileReader fr = new FileReader(actionsFile, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null) {
          parseAction(line);
        }

        br.close();
      }
    } catch (IOException ex) {
      System.err.println("Loading actions failed: " + ex.toString());
    } finally {
      stopLoading();
      // Reset interface
      checkboxGrowthRandom.doClick();
      checkboxGrowthRandom.doClick();
    }
  }

  void actionLoadHeightmap(File heightImageFile) {
    startLoading("Loading Heightmap");
    try {
      int mapSize = (int) comboBoxMapSize.getSelectedItem();
      api = null;
      genHistory = new ArrayList<String>();
      BufferedImage heightImage = new BufferedImage(mapSize, mapSize,
          BufferedImage.TYPE_USHORT_GRAY);
      heightImage = ImageIO.read(heightImageFile);
      mapPanel.setMapSize(mapSize);
      heightMap = new HeightMap(heightImage, mapSize,
          Integer.parseInt(textFieldMapMaxHeight.getText()));

      updateMapView();

      genHistory.add("IMPORTHEIGHTMAP:" + heightImageFile.getName() + ","
          + comboBoxMapSize.getSelectedIndex() + "," + textFieldMapMaxHeight.getText());

    } catch (NumberFormatException | IOException nfe) {
      JOptionPane.showMessageDialog(this, "Error loading file " + nfe.getMessage().toLowerCase(),
          "Error Loading Heightmap", JOptionPane.ERROR_MESSAGE);
    } finally {
      stopLoading();
    }
  }

  void actionLoadHeightmap() {
    JFileChooser fc = new JFileChooser();
    fc.addChoosableFileFilter(new ImageFileView());
    fc.setAcceptAllFileFilterUsed(false);
    fc.setCurrentDirectory(new File("./maps/"));

    int returnVal = fc.showDialog(this, "Load Heightmap");
    if (returnVal != JFileChooser.APPROVE_OPTION) {
      return;
    }
    File imageFile = fc.getSelectedFile();

    actionLoadHeightmap(imageFile);
  }

  void actionLoadBiomes() {
    if (tileMap == null) {
      JOptionPane.showMessageDialog(null, "TileMap does not exist - Add Dirt first",
          "Error Loading Biomes", JOptionPane.ERROR_MESSAGE);
      return;
    }
    startLoading("Loading Biomes");
    try {
      final int mapSize = (int) comboBoxMapSize.getSelectedItem();

      JFileChooser fc = new JFileChooser();
      fc.addChoosableFileFilter(new ImageFileView());
      fc.setAcceptAllFileFilterUsed(false);
      fc.setCurrentDirectory(new File("./maps/"));

      int returnVal = fc.showDialog(this, "Load Biomes");
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }
      File imageFile = fc.getSelectedFile();
      BufferedImage biomesImage = new BufferedImage(mapSize, mapSize, BufferedImage.TYPE_INT_RGB);
      biomesImage = ImageIO.read(imageFile);

      tileMap.importBiomeImage(biomesImage);

      updateMapView();

    } catch (NumberFormatException | IOException nfe) {
      JOptionPane.showMessageDialog(this, "Error loading file " + nfe.getMessage().toLowerCase(),
          "Error Loading Biomes", JOptionPane.ERROR_MESSAGE);
    } finally {
      stopLoading();
    }

  }

  private WurmAPI getApi() {
    if (apiClosed) {
      api = null;
    }

    if (api == null) {
      try {
        api = WurmAPI.create("./maps/" + mapName + "/",
            (int) (Math.log(heightMap.getMapSize()) / Math.log(2)));
        apiClosed = false;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return api;
  }

  private void updateMapView() {
    if (defaultView == Constants.ViewType.HEIGHT) {
      startLoading("Loading View");
      Graphics g = mapPanel.getMapImage().getGraphics();

      for (int i = 0; i < heightMap.getMapSize(); i++) {
        progress.update((int) ((float) i / heightMap.getMapSize() * 98f));
        for (int j = 0; j < heightMap.getMapSize(); j++) {
          g.setColor(new Color((float) heightMap.getHeight(i, j), (float) heightMap.getHeight(i, j),
              (float) heightMap.getHeight(i, j)));
          g.fillRect(i, j, 1, 1);
        }
      }
    } else {
      updateApiMap();

      if (defaultView == Constants.ViewType.TOPO) {
        mapPanel.setMapImage(getApi().getMapData().createTopographicDump(true, (short) 250));
      } else if (defaultView == Constants.ViewType.CAVE) {
        mapPanel.setMapImage(getApi().getMapData().createCaveDump(true));
      } else if (defaultView == Constants.ViewType.ISO) {
        mapPanel.setMapImage(getApi().getMapData().createMapDump());
      } else if (defaultView == Constants.ViewType.BIOMES) {
        mapPanel.setMapImage(getBiomeImage());
      }
    }

    mapPanel.updateScale();
    mapPanel.checkBounds();
    mapPanel.repaint();
    stopLoading();
  }

  private void updateApiMap() {
    startLoading("Updating Map");
    MapData map = getApi().getMapData();
    Random treeRand = new Random(System.currentTimeMillis());

    try {
      for (int i = 0; i < heightMap.getMapSize(); i++) {
        progress.update((int) ((float) i / heightMap.getMapSize() * 100f / 3));
        for (int j = 0; j < heightMap.getMapSize(); j++) {
          map.setSurfaceHeight(i, j, tileMap.getSurfaceHeight(i, j));
          map.setRockHeight(i, j, tileMap.getRockHeight(i, j));

          if (tileMap.hasOres()) {
            map.setCaveTile(i, j, tileMap.getOreType(i, j), tileMap.getOreCount(i, j));
          }
          map.setSurfaceTile(i, j, Tile.TILE_ROCK);
        }
      }
      for (int i = 0; i < heightMap.getMapSize(); i++) {
        progress.update((int) ((float) i / heightMap.getMapSize() * 100f / 3) + 33);
        for (int j = 0; j < heightMap.getMapSize(); j++) {
          if (tileMap.getType(i, j) != Tile.TILE_ROCK && !tileMap.getType(i, j).isTree()
              && !tileMap.getType(i, j).isBush()) {
            for (int x = i - 1; x <= i + 1; x++) {
              for (int y = j - 1; y <= j + 1; y++) {
                if (x > 0 && y > 0 && x < heightMap.getMapSize() && y < heightMap.getMapSize()) {
                  map.setSurfaceTile(x, y, tileMap.getType(i, j));
                  map.setGrass(x, y, GrowthStage.MEDIUM,
                      FlowerType.fromInt(tileMap.getFlowerType(x, y)));
                }
              }
            }
          }
        }
      }
      for (int i = 0; i < heightMap.getMapSize(); i++) {
        progress.update((int) ((float) i / heightMap.getMapSize() * 100f / 3) + 66);
        for (int j = 0; j < heightMap.getMapSize(); j++) {
          if (tileMap.getType(i, j).isTree()) {
            map.setTree(i, j, tileMap.getType(i, j).getTreeType((byte) 0),
                FoliageAge.values()[treeRand.nextInt(FoliageAge.values().length)],
                GrowthTreeStage.MEDIUM);
          } else if (tileMap.getType(i, j).isBush()) {
            map.setBush(i, j, tileMap.getType(i, j).getBushType((byte) 0),
                FoliageAge.values()[treeRand.nextInt(FoliageAge.values().length)],
                GrowthTreeStage.MEDIUM);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      stopLoading();
      System.gc();
    }
  }

  private void parseAction(String action) {
    String[] parts = action.split(":");
    if (parts.length < 2) {
      return;
    }

    String[] options = parts[1].split(",");
    switch (parts[0]) {
      case "HEIGHTMAP":
        if (options.length != 9) {
          JOptionPane.showMessageDialog(null, "Not enough options for HEIGHTMAP",
              "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
          return;
        }

        try {
          textFieldMapSeed.setText(options[0]);
          comboBoxMapSize.setSelectedIndex(Integer.parseInt(options[1]));
          textFieldMapResolution.setText(options[2]);
          textFieldMapIterations.setText(options[3]);
          textFieldMapMinEdge.setText(options[4]);
          textFieldMapBorderWeight.setText(options[5]);
          textFieldMapMaxHeight.setText(options[6]);
          textFieldNormalizeRatio.setText(options[7]);
          checkboxMoreLand.setSelected(Boolean.parseBoolean(options[8]));
          checkboxMapRandomSeed.setSelected(false);
          textFieldMapSeed.setEnabled(true);

          actionGenerateHeightmap();
        } catch (Exception nfe) {
          JOptionPane.showMessageDialog(null,
              "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Loading Actions",
              JOptionPane.ERROR_MESSAGE);
        }
        break;
      case "ERODE":
        if (options.length != 3) {
          JOptionPane.showMessageDialog(null, "Not enough options for ERODE",
              "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
          return;
        }

        textFieldErodeIterations.setText(options[0]);
        textFieldErodeMinSlope.setText(options[1]);
        textFieldErodeSediment.setText(options[2]);

        actionErodeHeightmap();
        break;
      case "DROPDIRT":
        if (options.length != 8) {
          JOptionPane.showMessageDialog(null, "Not enough options for DROPDIRT",
              "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
          return;
        }

        textFieldBiomeSeed.setText(options[0]);
        textFieldWaterHeight.setText(options[1]);
        textFieldDirtPerTile.setText(options[2]);
        textFieldMaxDirtSlope.setText(options[3]);
        textFieldMaxDiagSlope.setText(options[4]);
        textFieldMaxDirtHeight.setText(options[5]);
        textFieldCliffRatio.setText(options[6]);
        checkBoxLandSlide.setSelected(Boolean.parseBoolean(options[7]));
        checkboxBiomeRandomSeed.setSelected(false);
        textFieldBiomeSeed.setEnabled(true);

        actionDropDirt();
        break;
      case "UPDATEWATER":
        if (options.length != 1) {
          JOptionPane.showMessageDialog(null, "Not enough options for DROPDIRT",
              "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
          return;
        }

        textFieldWaterHeight.setText(options[0]);

        actionUpdateWater();
        break;
      case "UNDOBIOME":
        actionUndoBiome();
        break;
      case "RESETBIOMES":
        actionResetBiomes();
        break;
      case "GENORES":
        if (options.length != 14) {
          JOptionPane.showMessageDialog(null, "Not enough options for GENORES",
              "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
          return;
        }

        textFieldRock.setText(options[0]);
        textFieldIron.setText(options[1]);
        textFieldGold.setText(options[2]);
        textFieldSilver.setText(options[3]);
        textFieldZinc.setText(options[4]);
        textFieldCopper.setText(options[5]);
        textFieldLead.setText(options[6]);
        textFieldTin.setText(options[7]);
        textFieldAddy.setText(options[8]);
        textFieldGlimmer.setText(options[9]);
        textFieldMarble.setText(options[10]);
        textFieldSlate.setText(options[11]);
        textFieldSandstone.setText(options[12]);
        textFieldRocksalt.setText(options[13]);

        actionGenerateOres();
        break;
      case "IMPORTHEIGHTMAP":
        if (options.length != 3) {
          JOptionPane.showMessageDialog(this, "Not enough options for HEIGHTMAP",
              "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
          return;
        }

        try {
          final File heightImageFile = new File(actionsFileDirectory + "/" + options[0]);
          comboBoxMapSize.setSelectedIndex(Integer.parseInt(options[1]));
          textFieldMapMaxHeight.setText(options[2]);

          api = null;
          genHistory = new ArrayList<String>();

          actionLoadHeightmap(heightImageFile);

          genHistory.add("IMPORTHEIGHTMAP:" + heightImageFile.getName() + ","
              + comboBoxMapSize.getSelectedIndex() + "," + textFieldMapMaxHeight.getText());

        } catch (Exception nfe) {
          JOptionPane.showMessageDialog(this, "Error: " + nfe.getMessage().toLowerCase(),
              "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
        }
        break;

      default:
        if (parts[0].startsWith("SEEDBIOME")) {
          if (options.length != 17) {
            JOptionPane.showMessageDialog(null, "Not enough options for SEEDBIOME",
                "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
            return;
          }
          try {
            int i = 0;
            comboBoxBiomeType.setSelectedIndex(Integer.parseInt(options[i++]));
            textFieldSeedCount.setText(options[i++]);
            textFieldBiomeSize.setText(options[i++]);
            textFieldBiomeDensity.setText(options[i++]);
            textFieldBiomeMaxSlope.setText(options[i++]);
            textFieldGrowthN.setText(options[i++]);
            textFieldGrowthS.setText(options[i++]);
            textFieldGrowthE.setText(options[i++]);
            textFieldGrowthW.setText(options[i++]);
            checkboxGrowthRandom.setSelected(Boolean.parseBoolean(options[i++]));
            textFieldGrowthMin.setText(options[i++]);
            textFieldGrowthMax.setText(options[i++]);
            textFieldBiomeMinHeight.setText(options[i++]);
            textFieldBiomeMaxHeight.setText(options[i++]);
            chckbxAroundWater.setSelected(Boolean.parseBoolean(options[i++]));
            comboBoxFlowerType.setSelectedIndex(Integer.parseInt(options[i++]));
            textFieldFlowerPercent.setText(options[i++]);

            actionSeedBiome(null);
          } catch (Exception nfe) {
            JOptionPane.showMessageDialog(null,
                "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Loading Actions",
                JOptionPane.ERROR_MESSAGE);
          }
        } else if (parts[0].startsWith("PAINTBIOME")) {
          if (options.length != 18) {
            JOptionPane.showMessageDialog(null, "Not enough options for SEEDBIOME",
                "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
            return;
          }

          try {
            int i = 0;
            comboBoxBiomeType.setSelectedIndex(Integer.parseInt(options[i++]));
            final Point origin = new Point(Integer.parseInt(options[i++]),
                                     Integer.parseInt(options[i++]));
            textFieldBiomeSize.setText(options[i++]);
            textFieldBiomeDensity.setText(options[i++]);
            textFieldBiomeMaxSlope.setText(options[i++]);
            textFieldGrowthN.setText(options[i++]);
            textFieldGrowthS.setText(options[i++]);
            textFieldGrowthE.setText(options[i++]);
            textFieldGrowthW.setText(options[i++]);
            checkboxGrowthRandom.setSelected(Boolean.parseBoolean(options[i++]));
            textFieldGrowthMin.setText(options[i++]);
            textFieldGrowthMax.setText(options[i++]);
            textFieldBiomeMinHeight.setText(options[i++]);
            textFieldBiomeMaxHeight.setText(options[i++]);
            chckbxAroundWater.setSelected(Boolean.parseBoolean(options[i++]));
            comboBoxFlowerType.setSelectedIndex(Integer.parseInt(options[i++]));
            textFieldFlowerPercent.setText(options[i++]);

            actionSeedBiome(origin);
          } catch (Exception nfe) {
            JOptionPane.showMessageDialog(null,
                "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Loading Actions",
                JOptionPane.ERROR_MESSAGE);
          }
        } else {
          System.err.println("Error importing, Unknown action: " + options[0]);
        }
        break;
    }
  }

  private class ActionFileView extends FileFilter {

    public boolean accept(File f) {
      if (f.isDirectory()) {
        return true;
      }

      String extension = getExtension(f);
      if (extension != null) {
        return extension.equals("act");
      }

      return false;
    }

    private String getExtension(File f) {
      String ext = null;
      String s = f.getName();
      int i = s.lastIndexOf('.');

      if (i > 0 && i < s.length() - 1) {
        ext = s.substring(i + 1).toLowerCase();
      }
      return ext;
    }

    @Override
    public String getDescription() {
      return "Action Files (.act)";
    }
  }

  private class TextFileView extends FileFilter {

    public boolean accept(File f) {
      if (f.isDirectory()) {
        return true;
      }

      String extension = getExtension(f);
      if (extension != null) {
        return extension.equals("txt");
      }

      return false;
    }

    private String getExtension(File f) {
      String ext = null;
      String s = f.getName();
      int i = s.lastIndexOf('.');

      if (i > 0 && i < s.length() - 1) {
        ext = s.substring(i + 1).toLowerCase();
      }
      return ext;
    }

    @Override
    public String getDescription() {
      return "Biome Files (.txt)";
    }
  }

  private class ImageFileView extends FileFilter {

    public boolean accept(File f) {
      if (f.isDirectory()) {
        return true;
      }

      String extension = getExtension(f);
      if (extension != null) {
        return extension.equals("png");
      }

      return false;
    }

    private String getExtension(File f) {
      String ext = null;
      String s = f.getName();
      int i = s.lastIndexOf('.');

      if (i > 0 && i < s.length() - 1) {
        ext = s.substring(i + 1).toLowerCase();
      }
      return ext;
    }

    @Override
    public String getDescription() {
      return "Image File (.png)";
    }
  }

  private void setRockTotal() {
    try {
      double[] rates = { Double.parseDouble(textFieldIron.getText()),
          Double.parseDouble(textFieldGold.getText()),
          Double.parseDouble(textFieldSilver.getText()),
          Double.parseDouble(textFieldZinc.getText()),
          Double.parseDouble(textFieldCopper.getText()),
          Double.parseDouble(textFieldLead.getText()), Double.parseDouble(textFieldTin.getText()),
          Double.parseDouble(textFieldAddy.getText()),
          Double.parseDouble(textFieldGlimmer.getText()),
          Double.parseDouble(textFieldMarble.getText()),
          Double.parseDouble(textFieldSlate.getText()),
          Double.parseDouble(textFieldSandstone.getText()),
          Double.parseDouble(textFieldRocksalt.getText()) };

      float total = 0;
      for (int i = 0; i < rates.length; i++) {
        total += rates[i];
      }

      textFieldRock.setText("" + (100.0f - total));
    } catch (NumberFormatException nfe) {
      /* NumberFormatException */
    }
  }

  private BufferedImage getBiomeImage() {
    int mapSize = heightMap.getMapSize();
    BufferedImage bufferedImage = new BufferedImage(mapSize, mapSize, BufferedImage.TYPE_INT_RGB);
    WritableRaster wr = bufferedImage.getRaster();

    int[] array = new int[mapSize * mapSize * 3];
    for (int x = 0; x < mapSize; x++) {
      for (int y = 0; y < mapSize; y++) {
        final Tile tile = api.getMapData().getSurfaceTile(x, y);
        final Color color;
        if (tile != null) {
          if (tile == Tile.TILE_GRASS && tileMap.getFlowerType(x, y) != 0) {
            color = new Color(220, 250, tileMap.getFlowerType(x, y) + 50);
          } else {
            color = TileMap.getTileColor(tile);
          }
        } else {
          color = TileMap.getTileColor(Tile.TILE_DIRT);
        }
        array[(x + y * mapSize) * 3 + 0] = color.getRed();
        array[(x + y * mapSize) * 3 + 1] = color.getGreen();
        array[(x + y * mapSize) * 3 + 2] = color.getBlue();
      }
    }

    wr.setPixels(0, 0, mapSize, mapSize, array);

    bufferedImage.setData(wr);
    return bufferedImage;
  }

  /** Update Map Coords. */
  public void updateMapCoords(int x, int y, boolean show) {
    if (show && tileMap != null) {
      int height = tileMap.getMapHeight(x, mapPanel.getMapSize() - y);
      lblMapCoords.setText("Tile (" + x + "," + y + "), Player (" + (x * 4) + "," + (y * 4)
          + "), Height (" + height + ")");
    } else {
      lblMapCoords.setText("Right click to place a marker");
    }
  }

  public void submitError(String err) {
    textAreaErrors.append(err);
    btnViewErrors.setVisible(true);
  }
}
