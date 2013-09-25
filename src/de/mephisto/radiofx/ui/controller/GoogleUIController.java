package de.mephisto.radiofx.ui.controller;

import de.mephisto.radiofx.resources.ResourceLoader;
import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.services.ServiceRegistry;
import de.mephisto.radiofx.services.google.Album;
import de.mephisto.radiofx.services.google.IGoogleMusicService;
import de.mephisto.radiofx.services.google.Playlist;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;

/**
 * Controls the UI for the google music
 */
public class GoogleUIController extends PageableUIController {
  private final static Logger LOG = LoggerFactory.getLogger(GoogleUIController.class);

  private static final Font ALBUM_TITLE_FONT= Font.font("Tahoma", FontWeight.NORMAL, 12);
  private static final Font ARTIST_TITLE_FONT= Font.font("Tahoma", FontWeight.BOLD, 12);
  private static final Font SELECTION_FONT= Font.font("Tahoma", FontWeight.NORMAL, 14);
  private static final Font SELECTION_FONT_BOLD= Font.font("Tahoma", FontWeight.BOLD, 14);

  private static final int COVER_SIZE = 100;
  private static final double SCROLL_CORRECTION = 0.000023;

  private Text artistText;
  private Text albumText;
  private ScrollPane centerScroller;
  private double scrollPos;
  private HBox hBoxAlbums;
  private Node lastSelection;

  @Override
  public BorderPane init() {
    BorderPane tabRoot = new BorderPane();
    tabRoot.setMinHeight(UIUtil.MIN_MAIN_HEIGHT);
    IGoogleMusicService service = ServiceRegistry.getGoogleService();
    List<Album> albums = service.getAlbums();

    VBox vMain = new VBox(5);
    vMain.setPadding(new Insets(5,0,0,0));
    vMain.setAlignment(Pos.CENTER_LEFT);

    HBox selectionBox = new HBox(5);
    selectionBox.setPadding(new Insets(0,0,0,15));

    artistText = new Text(0, 0, "");
    artistText.setFont(SELECTION_FONT_BOLD);
    artistText.setFill(UIUtil.COLOR_DARK_HEADER);
    albumText = new Text(0, 0, "");
    albumText.setFont(SELECTION_FONT);
    albumText.setFill(UIUtil.COLOR_DARK_HEADER);
    selectionBox.getChildren().add(artistText);
    selectionBox.getChildren().add(albumText);

    vMain.getChildren().add(selectionBox);

    centerScroller = new ScrollPane();
    centerScroller.setMinHeight(153);
    centerScroller.setStyle("-fx-background-color:transparent;");
    centerScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

    hBoxAlbums = new HBox(5);
    hBoxAlbums.setPadding(new Insets(0,185,0,185));
    centerScroller.setContent(hBoxAlbums);
    vMain.getChildren().add(centerScroller);


    int count = 0;
    for(Album album : albums) {
      VBox vbox = new VBox(2);
      vbox.setMaxWidth(COVER_SIZE);
      vbox.setPadding(new Insets(3,3,3,3));
      vbox.setId(String.valueOf(album.getMID()));

      if(!StringUtils.isEmpty(album.getArtUrl())) {
        Canvas cover = UIUtil.createLazyLoadingImageCanvas(album.getArtUrl(), COVER_SIZE, COVER_SIZE);
        vbox.getChildren().add(cover);
      }
      else {
        String url = ResourceLoader.getResource("cover.png");
        Canvas cover = UIUtil.createImageCanvas(url, COVER_SIZE, COVER_SIZE);
        vbox.getChildren().add(cover);
      }

      Text text = new Text(0, 0, formatLabel(album.getArtist(), 13));
      text.setFont(ARTIST_TITLE_FONT);
      text.setFill(UIUtil.COLOR_DARK_HEADER);
      vbox.getChildren().add(text);

      text = new Text(0, 0, formatLabel(album.getName(), 15));
      text.setFont(ALBUM_TITLE_FONT);
      text.setFill(UIUtil.COLOR_DARK_HEADER);
      vbox.getChildren().add(text);

      hBoxAlbums.getChildren().add(vbox);
    }

    tabRoot.setCenter(vMain);

    super.setPager(new Pager(tabRoot, service, this, false, false));
    super.setTabRoot(tabRoot);

    UIUtil.fadeInComponent(tabRoot);
    if(!albums.isEmpty()) {
      updatePage(albums.get(0));
    }

    return tabRoot;
  }

  /**
   * Updates the fields with the given station info.
   * @param model
   */
  @Override
  public void updatePage(IServiceModel model) {
    if(model instanceof Album) {
      if(lastSelection != null) {
        lastSelection.setStyle("-fx-background-color:transparent;");
      }
      Album album = (Album) model;
      albumText.setText(album.getName());
      artistText.setText(album.getArtist() + ":");

      final ObservableList<Node> children = hBoxAlbums.getChildren();
      for(Node node : children) {
        String id = node.getId();
        if(id.equals(String.valueOf(album.getMID()))) {
          lastSelection = node;
          lastSelection.setStyle("-fx-background-color:" + UIUtil.HEX_COLOR_INACTIVE + ";");
          break;
        }
      }
    }
  }

  @Override
  public void next() {
    scrollPos+=(1.0/hBoxAlbums.getChildren().size());
    if(scrollPos > 1.0) {
      scrollPos = 1;
    }
    scrollPos+=SCROLL_CORRECTION;
    centerScroller.setHvalue(scrollPos);
    super.next();
  }

  @Override
  public void prev() {
    scrollPos-=(1.0/hBoxAlbums.getChildren().size());
    if(scrollPos < 0) {
      scrollPos = 0;
    }
    scrollPos-=SCROLL_CORRECTION;
    centerScroller.setHvalue(scrollPos);
    super.prev();
  }

  private String formatLabel(String label, int length) {
    if(label.length() > length) {
      label = label.substring(0, length-1) + "..";
    }
    return label;
  }

}
