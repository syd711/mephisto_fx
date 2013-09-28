package de.mephisto.radiofx.ui.controller;

import de.mephisto.radiofx.resources.ResourceLoader;
import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.services.ServiceRegistry;
import de.mephisto.radiofx.services.google.Album;
import de.mephisto.radiofx.services.google.IGoogleMusicService;
import de.mephisto.radiofx.services.google.Song;
import de.mephisto.radiofx.ui.Pager;
import de.mephisto.radiofx.ui.UIStateController;
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
public class GoogleUIPlayerController extends PageableUIController {
  public static final String STYLE_INACTIVE = "-fx-background-color: " + UIUtil.HEX_COLOR_BACKGROUND + ";-fx-border-color: " + UIUtil.HEX_COLOR_SEPARATOR +
      " " + UIUtil.HEX_COLOR_SEPARATOR + " " + UIUtil.HEX_COLOR_SEPARATOR + " " + UIUtil.HEX_COLOR_SEPARATOR + ";";

  public static final String STYLE_ACTIVE = "-fx-background-color: " + UIUtil.HEX_COLOR_INACTIVE + ";-fx-border-color: " + UIUtil.HEX_COLOR_SEPARATOR +
      " " + UIUtil.HEX_COLOR_SEPARATOR + " " + UIUtil.HEX_COLOR_SEPARATOR + " " + UIUtil.HEX_COLOR_SEPARATOR + ";";

  private final static int COVER_SIZE = 100;
  private final static int VISIBLE_ITEM_COUNT = 3;
  private final static int SCROLL_DELAY = 300;
  private final static int SCROLL_LENGTH = 29;

  private int scrollPos = 0;

  private Text artistText;
  private Text albumText;

  private Album album;
  private boolean backSelected;

  private VBox songBox;
  private HBox backBox;
  private Node lastSongSelection;
  private ScrollPane centerRegion;
  private Pager pager;

  public GoogleUIPlayerController() {
    super(ServiceRegistry.getGoogleService());
  }


  @Override
  public BorderPane init() {
    BorderPane tabRoot = new BorderPane();
    tabRoot.setMinHeight(UIUtil.MIN_MAIN_HEIGHT);
    IGoogleMusicService service = ServiceRegistry.getGoogleService();
    List<Album> albums = service.getAlbums();

    VBox vMain = new VBox(5);
    vMain.setPadding(new Insets(5, 0, 0, 0));
    vMain.setAlignment(Pos.CENTER_LEFT);

    HBox topBox = new HBox(5);
    topBox.setPadding(new Insets(0, 0, 0, 15));

    HBox selectionBox = new HBox();
    selectionBox.setMinWidth(419);
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

    //back button
    backBox = new HBox();
    backBox.setAlignment(Pos.CENTER);
    backBox.setMinWidth(30);
    backBox.setMinHeight(20);
    backBox.setStyle(GoogleUIPlayerController.STYLE_INACTIVE);
    final Canvas imageCanvas = UIUtil.createImageCanvas(ResourceLoader.getResource("backward.png"), 16, 16);
    backBox.getChildren().add(imageCanvas);
    topBox.getChildren().add(backBox);
    backBox.setVisible(false);

    //add the top navi to the root
    vMain.getChildren().add(topBox);
    tabRoot.setCenter(vMain);

    centerRegion = new ScrollPane();
    centerRegion.setMinHeight(153);
    centerRegion.setStyle("-fx-background-color:transparent;");
    centerRegion.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    vMain.getChildren().add(centerRegion);

    pager = new Pager(tabRoot, new ArrayList<IServiceModel>(), true, false);
    this.album = (Album) pager.getActiveModel();

    super.setPager(pager);
    super.setTabRoot(tabRoot);

    UIUtil.fadeInComponent(tabRoot);
    if (!albums.isEmpty()) {
      updatePage(albums.get(0));
    }

    return tabRoot;
  }


  @Override
  public void onDisplay() {
    album = UIStateController.getInstance().getGoogleNaviController().getActiveAlbum();
    //apply header texts
    artistText.setText(album.getArtist()+ ": ");
    albumText.setText(album.getName());
    //apply default value for pager
    album.setActiveSong(album.getSongs().get(0));
    //apply models to pager
    pager.setModels(new ArrayList<IServiceModel>(album.getSongs()), album.getActiveSong());
    display();
  }

  /**
   * Creates the album playback view.
   */
  private void display() {
    backBox.setVisible(true);
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
    centerRegion.setContent(playbackBox);

    updatePage(album.getActiveSong());
  }

  @Override
  public void prev() {
    selectBackButton(false);
    if (album.getActiveSongIndex() == 0) {
      selectBackButton(true);
      return;
    }

    album.setActiveSong((Song) pager.getActiveModel());

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

  @Override
  public void next() {
    selectBackButton(false);
    if (album.getActiveSongIndex() >= (album.getSize() - 1)) {
      selectBackButton(true);
      return;
    }
    super.next();
    album.setActiveSong((Song) pager.getActiveModel());

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

  @Override
  public void updatePage(IServiceModel model) {
    if (lastSongSelection != null) {
      lastSongSelection.setStyle(STYLE_INACTIVE);
    }

    if(songBox != null) {
      final ObservableList<Node> children = songBox.getChildren();
      for (Node node : children) {
        String id = node.getId();
        if (!backSelected && id.equals(String.valueOf(album.getActiveSong().getMID())) ) {
          lastSongSelection = node;
          lastSongSelection.setStyle(STYLE_ACTIVE);
          break;
        }
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


  @Override
  public IRotaryControllable longPush() {
    return UIStateController.getInstance().getRadioController();
  }

  @Override
  public IRotaryControllable push() {
    return UIStateController.getInstance().getGooglePlayerController();
  }
}
