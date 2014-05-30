package de.mephisto.radiofx.ui.controller;

import de.mephisto.radiofx.MephistoRadioFX;
import de.mephisto.radiofx.resources.ResourceLoader;
import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.services.IServiceStateListener;
import de.mephisto.radiofx.services.ServiceRegistry;
import de.mephisto.radiofx.services.google.Album;
import de.mephisto.radiofx.services.google.IGoogleMusicService;
import de.mephisto.radiofx.ui.Footer;
import de.mephisto.radiofx.ui.Pager;
import de.mephisto.radiofx.ui.UIStateController;
import de.mephisto.radiofx.util.*;
import javafx.animation.TranslateTransitionBuilder;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Controls the UI for the google music
 */
public class GoogleUINaviController extends PageableUIController implements IServiceStateListener {
  private static final Logger LOG = LoggerFactory.getLogger(GoogleUINaviController.class);

  public static final String STYLE_ACTIVE = "-fx-background-color:" + Colors.HEX_COLOR_INACTIVE + ";";
  public static final String STYLE_INACTIVE = "-fx-background-color: transparent;";

  public static final int COVER_SIZE = 180;
  private static final int SCROLL_DELAY = 200;
  private static final int SCROLL_WIDTH = 190;

  private Text artistText;
  private Text albumText;

  private HBox centerRegion;
  private Pager pager;

  private Node selectedAlbumNode;
  private HBox hBoxAlbums;
  private double scrollPos;
  private Album activeAlbum;
  private Text loadingText;

  public GoogleUINaviController() {
    super(ServiceRegistry.getGoogleService());
    ServiceRegistry.getGoogleService().addServiceStateListener(this);
  }

  @Override
  public void onDisplay() {
    IGoogleMusicService service = ServiceRegistry.getGoogleService();
    if(service.getAlbums().isEmpty()) {
      service.loadGoogleMusic(loadingText);
    }
  }

