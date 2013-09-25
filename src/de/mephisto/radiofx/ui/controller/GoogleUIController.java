package de.mephisto.radiofx.ui.controller;

import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.services.ServiceRegistry;
import de.mephisto.radiofx.services.google.Album;
import de.mephisto.radiofx.services.google.IGoogleMusicService;
import de.mephisto.radiofx.services.google.Song;
import de.mephisto.radiofx.ui.Pager;
import de.mephisto.radiofx.util.UIUtil;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Controls the UI for the google music
 */
public class GoogleUIController extends PageableUIController {
  private Text artistText;
  private Text albumText;
  private ScrollPane centerScroller;
  private Pager pager;

  private boolean playbackMode = false;

  private GoogleUINaviHandler naviHandler;
  private GoogleUIPlayerHandler playerHandler;

  private Album activeAlbum;

  @Override
  public BorderPane init() {
    BorderPane tabRoot = new BorderPane();
    tabRoot.setMinHeight(UIUtil.MIN_MAIN_HEIGHT);
    IGoogleMusicService service = ServiceRegistry.getGoogleService();
    List<Album> albums = service.getAlbums();

    VBox vMain = new VBox(5);
    vMain.setPadding(new Insets(5, 0, 0, 0));
    vMain.setAlignment(Pos.CENTER_LEFT);

    HBox selectionBox = new HBox(5);
    selectionBox.setPadding(new Insets(0, 0, 0, 15));

    artistText = new Text(0, 0, "");
    artistText.setFont(UIUtil.FONT_BOLD_14);
    artistText.setFill(UIUtil.COLOR_DARK_HEADER);
    albumText = new Text(0, 0, "");
    albumText.setFont(UIUtil.FONT_NORMAL_14);
    albumText.setFill(UIUtil.COLOR_DARK_HEADER);
    selectionBox.getChildren().add(artistText);
    selectionBox.getChildren().add(albumText);

    vMain.getChildren().add(selectionBox);
    tabRoot.setCenter(vMain);

    centerScroller = new ScrollPane();
    centerScroller.setMinHeight(153);
    centerScroller.setStyle("-fx-background-color:transparent;");
    centerScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    vMain.getChildren().add(centerScroller);

    pager = new Pager(tabRoot, service, this, false, false);
    this.activeAlbum = (Album) pager.getActiveModel();

    naviHandler = new GoogleUINaviHandler(centerScroller);
    playerHandler = new GoogleUIPlayerHandler(centerScroller, pager);

    naviHandler.display();

    super.setPager(pager);
    super.setTabRoot(tabRoot);

    UIUtil.fadeInComponent(tabRoot);
    if (!albums.isEmpty()) {
      updatePage(albums.get(0));
    }

    return tabRoot;
  }


  /**
   * Updates the fields with the given station info.
   *
   * @param model
   */
  @Override
  public void updatePage(IServiceModel model) {
    if (model instanceof Album) {
      Album album = (Album) model;
      albumText.setText(album.getName());
      artistText.setText(album.getArtist() + ":");
      naviHandler.updatePage(model);
    }
    else if (model instanceof Song) {
      playerHandler.updatePage(model);
    }
  }

  @Override
  public void next() {
    if (playbackMode) {
      playerHandler.next();
    }
    else {
      naviHandler.next();
    }
    super.next();
  }

  @Override
  public void prev() {
    if (playbackMode) {
      playerHandler.prev();
    }
    else {
      naviHandler.prev();
    }
    super.prev();
  }

  @Override
  public void push() {
    playbackMode = !playbackMode;

    final FadeTransition outFader = UIUtil.createOutFader(centerScroller);
    outFader.onFinishedProperty().set(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        if (playbackMode) {
          activeAlbum = (Album) pager.getActiveModel();
          activeAlbum.setActiveSong(activeAlbum.getSongs().get(0));
          pager.setModels(new ArrayList<IServiceModel>(activeAlbum.getSongs()), activeAlbum.getActiveSong());
          playerHandler.display(activeAlbum);
        }
        else {
          IGoogleMusicService service = ServiceRegistry.getGoogleService();
          List<Album> albums = service.getAlbums();
          pager.setModels(new ArrayList<IServiceModel>(albums), activeAlbum);
          naviHandler.display();

        }
        pager.toggleDisplayMode();
        updatePage(activeAlbum);
        UIUtil.fadeInComponent(centerScroller);
      }
    });
    outFader.play();
  }

}
