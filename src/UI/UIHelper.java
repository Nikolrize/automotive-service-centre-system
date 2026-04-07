package UI;

import java.awt.*;

public class UIHelper {
    public static GridBagConstraints createGbc(int x, int y, int inset) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(inset, inset, inset, inset);

        return gbc;
    }
}
