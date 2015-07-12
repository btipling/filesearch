package FileSearch.dialogs;

import FileSearch.Search;
import FileSearch.SearchManager;
import FileSearch.SearchOptions;

import javax.swing.*;
import java.awt.event.*;

public class SearchDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel resultsPanel;
    private JPanel searchFormPanel;
    private JPanel textInputPanel;
    private JPanel searchOptionsPanel;
    private JTextField textField1;
    private JCheckBox caseCB;
    private JCheckBox regexCB;
    private JRadioButton fileNameRadioButton;
    private JRadioButton fullPathRadioButton;
    private JButton selectSearchPathButton;
    private JLabel pathLabel;
    private JCheckBox recursiveCB;
    private JScrollPane resultsPane;
    private JList resultsList;
    private SearchManager searchManager;
    private String searchPath;

    public SearchDialog(final SearchManager searchManager) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        this.searchManager = searchManager;

        buttonOK.addActionListener(new ActionListener() {
            final SearchManager sm = searchManager;
            public void actionPerformed(ActionEvent e) {
                Search search = new Search(createSearchOptions());
                sm.execute(search);
                onOK();
            }
        });

        buttonCancel.addActionListener(e -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    protected SearchOptions createSearchOptions(){
        SearchOptions so = new SearchOptions();
        so.searchPath = searchPath;
        so.searchString = textField1.getText();
        so.caseSensitive = caseCB.isSelected();
        so.regex = regexCB.isSelected();
        so.wholePath = fullPathRadioButton.isSelected();
        so.recursive = recursiveCB.isSelected();

        return so;
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }

}
