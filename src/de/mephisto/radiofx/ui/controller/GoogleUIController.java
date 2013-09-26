package de.mephisto.radiofx.ui.controller;

import de.mephisto.radiofx.resources.ResourceLoader;
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
import javafx.scene.canvas.Canvas;
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
    HBox backBox = new HBox();
    backBox.setAlignment(Pos.CENTER);
    backBox.setMinWidth(30);
    backBox.setMinHeight(20);
    backBox.setStyle(GoogleUIPlayerHandler.STYLE_INACTIVE);
    final Canvas imageCanvas = UIUtil.createImageCanvas(ResourceLoader.getResource("backward.png"), 16, 16);
    backBox.getChildren().add(imageCanvas);
    topBox.getChildren().add(backBox);
    backBox.setVisible(false);

    //add the top navi to the root
    vMain.getChildren().add(topBox);
    tabRoot.setCenter(vMain);

    centerScroller = new ScrollPane();
    centerScroller.setMinHeight(153);
    centerScroller.setStyle("-fx-background-color:transparent;");
    centerScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    vMain.getChildren().add(centerScroller);

    pager = new Pager(tabRoot, service, this, false, false);
    this.activeAlbum = (Album) pager.getActiveModel();

    naviHandler = new GoogleUINaviHandler(centerScroller);
    playerHandler = new GoogleUIPlayerHandler(centerScroller, pager, backBox, naviHandler);

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
      artistText.setText(album.getArtist() + ": ");
      naviHandler.updatePage(model);
    }
    else if (model instanceof Song) {
      playerHandler.updatePage(model);
    }
  }

  @Override
  public void next() {
    if (playerHandler.isVisible()) {
      playerHandler.next();
    }
    else {
      naviHandler.next();
    }

    if(playerHandler.isVisible() && playerHandler.isBackSelected()) {
      return;
    }
    super.next();

  }

  @Override
  public void prev() {
    if (playerHandler.isVisible()) {
      playerHandler.prev();
    }
    else {
      naviHandler.prev();
    }
    if(playerHandler.isVisible() && playerHandler.isBackSelected()) {
      return;
    }
    super.prev();
  }

  @Override
  public void push() {
    if(playerHandler.isVisible()) {
      playerHandler.push();
      return;
    }

    final FadeTransition outFader = UIUtil.createOutFader(centerScroller);
    outFader.onFinishedProperty().set(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        activeAlbum = (Album) pager.getActiveModel();
        activeAlbum.setActiveSong(activeAlbum.getSongs().get(0));
        pager.setModels(new ArrayList<IServiceModel>(activeAlbum.getSongs()), activeAlbum.getActiveSong());
        playerHandler.display(activeAlbum);

        pager.toggleDisplayMode();
        updatePage(activeAlbum);
        UIUtil.fadeInComponent(centerScroller);
      }
    });
    outFader.play();
  }

}
