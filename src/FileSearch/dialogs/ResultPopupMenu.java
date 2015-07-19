package FileSearch.dialogs;

import javax.swing.*;
import java.awt.*;

public class ResultPopupMenu extends JPopupMenu {
    JList list;
    public ResultPopupMenu(JList list) {
        super();
        this.list = list;
    }

    @Override
    public void show(Component invoker, int x, int y) {
        int row = list.locationToIndex(new Point(x, y));
        if (row != -1) {
            list.setSelectedIndex(row);
        }
        super.show(invoker, x, y);
    }
}
