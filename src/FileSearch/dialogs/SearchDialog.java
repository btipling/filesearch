package FileSearch.dialogs;

import FileSearch.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

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
    private JList<String> resultsList;
    private JList<String> searchPathList;
    private JLabel statusLabel;
    private JButton stopButton;
    private SearchManager searchManager;
    private DefaultListModel<String> searchPathModel = new DefaultListModel<>();
    private ResultsListModel resultsListModel = new ResultsListModel();
    private Search currentSearch = null;

    public SearchDialog(final SearchManager searchManager) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        searchPathList.setModel(searchPathModel);
        resultsList.setModel(resultsListModel);
        this.searchManager = searchManager;

        buttonOK.addActionListener(e -> {
            onOK();
        });

        buttonCancel.addActionListener(e -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        stopButton.addActionListener(e -> stopSearching());

        searchPathList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    searchPathModel.remove(searchPathList.locationToIndex(e.getPoint()));
                }
            }
        });

        selectDirectoriesToSearchButton.addActionListener(e -> {
            FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false, false, true);
            descriptor.setTitle("Select Directories to Search");
            descriptor.setDescription("You can pick multiple directories to search.");
            String projectPath = project.getBasePath();
            VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(new File(projectPath));
            VirtualFile[] vFiles = FileChooser.chooseFiles(descriptor, null, virtualFile);
            FSLog.log.info(String.format("Got stuff: %s", vFiles.toString()));
            for (VirtualFile file : vFiles) {
                searchPathModel.addElement(file.getPath());
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    protected SearchOptions createSearchOptions(){
        SearchOptions so = new SearchOptions();
        String[] sp = new String[searchPathModel.size()];
        Object[] r = searchPathModel.toArray();
        for (int i = 0; i < r.length; i++) {
            sp[i] = (String)r[i];
        }
        so.searchPaths = sp;
        so.searchString = textField1.getText();
        so.caseSensitive = caseCB.isSelected();
        so.regex = regexCB.isSelected();
        so.wholePath = fullPathRadioButton.isSelected();
        so.recursive = recursiveCB.isSelected();
        return so;
    }

    private void onOK() {
        Search search = new Search(createSearchOptions());
        currentSearch = search;
        resultsListModel.update(search.getResults());
        AtomicInteger i = new AtomicInteger(0);
        search.addListener(new SearchResultListener() {
            @Override
            public void onFinishedResults(Search search) {
                ApplicationManager.getApplication().invokeLater(() -> statusLabel.setText("Finished."));
            }

            @Override
            public void onReceivedResult(Search search, Result result) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    statusLabel.setText(String.format("Found %s.", result.toString()));
                    resultsListModel.addedResults(1);
                    int lastIndex = resultsList.getModel().getSize() - 1;
                    if (lastIndex >= 0) {
                        resultsList.ensureIndexIsVisible(lastIndex);
                    }
                });
            }

            @Override
            public void onStatusUpdate(String status) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    int cur = i.addAndGet(1);
                    if (cur == 1 || cur % 10000 == 0) {
                        FSLog.log.info(String.format("Setting text at %d", cur));
                        statusLabel.setText(status);
                    }
                });
            }
        });
        stopButton.setEnabled(true);
        buttonOK.setEnabled(false);
        searchManager.execute(search);
    }

    private void resetUI() {
        stopButton.setEnabled(false);
        buttonOK.setEnabled(true);
    }

    private void stopSearching() {
        if (currentSearch != null) {
            searchManager.cancel(currentSearch);
        }
        statusLabel.setText("Stopped search.");
        resetUI();
    }

    private void onCancel() {
        stopSearching();
        dispose();
    }

    public void setProject(Project project) {
        this.project = project;
        if (searchPathModel.isEmpty()) {
            searchPathModel.addElement(project.getBasePath());
        }
    }
}
