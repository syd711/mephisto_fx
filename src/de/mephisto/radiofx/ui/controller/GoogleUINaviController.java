package de.mephisto.radiofx.ui.controller;

import de.mephisto.radiofx.resources.ResourceLoader;
import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.services.ServiceRegistry;
import de.mephisto.radiofx.services.google.Album;
import de.mephisto.radiofx.services.google.IGoogleMusicService;
import de.mephisto.radiofx.ui.Pager;
import de.mephisto.radiofx.ui.UIStateController;
import de.mephisto.radiofx.util.UIUtil;
import javafx.animation.TranslateTransitionBuilder;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Controls the UI for the google music
 */
public class GoogleUINaviController extends PageableUIController {
  public static final String STYLE_ACTIVE = "-fx-background-color:" + UIUtil.HEX_COLOR_INACTIVE + ";";
  public static final String STYLE_INACTIVE = "-fx-background-color: transparent;";

  public static final int COVER_SIZE = 100;
  private static final int SCROLL_DELAY = 200;
  private static final int SCROLL_WIDTH = 110;

  private Text artistText;
  private Text albumText;

  private HBox centerRegion;
  private Pager pager;

  private Node selectedAlbumNode;
  private HBox hBoxAlbums;
  private double scrollPos;
  private Album activeAlbum;

  public GoogleUINaviController() {
    super(ServiceRegistry.getGoogleService());
  }

  @Override
  public BorderPane init() {
    BorderPane tabRoot = new BorderPane();
    tabRoot.setMinHeight(UIUtil.MIN_MAIN_HEIGHT);
    IGoogleMusicService service = ServiceRegistry.getGoogleService();
    List<Album> albums = service.getAlbums();

    VBox vMain = new VBox(5);
    vMain.setAlignment(Pos.TOP_LEFT);

    HBox topBox = new HBox(5);
    topBox.setPadding(new Insets(5, 0, 0, 15));

    HBox selectionBox = new HBox();
    selectionBox.setMinWidth(420);
    topBox.getChildren().add(selectionBox);

    //title text
    artistText = new Text(0, 0, "");
    artistText.setFont(UIUtil.FONT_BOLD_14);
    artistText.setFill(UIUtil.COLOR_DARK_HEADER);
    albumText = new Text(0, 0, "");
    albumText.setFont(UIUtil.FONT_NORMAL_14);
    albumText.setFill(UIUtil.COLOR_DARK_HEADER);
    selectionBox.getChildren().add(artistText);
    selectionBox.getChildren().add(albumText);

    //add the top navi to the root
    vMain.getChildren().add(topBox);
    tabRoot.setCenter(vMain);

    centerRegion = new HBox();
//    centerRegion.setMaxHeight(153);
    vMain.getChildren().add(centerRegion);

    pager = new Pager(tabRoot, new ArrayList<IServiceModel>(ServiceRegistry.getGoogleService().getAlbums()), false, false);
    this.activeAlbum = (Album) pager.getActiveModel();

    display();

    super.setPager(pager);
    super.setTabRoot(tabRoot);

    UIUtil.fadeInComponent(tabRoot);
    if (!albums.isEmpty()) {
      updatePage(albums.get(0));
    }

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
    centerRegion.setPadding(new Insets(0,185,0,185));

    for (Album album : albums) {
      HBox albumRoot = new HBox(0);
      albumRoot.setId(String.valueOf(album.getMID()));

      VBox vbox = new VBox(2);
      vbox.setMaxWidth(COVER_SIZE);
      vbox.setPadding(new Insets(3, 3, 3, 3));

      if (!StringUtils.isEmpty(album.getArtUrl())) {
        Canvas cover = UIUtil.createLazyLoadingImageCanvas(album.getCoverId(), album.getArtUrl(), COVER_SIZE, COVER_SIZE);
        vbox.getChildren().add(cover);
      }
      else {
        String url = ResourceLoader.getResource("cover.png");
        Canvas cover = UIUtil.createImageCanvas(url, COVER_SIZE, COVER_SIZE);
        vbox.getChildren().add(cover);
      }

      Text text = new Text(0, 0, formatLabel(album.getArtist(), 13));
      text.setFont(UIUtil.FONT_BOLD_12);
      text.setFill(UIUtil.COLOR_DARK_HEADER);
      vbox.getChildren().add(text);

      text = new Text(0, 0, formatLabel(album.getName(), 15));
      text.setFont(UIUtil.FONT_NORMAL_12);
      text.setFill(UIUtil.COLOR_DARK_HEADER);
      vbox.getChildren().add(text);

      albumRoot.getChildren().add(vbox);
      hBoxAlbums.getChildren().add(albumRoot);
    }
  }

  @Override
  public void updatePage(IServiceModel model) {
    Album album = (Album) model;
    albumText.setText(album.getName());
    artistText.setText(album.getArtist() + ": ");

    if (selectedAlbumNode != null) {
      selectedAlbumNode.setStyle(STYLE_INACTIVE);
    }

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

  @Override
  public IRotaryControllable next() {
    if(!pager.isAtEnd()) {
      TranslateTransitionBuilder.create()
          .duration(Duration.millis(SCROLL_DELAY))
          .node(centerRegion)
          .fromX(scrollPos)
          .toX(scrollPos - SCROLL_WIDTH)
          .autoReverse(false)
          .build().play();

      scrollPos-=SCROLL_WIDTH;
    }
    super.next();
    activeAlbum = (Album) pager.getActiveModel();
    return this;
  }

  @Override
  public IRotaryControllable prev() {
    if(!pager.isAtStart()) {
      TranslateTransitionBuilder.create()
          .duration(Duration.millis(SCROLL_DELAY))
          .node(centerRegion)
          .fromX(scrollPos)
          .toX(scrollPos + SCROLL_WIDTH)
          .autoReverse(false)
          .build().play();

      scrollPos+=SCROLL_WIDTH;
    }

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
