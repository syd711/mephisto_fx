package de.mephisto.radiofx.ui.controller;

import de.mephisto.radiofx.resources.ResourceLoader;
import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.services.ServiceRegistry;
import de.mephisto.radiofx.services.google.Album;
import de.mephisto.radiofx.services.google.IGoogleMusicService;
import de.mephisto.radiofx.services.google.Playlist;
import de.mephisto.radiofx.ui.Pager;
import de.mephisto.radiofx.util.UIUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
  private static final Font SELECTION_FONT= Font.font("Tahoma", FontWeight.NORMAL, 16);

  private static final int VISIBLE_ITEM_COUNT = 8;
  private static final int COVER_SIZE = 105;

  private Text selectionText;
  private ScrollPane centerScroller;

  @Override
  public BorderPane init() {
    BorderPane tabRoot = new BorderPane();
    tabRoot.setMinHeight(UIUtil.HEIGHT - 88);
    IGoogleMusicService service = ServiceRegistry.getGoogleService();
    List<Album> albums = service.getAlbums();

    VBox vMain = new VBox(5);
    vMain.setPadding(new Insets(5,5,0,5));
    vMain.setAlignment(Pos.CENTER);

    selectionText = new Text(0, 0, "");
    selectionText.setFont(SELECTION_FONT);
    selectionText.setFill(UIUtil.COLOR_DARK_HEADER);
    vMain.getChildren().add(selectionText);

    centerScroller = new ScrollPane();
    centerScroller.setMinHeight(153);
    centerScroller.setStyle("-fx-background-color:transparent;");
    centerScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

    HBox hBoxAlbums = new HBox(10);
    centerScroller.setContent(hBoxAlbums);
    vMain.getChildren().add(centerScroller);


    int count = 0;
    for(Album album : albums) {
      if(count > VISIBLE_ITEM_COUNT) {
        break;
      }
      count++;

      VBox vbox = new VBox(2);

      if(!StringUtils.isEmpty(album.getArtUrl())) {
        LOG.info("Resolving cover for album " + album.getName());
        Canvas cover = UIUtil.createImageCanvas(album.getArtUrl(), COVER_SIZE, COVER_SIZE);
        vbox.getChildren().add(cover);
      }
      else {
        String url = ResourceLoader.getResource("cover.png");
        Canvas cover = UIUtil.createImageCanvas(url, COVER_SIZE, COVER_SIZE);
        vbox.getChildren().add(cover);
      }

      Text text = new Text(0, 0, formatLabel(album.getArtist()));
      text.setFont(ARTIST_TITLE_FONT);
      text.setFill(UIUtil.COLOR_DARK_HEADER);
      vbox.getChildren().add(text);

      text = new Text(0, 0, formatLabel(album.getName()));
      text.setFont(ALBUM_TITLE_FONT);
      text.setFill(UIUtil.COLOR_DARK_HEADER);
      vbox.getChildren().add(text);

      hBoxAlbums.getChildren().add(vbox);
    }

    tabRoot.setCenter(vMain);

    super.setPager(new Pager(tabRoot, service, this, false));
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
      Album album = (Album) model;
      String title = album.getArtist() + ": " + album.getName();
      if(title.length() > 60) {
        title = title.substring(0, 59) + "...";
      }
      selectionText.setText(title);
      centerScroller.setHvalue(centerScroller.getHmax());
    }
  }

  private String formatLabel(String label) {
    if(label.length() > 16) {
      label = label.substring(0, 15) + "...";
    }
    return label;
  }

}
