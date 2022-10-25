package lee.aspect.dev.application.interfaceGui;


import javafx.scene.Parent;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class WarningManager {

    public static ImageView setWarning(double x, double y, int size){
        ImageView warning = new ImageView(Objects.requireNonNull(WarningManager.class.getResource("/lee/aspect/dev/icon/Warning.png")).toExternalForm());
        warning.setFitHeight(size);
        warning.setFitWidth(size);
        warning.setLayoutX(x);
        warning.setLayoutY(y);
        return warning;
    }

    public static ImageView setWarning(Parent parent, int size){
        return setWarning(parent.getLayoutX()-10 - size/2, parent.getLayoutY() + parent.getLayoutBounds().getCenterY() - size/2, size);
    }

}