  @Override
  public void serviceLoaded() {
    IGoogleMusicService service = ServiceRegistry.getGoogleService();
    List<Album> albums = service.getAlbums();
    if(albums.isEmpty()) {
      return;
    }

    BorderPane tabRoot = (BorderPane) getTabRoot();


    final VBox vMain = new VBox(5);
    vMain.setAlignment(Pos.TOP_LEFT);

    HBox topBox = new HBox(5);
    topBox.setPadding(new Insets(5, 0, 0, 15));

    HBox selectionBox = new HBox();
    selectionBox.setMinWidth(PaneUtil.WIDTH-40);
    topBox.getChildren().add(selectionBox);

    //title text
    artistText = new Text(0, 0, "");
    artistText.setFont(Fonts.FONT_BOLD_20);
    albumText = new Text(0, 0, "");
    albumText.setFont(Fonts.FONT_NORMAL_20);
    selectionBox.getChildren().add(artistText);
    selectionBox.getChildren().add(albumText);

    //add the top navi to the root
    vMain.getChildren().add(topBox);
    tabRoot.setCenter(vMain);

    centerRegion = new HBox();
    pager = new Pager(tabRoot, new ArrayList<IServiceModel>(ServiceRegistry.getGoogleService().getAlbums()), false, true);
    this.activeAlbum = (Album) pager.getActiveModel();

    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        display();
        vMain.getChildren().add(centerRegion);
      }
    });

    super.setPager(pager);

    if (!albums.isEmpty()) {
      updatePage(albums.get(0));
    }
  }

  @Override
  public BorderPane init() {
    BorderPane tabRoot = new BorderPane();
    tabRoot.setMinHeight(PaneUtil.MIN_MAIN_HEIGHT);

    loadingText = new Text(0, 0, "Loading Google Music...");
    loadingText.setFont(Fonts.FONT_BOLD_20);
    tabRoot.setCenter(loadingText);

    TransitionUtil.createBlink(loadingText).play();

    return tabRoot;
  }

  /**
   * Creates the horizontal scrollable album navi.
   */
  private void display() {
    IGoogleMusicService service = ServiceRegistry.getGoogleService();
    List<Album> albums = service.getAlbums();

    hBoxAlbums = new HBox(4);
    centerRegion.getChildren().add(hBoxAlbums);
    centerRegion.setPadding(new Insets(0, 255, 0, 255));

    for (Album album : albums) {
      HBox albumRoot = new HBox(0);
      albumRoot.setId(String.valueOf(album.getMID()));

      VBox vbox = new VBox(2);
      vbox.setMaxWidth(COVER_SIZE);
      vbox.setPadding(new Insets(3, 3, 3, 3));

      if (!StringUtils.isEmpty(album.getArtUrl())) {
        Canvas cover = ImageCache.createLazyLoadingImageCanvas(album.getCoverId(), album.getArtUrl(), COVER_SIZE, COVER_SIZE);
        vbox.getChildren().add(cover);
      }
      else {
        String url = ResourceLoader.getResource("cover.png");
        Canvas cover = TransitionUtil.createImageCanvas(url, COVER_SIZE, COVER_SIZE);
        vbox.getChildren().add(cover);
      }

      Text text = new Text(0, 0, formatLabel(album.getArtist(), 20));
      text.setFont(Fonts.FONT_BOLD_14);
      text.setFill(Colors.COLOR_DARK_HEADER);
      vbox.getChildren().add(text);

      text = new Text(0, 0, formatLabel(album.getName(), 20));
      text.setFont(Fonts.FONT_NORMAL_14);
      text.setFill(Colors.COLOR_DARK_HEADER);
      vbox.getChildren().add(text);

      albumRoot.getChildren().add(vbox);
      hBoxAlbums.getChildren().add(albumRoot);
    }
    LOG.info("Finished loading Google Music UI, loaded " + albums.size() + " albums.");
  }

  @Override
  public void updatePage(IServiceModel model) {
    Album album = (Album) model;
    albumText.setText(album.getName());
    artistText.setText(album.getArtist() + ": ");

    if (selectedAlbumNode != null) {
      selectedAlbumNode.setStyle(STYLE_INACTIVE);
    }

    if(hBoxAlbums != null) { //null because of lazy loading
      final ObservableList<Node> children = hBoxAlbums.getChildren();
      for (Node node : children) {
        String id = node.getId();
        if (id.equals(String.valueOf(album.getMID()))) {
          selectedAlbumNode = node;
          selectedAlbumNode.setStyle(STYLE_ACTIVE);
          break;
        }
      }
    }
  }

  @Override
  public int getFooterId() {
    return Footer.FOOTER_MUSIC;
  }

  @Override
  public IRotaryControllable next() {
    double scrollTo = 0;
    if (!pager.isAtEnd()) {
      scrollTo = scrollPos -= SCROLL_WIDTH;
    }

    TranslateTransitionBuilder.create()
        .duration(Duration.millis(SCROLL_DELAY))
        .node(centerRegion)
        .fromX(scrollPos)
        .toX(scrollTo)
        .autoReverse(false)
        .build().play();
    scrollPos = scrollTo;

    super.next();
    activeAlbum = (Album) pager.getActiveModel();
    return this;
  }

  @Override
  public IRotaryControllable prev() {
    double scrollTo = -((getPager().size()-1) * SCROLL_WIDTH);
    if (!pager.isAtStart()) {
      scrollTo = scrollPos += SCROLL_WIDTH;
    }

    TranslateTransitionBuilder.create()
        .duration(Duration.millis(SCROLL_DELAY))
        .node(centerRegion)
        .fromX(scrollPos)
        .toX(scrollTo)
        .autoReverse(false)
        .build().play();
    scrollPos = scrollTo;
    super.prev();
    activeAlbum = (Album) pager.getActiveModel();
    return this;
  }

  @Override
  public IRotaryControllable push() {
    return UIStateController.getInstance().getGooglePlayerController();
  }

  @Override
  public IRotaryControllable longPush() {
    return UIStateController.getInstance().getRadioController();
  }

  /**
   * Returns the container of the active selection.
   *
   * @return
   */
  public Node getSelectedAlbumNode() {
    return selectedAlbumNode;
  }

  /**
   * Helper that ensures that the text of the album does not extend the
   * box width.
   *
   * @param label
   * @param length
   * @return
   */
  private String formatLabel(String label, int length) {
    if (label.length() > length) {
      label = label.substring(0, length - 1) + "..";
    }
    return label;
  }

  public Album getActiveAlbum() {
    return activeAlbum;
  }

  public HBox getCenterRegion() {
    return centerRegion;
  }

  public double getScrollPos() {
    return scrollPos;
  }
}
