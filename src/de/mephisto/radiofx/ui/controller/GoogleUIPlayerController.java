package de.mephisto.radiofx.ui.controller;

import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.services.ServiceRegistry;
import de.mephisto.radiofx.services.google.Album;
import de.mephisto.radiofx.services.google.IGoogleMusicService;
import de.mephisto.radiofx.services.google.Song;
import de.mephisto.radiofx.ui.UIStateController;
import de.mephisto.radiofx.util.UIUtil;
import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

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

  private final static int VISIBLE_ITEM_COUNT = 4;
  private final static int SCROLL_DELAY = 400;
  private final static int SCROLL_LEFT_LENGTH = 29;

  private int scrollPos = 0;

  private Album album;

  private VBox songBox;
  private Node lastSongSelection;
  private Pane albumNode;

  private ScrollPane songScroller;

  public GoogleUIPlayerController() {
    super(ServiceRegistry.getGoogleService());
  }


  @Override
  public BorderPane init() {
    return null; //component does only modify the current UI and re-uses the pager.
  }


  @Override
  public void onDisplay() {
    GoogleUINaviController naviController = UIStateController.getInstance().getGoogleNaviController();
    album = naviController.getActiveAlbum();
    album.setActiveSong(album.getSongs().get(0));
    setPager(naviController.getPager());

    albumNode = (Pane) naviController.getSelectedAlbumNode();
    albumNode.setStyle(GoogleUINaviController.STYLE_INACTIVE);

    showPlayerMode(true);
    updatePage(album.getActiveSong());
  }

  private void showPlayerMode(final boolean show) {
    getPager().toggleMode();
    if (show) {
      getPager().setModels(new ArrayList<IServiceModel>(album.getSongs()), album.getActiveSong());
    }
    else {
      IGoogleMusicService service = ServiceRegistry.getGoogleService();
      List<Album> albums = service.getAlbums();
      getPager().setModels(new ArrayList<IServiceModel>(albums), album);
    }

    Pane centerRegion = UIStateController.getInstance().getGoogleNaviController().getCenterRegion();

    final ObservableList<Node> children = ((Pane) centerRegion.getChildren().get(0)).getChildren();
    List<Node> nodes = new ArrayList<Node>();
    int count = children.indexOf(albumNode)+1;
    for (int i = count; i < count+4; i++) {
      if (children.size()>i) {
        nodes.add(children.get(i));
      }
    }

    if(!show) {
      final FadeTransition outFader = UIUtil.createOutFader(songScroller);
      outFader.setOnFinished(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
          albumNode.getChildren().remove(songScroller);
        }
      });
      outFader.play();
    }

    int scrollPos = (int) UIStateController.getInstance().getGoogleNaviController().getScrollPos();
    for (Node node : nodes) {
      UIUtil.moveNodeX(node, 0, children.indexOf(albumNode) * 110 + 450, !show, SCROLL_DELAY);
    }

    final EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        if (show) {
          display();

          UIUtil.fadeInComponent(songScroller);
        }
        else {
          albumNode.setStyle(GoogleUINaviController.STYLE_ACTIVE);
        }
      }
    };

    UIUtil.moveNode(centerRegion, scrollPos, (scrollPos - 175), !show, SCROLL_DELAY, eventHandler, true);
  }


  @Override
  public IRotaryControllable prev() {
    if (getPager().isAtStart()) {
      showPlayerMode(false);
      return UIStateController.getInstance().getGoogleNaviController();
    }

    super.prev();
    album.setActiveSong((Song) getPager().getActiveModel());

    if (album.getActiveSongIndex() > (album.getSize() - VISIBLE_ITEM_COUNT)) {
      return this;
    }

    if (scrollPos < 0) {
      UIUtil.moveNodeY(songBox, scrollPos, scrollPos + SCROLL_LEFT_LENGTH, false, SCROLL_DELAY);
      scrollPos += SCROLL_LEFT_LENGTH;
    }
    return this;
  }

  @Override
  public IRotaryControllable next() {
    if (getPager().isAtEnd()) {
      showPlayerMode(false);
      return UIStateController.getInstance().getGoogleNaviController();
    }
    super.next();

    if (album.getActiveSongIndex() < (VISIBLE_ITEM_COUNT - 1)) {
      return this;
    }
    if (album.getActiveSongIndex() > ((album.getSize()) - VISIBLE_ITEM_COUNT)) {
      return this;
    }

    UIUtil.moveNodeY(songBox, scrollPos, scrollPos - SCROLL_LEFT_LENGTH, false, SCROLL_DELAY);
    scrollPos -= SCROLL_LEFT_LENGTH;
    return this;
  }

  @Override
  public IRotaryControllable longPush() {
    return UIStateController.getInstance().getRadioController();
  }

  @Override
  public IRotaryControllable push() {
    return UIStateController.getInstance().getGooglePlayerController();
  }

  @Override
  public void updatePage(IServiceModel model) {
    if (model instanceof Song) {
      album.setActiveSong((Song) getPager().getActiveModel());

      if (lastSongSelection != null) {
        lastSongSelection.setStyle(STYLE_INACTIVE);
      }

      if (songBox != null) {
        final ObservableList<Node> children = songBox.getChildren();
        for (Node node : children) {
          String id = node.getId();
          if (id.equals(String.valueOf(album.getActiveSong().getMID()))) {
            lastSongSelection = node;
            lastSongSelection.setStyle(STYLE_ACTIVE);
            break;
          }
        }
      }
    }
  }


  /**
   * Creates the album playback view.
   */
  private void display() {
    scrollPos = 0;

    songScroller = new ScrollPane();
    songScroller.setMinWidth(370);
    songScroller.setStyle("-fx-background-color:transparent;");
    songScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

    songBox = new VBox(4);
    songBox.setPadding(new Insets(3, 2, 2, 2));
    int track = 1;
    for (Song song : album.getSongs()) {
      HBox trackBox = new HBox();

      trackBox.setId(String.valueOf(song.getMID()));
      trackBox.setAlignment(Pos.BASELINE_LEFT);
      trackBox.setPadding(new Insets(2, 5, 2, 5));
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
      nameBox.setMinWidth(280);

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

    songScroller.setOpacity(0);
    songScroller.setContent(songBox);
    albumNode.getChildren().add(songScroller);

    updatePage(album.getActiveSong());
  }

  /**
   * Creates a text field for the track container.
   *
   * @param label
   * @return
   */
  private Text createTrackText(String label) {
    if (label.length() > 30) {
      label = label.substring(0, 29) + "...";
    }
    Text text = new Text(label);
    text.setFont(UIUtil.FONT_NORMAL_14);
    return text;
  }
}
