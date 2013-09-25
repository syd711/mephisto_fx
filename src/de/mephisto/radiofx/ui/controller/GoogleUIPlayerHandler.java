package de.mephisto.radiofx.ui.controller;

import de.mephisto.radiofx.resources.ResourceLoader;
import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.services.ServiceRegistry;
import de.mephisto.radiofx.services.google.Album;
import de.mephisto.radiofx.services.google.IGoogleMusicService;
import de.mephisto.radiofx.services.google.Song;
import de.mephisto.radiofx.ui.Pager;
import de.mephisto.radiofx.util.UIUtil;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 *
 */
public class GoogleUIPlayerHandler {
  private static final String STYLE_INACTIVE = "-fx-background-color: " + UIUtil.HEX_COLOR_BACKGROUND + ";-fx-border-color: " + UIUtil.HEX_COLOR_SEPARATOR +
      " " + UIUtil.HEX_COLOR_SEPARATOR + " " + UIUtil.HEX_COLOR_SEPARATOR + " " + UIUtil.HEX_COLOR_SEPARATOR + ";";

  private static final String STYLE_ACTIVE = "-fx-background-color: " + UIUtil.HEX_COLOR_INACTIVE + ";-fx-border-color: " + UIUtil.HEX_COLOR_SEPARATOR +
      " " + UIUtil.HEX_COLOR_SEPARATOR + " " + UIUtil.HEX_COLOR_SEPARATOR + " " + UIUtil.HEX_COLOR_SEPARATOR + ";";

  public static final int COVER_SIZE = 100;

  private ScrollPane centerScroller;
  private Node lastSongSelection;
  private VBox songBox;
  private Pager pager;

  public GoogleUIPlayerHandler(ScrollPane centerScroller, Pager pager) {
    this.centerScroller = centerScroller;
    this.pager = pager;
  }
  /**
   * Creates the album playback view.
   */
  public void display(Album album) {
    HBox playbackBox = new HBox(5);
    playbackBox.setAlignment(Pos.TOP_LEFT);

    VBox vbox = new VBox(1);
    vbox.setAlignment(Pos.TOP_RIGHT);
    vbox.setPadding(new Insets(2,10,2,12));
    vbox.setId(String.valueOf(album.getMID()));

    if(!StringUtils.isEmpty(album.getArtUrl())) {
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

    if(album.getYear() > 0){
      text = new Text(0, 0, String.valueOf(album.getYear()));
      text.setFill(UIUtil.COLOR_DARK_HEADER);
      text.setFont(UIUtil.FONT_NORMAL_12);
      vbox.getChildren().add(text);
    }

    playbackBox.getChildren().add(vbox);

    ScrollPane songScroller = new ScrollPane();
    songScroller.setMinHeight(153);
    songScroller.setMinWidth(350);
    songScroller.setStyle("-fx-background-color:transparent;");
    songScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

    songBox = new VBox(4);
    songBox.setPadding(new Insets(2,2,2,2));
    int track = 1;
    for(Song song : album.getSongs()) {
      HBox trackBox = new HBox();
      trackBox.setId(String.valueOf(song.getMID()));
      trackBox.setMinWidth(330);
      trackBox.setAlignment(Pos.BASELINE_LEFT);
      trackBox.setPadding(new Insets(3, 5, 3, 5));
      if(track == 1) {
        trackBox.setStyle(STYLE_ACTIVE);
      }
      else {
        trackBox.setStyle(STYLE_INACTIVE);
      }

      BorderPane innerTrackBox = new BorderPane();
      innerTrackBox.setLeft(new Text(track + "."));
      HBox nameBox = new HBox();
      nameBox.setMinWidth(280);
      nameBox.getChildren().add(new Text(" " + song.getName()));
      innerTrackBox.setCenter(nameBox);
      innerTrackBox.setRight(new Text(song.getDuration()));

      trackBox.getChildren().add(innerTrackBox);
      songBox.getChildren().add(trackBox);

      track++;
      if(track > 5) {
        break;
      }
    }

    songScroller.setContent(songBox);

    playbackBox.getChildren().add(songScroller);
    centerScroller.setContent(playbackBox);

    updatePage(album.getActiveSong());
  }

  protected void prev() {

  }

  protected void next() {
  }

  public void updatePage(IServiceModel model) {
    Song song = (Song) pager.getActiveModel();

    if(lastSongSelection != null) {
      lastSongSelection.setStyle(STYLE_INACTIVE);
    }
    final ObservableList<Node> children = songBox.getChildren();
    for(Node node : children) {
      String id = node.getId();
      if(id.equals(String.valueOf(song.getMID()))) {
        lastSongSelection = node;
        lastSongSelection.setStyle(STYLE_ACTIVE);
        break;
      }
    }
  }
}
