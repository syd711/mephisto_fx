package de.mephisto.radiofx.ui.controller;

import de.mephisto.radiofx.resources.ResourceLoader;
import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.services.ServiceRegistry;
import de.mephisto.radiofx.services.google.Album;
import de.mephisto.radiofx.services.google.IGoogleMusicService;
import de.mephisto.radiofx.util.UIUtil;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 *
 */
public class GoogleUINaviHandler {
  public static final int COVER_SIZE = 100;
  private static final double SCROLL_CORRECTION = 0.000023;

  private GoogleUIController googleUIController;
  private HBox hBoxAlbums;
  private double scrollPos;
  private Node lastAlbumSelection;

  public GoogleUINaviHandler(GoogleUIController googleUIController) {
    this.googleUIController = googleUIController;
  }

  /**
   * Creates the horizontal scrollable album navi.
   */
  public void display() {
    IGoogleMusicService service = ServiceRegistry.getGoogleService();
    List<Album> albums = service.getAlbums();

    hBoxAlbums = new HBox(5);
    hBoxAlbums.setPadding(new Insets(0,185,0,185));
    googleUIController.getCenterRegion().setContent(hBoxAlbums);

    for(Album album : albums) {
      VBox vbox = new VBox(2);
      vbox.setMaxWidth(COVER_SIZE);
      vbox.setPadding(new Insets(3,3,3,3));
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

      Text text = new Text(0, 0, formatLabel(album.getArtist(), 13));
      text.setFont(UIUtil.FONT_NORMAL_12);
      text.setFill(UIUtil.COLOR_DARK_HEADER);
      vbox.getChildren().add(text);

      text = new Text(0, 0, formatLabel(album.getName(), 15));
      text.setFont(UIUtil.FONT_NORMAL_12);
      text.setFill(UIUtil.COLOR_DARK_HEADER);
      vbox.getChildren().add(text);

      hBoxAlbums.getChildren().add(vbox);
    }

    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        googleUIController.getCenterRegion().setHvalue(scrollPos);
      }
    });
  }

  /**
   * Delegated from main controller
   */
  protected void prev() {
    scrollPos-=(1.0/hBoxAlbums.getChildren().size());
    if(scrollPos < 0) {
      scrollPos = 0;
    }
    scrollPos-=SCROLL_CORRECTION;
    googleUIController.getCenterRegion().setHvalue(scrollPos);
  }

  /**
   * Delegated from main controller
   */
  protected void next() {
    scrollPos+=(1.0/hBoxAlbums.getChildren().size());
    if(scrollPos > 1.0) {
      scrollPos = 1;
    }
    scrollPos+=SCROLL_CORRECTION;
    googleUIController.getCenterRegion().setHvalue(scrollPos);
  }

  public void updatePage(IServiceModel model) {
    Album album = (Album) model;

    if(lastAlbumSelection != null) {
      lastAlbumSelection.setStyle("-fx-background-color:transparent;");
    }

    final ObservableList<Node> children = hBoxAlbums.getChildren();
    for(Node node : children) {
      String id = node.getId();
      if(id.equals(String.valueOf(album.getMID()))) {
        lastAlbumSelection = node;
        lastAlbumSelection.setStyle("-fx-background-color:" + UIUtil.HEX_COLOR_INACTIVE + ";");
        break;
      }
    }
  }


  /**
   * Helper that ensures that the text of the album does not extend the
   * box width.
   * @param label
   * @param length
   * @return
   */
  private String formatLabel(String label, int length) {
    if(label.length() > length) {
      label = label.substring(0, length-1) + "..";
    }
    return label;
  }
}
