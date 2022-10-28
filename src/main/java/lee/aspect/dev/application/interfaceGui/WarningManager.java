package lee.aspect.dev.application.interfaceGui;


import javafx.scene.Parent;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class WarningManager {

    public enum Mode{
        Left,Right,Up,Down
    }

    public static ImageView setWarning(double x, double y, int size, String message){
        ImageView warning = new ImageView(Objects.requireNonNull(WarningManager.class.getResource("/lee/aspect/dev/icon/Warning.png")).toExternalForm());
        warning.setFitHeight(size);
        warning.setFitWidth(size);
        warning.setLayoutX(x);
        warning.setLayoutY(y);
        Tooltip.install(warning, new Tooltip(message));
        return warning;
    }

    public static ImageView setWarning(Parent parent, int size, String message, Mode mode, double ofSetX, double ofSetY){
        switch(mode) {
            case Up:
                return setWarning(parent.getLayoutX() + parent.getLayoutBounds().getCenterX() + ofSetX,
                        parent.getLayoutY()-10 - size/2 + ofSetY, size,message);
            case Down:
                return setWarning(parent.getLayoutX() + parent.getLayoutBounds().getCenterX()+ ofSetX,
                        parent.getLayoutY()+10 + size/2+ ofSetY, size,message);
            case Left:
                return setWarning(parent.getLayoutX()-10 - size/2+ ofSetX,
                        parent.getLayoutY() + parent.getLayoutBounds().getCenterY() - size/2+ ofSetY, size,message);
            case Right:
                return setWarning(parent.getLayoutX()+10+ size/2+ ofSetX,
                        parent.getLayoutY() + parent.getLayoutBounds().getCenterY() - size/2+ ofSetY, size,message);
            default:
                return null;
        }
    }

    public static ImageView setWarning(Parent p){
        return setWarning(p,16,"",Mode.Left);
    }

    public static ImageView setWarning(Parent parent, int size, String message, Mode mode){
        return setWarning(parent,size,message,mode,0,0);
    }

}
