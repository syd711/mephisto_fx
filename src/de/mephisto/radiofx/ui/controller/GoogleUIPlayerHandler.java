package de.mephisto.radiofx.ui.controller;

import de.mephisto.radiofx.resources.ResourceLoader;
import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.services.ServiceRegistry;
import de.mephisto.radiofx.services.google.Album;
import de.mephisto.radiofx.services.google.IGoogleMusicService;
import de.mephisto.radiofx.services.google.Song;
import de.mephisto.radiofx.util.UIUtil;
import javafx.animation.TranslateTransitionBuilder;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class GoogleUIPlayerHandler {
  public static final String STYLE_INACTIVE = "-fx-background-color: " + UIUtil.HEX_COLOR_BACKGROUND + ";-fx-border-color: " + UIUtil.HEX_COLOR_SEPARATOR +
      " " + UIUtil.HEX_COLOR_SEPARATOR + " " + UIUtil.HEX_COLOR_SEPARATOR + " " + UIUtil.HEX_COLOR_SEPARATOR + ";";

  public static final String STYLE_ACTIVE = "-fx-background-color: " + UIUtil.HEX_COLOR_INACTIVE + ";-fx-border-color: " + UIUtil.HEX_COLOR_SEPARATOR +
      " " + UIUtil.HEX_COLOR_SEPARATOR + " " + UIUtil.HEX_COLOR_SEPARATOR + " " + UIUtil.HEX_COLOR_SEPARATOR + ";";

  public static final int COVER_SIZE = 100;

  private final static int VISIBLE_ITEM_COUNT = 3;

  private final static int SCROLL_DELAY = 300;
  private static int SCROLL_LENGTH = 29;
  private int scrollPos = 0;

  private Node lastSongSelection;
  private VBox songBox;
  private GoogleUIController googleUIController;
  private Album album;
  private boolean visible;
  private boolean backSelected;
  private Node backBox;

  public GoogleUIPlayerHandler(GoogleUIController googleUIController, HBox backBox) {
    this.backBox = backBox;
    this.googleUIController = googleUIController;
  }

  public boolean isBackSelected() {
    return backSelected;
  }


  /**
   * Creates the album playback view.
   */
  public void display(Album album) {
    backBox.setVisible(true);
    this.visible = true;
    this.album = album;
    scrollPos = 0;

    HBox playbackBox = new HBox(5);
    playbackBox.setAlignment(Pos.TOP_LEFT);

    VBox vbox = new VBox(1);
    vbox.setAlignment(Pos.TOP_RIGHT);
    vbox.setPadding(new Insets(2, 10, 2, 12));
    vbox.setId(String.valueOf(album.getMID()));

    if (!StringUtils.isEmpty(album.getArtUrl())) {
      Canvas cover = UIUtil.createLazyLoadingImageCanvas(album.getCoverId(), album.getArtUrl(), COVER_SIZE, COVER_SIZE);
      vbox.getChildren().add(cover);
    }
    else {
      String url = ResourceLoader.getResource("cover.png");
      Canvas cover = UIUtil.createImageCanvas(url, COVER_SIZE, COVER_SIZE);
      vbox.getChildren().add(cover);
    }

    Text text = new Text(0, 0, album.getSize() + " tracks");
    text.setFont(UIUtil.FONT_NORMAL_12);
    text.setFill(UIUtil.COLOR_DARK_HEADER);
    vbox.getChildren().add(text);

    if (album.getYear() > 0) {
      text = new Text(0, 0, String.valueOf(album.getYear()));
      text.setFill(UIUtil.COLOR_DARK_HEADER);
      text.setFont(UIUtil.FONT_NORMAL_12);
      vbox.getChildren().add(text);
    }

    playbackBox.getChildren().add(vbox);

    ScrollPane songScroller = new ScrollPane();
    songScroller.setMinHeight(148);
    songScroller.setMinWidth(350);
    songScroller.setStyle("-fx-background-color:transparent;");
    songScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

    songBox = new VBox(4);
    songBox.setPadding(new Insets(2, 2, 2, 2));
    int track = 1;
    for (Song song : album.getSongs()) {
      HBox trackBox = new HBox();

      trackBox.setId(String.valueOf(song.getMID()));
      trackBox.setMinWidth(330);
      trackBox.setAlignment(Pos.BASELINE_LEFT);
      trackBox.setPadding(new Insets(3, 5, 3, 5));
      if (track == 1) {
        trackBox.setStyle(STYLE_ACTIVE);
      }
      else {
        trackBox.setStyle(STYLE_INACTIVE);
      }

      BorderPane innerTrackBox = new BorderPane();
      HBox posBox = new HBox(5);
      posBox.setMinWidth(20);
      posBox.getChildren().add(createTrackText(track + "."));
      innerTrackBox.setLeft(posBox);
      HBox nameBox = new HBox(5);
      nameBox.setMinWidth(270);

      String name = song.getName();
      if (name.endsWith(".mp3")) {
        name = name.substring(0, name.length() - 4);
      }
      nameBox.getChildren().add(createTrackText(" " + name));
      innerTrackBox.setCenter(nameBox);
      innerTrackBox.setRight(createTrackText(song.getDuration()));

      trackBox.getChildren().add(innerTrackBox);
      songBox.getChildren().add(trackBox);

      track++;
    }

    songScroller.setContent(songBox);

    playbackBox.getChildren().add(songScroller);
    googleUIController.getCenterRegion().setContent(playbackBox);

    updatePage(album.getActiveSong());
  }

  /**
   * Delegated from main controller
   */
  protected void prev() {
    selectBackButton(false);
    if (album.getActiveSongIndex() == 0) {
      selectBackButton(true);
      return;
    }

    album.setActiveSong((Song) googleUIController.getPager().getActiveModel());

    if (album.getActiveSongIndex() > (album.getSize() - VISIBLE_ITEM_COUNT)) {
      return;
    }

    if (scrollPos < 0) {
      TranslateTransitionBuilder.create()
          .duration(Duration.millis(SCROLL_DELAY))
          .node(songBox)
          .fromY(scrollPos)
          .toY(scrollPos + SCROLL_LENGTH)
          .autoReverse(false)
          .build().play();


      scrollPos += SCROLL_LENGTH;
    }
  }

  /**
   * Delegates next call.
   */
  protected void next() {
    selectBackButton(false);
    if (album.getActiveSongIndex() >= (album.getSize() - 1)) {
      selectBackButton(true);
      return;
    }
    album.setActiveSong((Song) googleUIController.getPager().getActiveModel());

    if (album.getActiveSongIndex() < (VISIBLE_ITEM_COUNT - 1)) {
      return;
    }
    if (album.getActiveSongIndex() > ((album.getSize() - 1) - VISIBLE_ITEM_COUNT)) {
      return;
    }

    TranslateTransitionBuilder.create()
        .duration(Duration.millis(SCROLL_DELAY))
        .node(songBox)
        .fromY(scrollPos)
        .toY(scrollPos - SCROLL_LENGTH)
        .autoReverse(false)
        .build().play();
    scrollPos -= SCROLL_LENGTH;
  }

  /**
   * Delegated update call from the goolge main controller.
   *
   * @param model
   */
  public void updatePage(IServiceModel model) {
    Song song = (Song) googleUIController.getPager().getActiveModel();

    if (lastSongSelection != null) {
      lastSongSelection.setStyle(STYLE_INACTIVE);
    }

    final ObservableList<Node> children = songBox.getChildren();
    for (Node node : children) {
      String id = node.getId();
      if (!backSelected && id.equals(String.valueOf(song.getMID())) ) {
        lastSongSelection = node;
        lastSongSelection.setStyle(STYLE_ACTIVE);
        break;
      }
    }
  }

  /**
   * Selects the back button for push events.
   *
   * @param select
   */
  private void selectBackButton(boolean select) {
    this.backSelected = select;
    if (backSelected) {
      backBox.setStyle(STYLE_ACTIVE);
    }
    else {
      backBox.setStyle(STYLE_INACTIVE);
    }
  }

  /**
   * Creates a text field for the track container.
   *
   * @param label
   * @return
   */
  private Text createTrackText(String label) {
    if(label.length() > 30) {
      label = label.substring(0, 29) + "...";
    }
    Text text = new Text(label);
    text.setFont(UIUtil.FONT_NORMAL_14);
    return text;
  }

  public boolean isVisible() {
    return visible;
  }

  /**
   * Delegates push from the google controller.
   */
  public void push() {
    if (backSelected) {
      selectBackButton(false);
      backBox.setVisible(false);
      this.visible = false;

      IGoogleMusicService service = ServiceRegistry.getGoogleService();
      List<Album> albums = service.getAlbums();
      googleUIController.getPager().setModels(new ArrayList<IServiceModel>(albums), album);
      googleUIController.getNaviHandler().display();

      googleUIController.getPager().toggleDisplayMode();
      UIUtil.fadeInComponent(googleUIController.getCenterRegion());
    }
    else {
      album.play();
    }
  }
}
