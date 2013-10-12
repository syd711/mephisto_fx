package de.mephisto.radiofx.ui.controller;

import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.services.ServiceRegistry;
import de.mephisto.radiofx.services.google.Album;
import de.mephisto.radiofx.services.google.IGoogleMusicService;
import de.mephisto.radiofx.services.google.Song;
import de.mephisto.radiofx.services.mpd.IMpdService;
import de.mephisto.radiofx.ui.Footer;
import de.mephisto.radiofx.ui.UIStateController;
import de.mephisto.radiofx.util.Colors;
import de.mephisto.radiofx.util.Fonts;
import de.mephisto.radiofx.util.PaneUtil;
import de.mephisto.radiofx.util.TransitionUtil;
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
  public static final String STYLE_INACTIVE = "-fx-background-color: " + Colors.HEX_COLOR_BACKGROUND + ";-fx-border-color: " + Colors.HEX_COLOR_SEPARATOR +
      " " + Colors.HEX_COLOR_SEPARATOR + " " + Colors.HEX_COLOR_SEPARATOR + " " + Colors.HEX_COLOR_SEPARATOR + ";";

  public static final String STYLE_ACTIVE = "-fx-background-color: " + Colors.HEX_COLOR_INACTIVE + ";-fx-border-color: " + Colors.HEX_COLOR_SEPARATOR +
      " " + Colors.HEX_COLOR_SEPARATOR + " " + Colors.HEX_COLOR_SEPARATOR + " " + Colors.HEX_COLOR_SEPARATOR + ";";

  private final static int VISIBLE_ITEM_COUNT = 4;
  private final static int SCROLL_DELAY = 400;
  private final static int SCROLL_LEFT_LENGTH = 27;

  private int scrollPos = 0;

  private Album album;

  private VBox songBox;
  private Node lastSongSelection;
  private Pane albumNode;

  private ScrollPane songScroller;
  private IMpdService mpdService;

  private FadeTransition blink;
  private Node lastBlinkNode;

  public GoogleUIPlayerController() {
    //the player does not use the mpd service!
    super(ServiceRegistry.getMpdService());
  }


  @Override
  public BorderPane init() {
    mpdService = ServiceRegistry.getMpdService();
    return null; //component does only modify the current UI and re-uses the pager.
  }


  @Override
  public void onDisplay() {
    GoogleUINaviController naviController = UIStateController.getInstance().getGoogleNaviController();
    album = naviController.getActiveAlbum();
    setPager(naviController.getPager());

    albumNode = (Pane) naviController.getSelectedAlbumNode();
    albumNode.setStyle(GoogleUINaviController.STYLE_INACTIVE);

    showPlayerMode(true);
    updatePage(album.getActiveSong());
  }

  @Override
  public void onDispose() {
    GoogleUINaviController naviController = UIStateController.getInstance().getGoogleNaviController();
    showPlayerMode(false);
    setPager(naviController.getPager());
  }

  @Override
  public IRotaryControllable prev() {
    if (getPager().isAtStart()) {
      return UIStateController.getInstance().getGoogleNaviController();
    }

    super.prev();

    if (getPager().getPosition() > (getPager().size() - VISIBLE_ITEM_COUNT)) {
      return this;
    }

    if (scrollPos < 0) {
      TransitionUtil.moveNodeY(songBox, scrollPos, scrollPos + SCROLL_LEFT_LENGTH, false, SCROLL_DELAY);
      scrollPos += SCROLL_LEFT_LENGTH;
    }
    return this;
  }

  @Override
  public IRotaryControllable next() {
    if (getPager().isAtEnd()) {
      return UIStateController.getInstance().getGoogleNaviController();
    }
    super.next();

    if (getPager().getPosition() < (VISIBLE_ITEM_COUNT - 1)) {
      return this;
    }
    if (getPager().getPosition() > ((getPager().size()+1) - VISIBLE_ITEM_COUNT)) {
      return this;
    }

    TransitionUtil.moveNodeY(songBox, scrollPos, scrollPos - SCROLL_LEFT_LENGTH, false, SCROLL_DELAY);
    scrollPos -= SCROLL_LEFT_LENGTH;
    return this;
  }

  @Override
  public IRotaryControllable longPush() {
    return UIStateController.getInstance().getGoogleNaviController();
  }

  @Override
  public IRotaryControllable push() {
    Song song = (Song) getPager().getActiveModel();
    album.reset();
    song.setActive(true);
    mpdService.playAlbum(album);
    Node node = getNodeForSong(song);
    updateBlink(node);
    return UIStateController.getInstance().getGooglePlayerController();
  }

  @Override
  public void serviceDataChanged(IServiceModel model) {
    if(model instanceof Song) {
      if(getPager() != null) {
        getPager().updateActivity(); //the activity status may have changed by the backend.
      }
      updatePage(model);
    }
  }

  @Override
  public void updatePage(IServiceModel model) {
    if(getPager() == null) {   //maybe null during init
      return;
    }
    if (model instanceof Song) {
      Song song = (Song) model;
      if(album.getActiveSong() == null) {
        updateBlink(null);
      }
      if(song.isActive()) {
        Node container = getNodeForSong(song);
        updateBlink(container);
      }


      if (lastSongSelection != null) {
        lastSongSelection.setStyle(STYLE_INACTIVE);
      }

      if (songBox != null) {
        final ObservableList<Node> children = songBox.getChildren();
        for (Node node : children) {
          String id = node.getId();
          if(getPager().getActiveModel() instanceof Song) {
            if (id.equals(String.valueOf(((Song)getPager().getActiveModel()).getMID()))) {
              lastSongSelection = node;
              lastSongSelection.setStyle(STYLE_ACTIVE);
            }
          }
        }
      }
    }
  }

  @Override
  public int getFooterId() {
    return Footer.FOOTER_MUSIC;
  }


  /**
   * Creates the album playback view.
   */
  private void display() {
    scrollPos = 0;

    songScroller = new ScrollPane();
    songScroller.setMinWidth(PaneUtil.WIDTH-GoogleUINaviController.COVER_SIZE-10);
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
        lastSongSelection = trackBox;
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
      nameBox.setMinWidth(395);

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
  }

  /**
   * Creates a text field for the track container.
   *
   * @param label
   * @return
   */
  private Text createTrackText(String label) {
    if (label.length() > 33) {
      label = label.substring(0, 32) + "...";
    }
    Text text = new Text(label);
    text.setFont(Fonts.FONT_NORMAL_20);
    return text;
  }


  /**
   * Returns the row for the given song.
   * @param song
   * @return
   */
  private Node getNodeForSong(Song song) {
    final ObservableList<Node> children = songBox.getChildren();
    for (Node node : children) {
      String id = node.getId();
      if (id.equals(String.valueOf(song.getMID()))) {
        return node;
      }
    }
    return null;
  }

  /**
   * Updates the UI for the active Track.
   * @param node
   */
  private void updateBlink(Node node) {
    if(node == null && lastBlinkNode != null) {
      lastBlinkNode.setOpacity(1);
      blink.stop();
    }
    if(lastBlinkNode == null || lastBlinkNode != node) {
      if(lastBlinkNode != null) {
        lastBlinkNode.setOpacity(1);
        blink.stop();
      }
      lastBlinkNode = node;
      blink = TransitionUtil.createBlink(node);
      blink.play();
    }
  }

  /**
   * Toogles the view between navigation view and playback mode.
   * @param show
   */
  private void showPlayerMode(final boolean show) {
    getPager().toggleMode();
    if (show) {
      getPager().setModels(new ArrayList<IServiceModel>(album.getSongs()), album.getSongs().get(0));
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
      final FadeTransition outFader = TransitionUtil.createOutFader(songScroller);
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
      TransitionUtil.moveNodeX(node, 0, children.indexOf(albumNode) * GoogleUINaviController.COVER_SIZE + 490, !show, SCROLL_DELAY);
    }

    final EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        if (show) {
          display();

          TransitionUtil.fadeInComponent(songScroller);
        }
        else {
          if(UIStateController.getInstance().getActiveController() != GoogleUIPlayerController.this) {
            albumNode.setStyle(GoogleUINaviController.STYLE_ACTIVE);
          }
        }
      }
    };

    TransitionUtil.moveNode(centerRegion, scrollPos, (scrollPos - 215), !show, SCROLL_DELAY, eventHandler, true);
  }

}
