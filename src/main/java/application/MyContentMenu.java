package application;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;

public class MyContentMenu extends ContextMenu {
    public static Parent source;

    public void show(Node anchor, double screenX, double screenY, Parent parent) {
        source = parent;
        super.show(anchor, screenX, screenY);
    }
}
