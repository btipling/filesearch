package FileSearch.dialogs;

import FileSearch.FSLog;
import FileSearch.Search;
import FileSearch.SearchManager;
import FileSearch.SearchOptions;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

public class SearchDialog extends JDialog {
    private Project project;
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
    private JRadioButton fileNameOnlyRadioButton;
    private JRadioButton fullPathRadioButton;
    private JButton selectDirectoriesToSearchButton;
    private JCheckBox recursiveCB;
    private JScrollPane resultsPane;
    private JList resultsList;
    private JList list1;
    private SearchManager searchManager;
    private ArrayList<String> searchPaths = new ArrayList<String>();

    public SearchDialog(final SearchManager searchManager) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        this.searchManager = searchManager;

        buttonOK.addActionListener(e -> {
            Search search = new Search(createSearchOptions());
            searchManager.execute(search);
            onOK();
        });

        buttonCancel.addActionListener(e -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        selectDirectoriesToSearchButton.addActionListener(e -> {
            FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false, false, true);
            descriptor.setTitle("Select Directories to Search");
            descriptor.setDescription("You can pick multiple directories to search.");
            String projectPath = project.getBasePath();
            VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(new File(projectPath));
            VirtualFile[] vFiles = FileChooser.chooseFiles(descriptor, null, virtualFile);
            FSLog.log.info(String.format("Got stuff: %s", vFiles));
        });

        contentPane.registerKeyboardAction(e -> onCancel(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    protected SearchOptions createSearchOptions(){
        SearchOptions so = new SearchOptions();
        String[] searchPathsArray = new String[searchPaths.size()];
        so.searchPaths = searchPaths.toArray(searchPathsArray);
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

    public void setProject(Project project) {
        this.project = project;
    }
}
