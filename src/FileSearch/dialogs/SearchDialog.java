package FileSearch.dialogs;

import FileSearch.*;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SearchDialog extends JDialog {
    private Project project;
    private JPanel contentPane;
    private JButton searchButton;
    private JButton closeButton;
    private JTextField searchInput;
    private JCheckBox caseCB;
    private JCheckBox regexCB;
    private JRadioButton fullPathRadioButton;
    private JButton selectDirectoriesToSearchButton;
    private JCheckBox recursiveCB;
    private JList<String> resultsList;
    private JList<String> searchPathList;
    private JLabel statusLabel;
    private JButton stopButton;
    private JButton clearBtn;
    private JCheckBox hiddenDirsCB;
    private JRadioButton exactFileNameRadioButton;
    private JRadioButton fileNameRadioButton;
    private SearchManager searchManager;
    private DefaultListModel<String> searchPathModel = new DefaultListModel<>();
    private ResultsListModel resultsListModel = new ResultsListModel();
    private Search currentSearch = null;
    private JPopupMenu popupMenu;

    private interface ResultMenuItemListener {
        void handleAction(String path);
    }

    public SearchDialog(final SearchManager searchManager) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(searchButton);
        searchPathList.setModel(searchPathModel);
        resultsList.setModel(resultsListModel);
        this.searchManager = searchManager;

        searchButton.addActionListener(e -> onSearch());
        closeButton.addActionListener(e -> onCancel());
        stopButton.addActionListener(e -> stopSearching());
        clearBtn.addActionListener(e -> {
            if (currentSearch != null) {
                currentSearch.clear();
                statusLabel.setText("");
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        searchPathList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    searchPathModel.remove(searchPathList.locationToIndex(e.getPoint()));
                }
                toggleSearchButton();
            }
        });
        resultsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    String path = resultsListModel.getElementAt(resultsList.locationToIndex(e.getPoint()));
                    FileUtils.openFile(path);
                }
            }
        });
        popupMenu = new ResultPopupMenu(resultsList);

        ApplicationInfo instance = ApplicationInfo.getInstance();
        popupMenu.add(createResultsListMenuItem("Open File", FileUtils::openFile));
        String miText = String.format("Open File with %s", instance.getVersionName());
        popupMenu.add(createResultsListMenuItem(miText, path -> FileUtils.openFileWithIDEA(path, project)));
        popupMenu.add(createResultsListMenuItem("Open Containing Folder", FileUtils::openFolder));
        popupMenu.add(createResultsListMenuItem("Copy File Path to Clipboard", path -> FileUtils.copyPath(path, project)));
        popupMenu.add(createResultsListMenuItem("Copy Containing Folder Path to Clipboard", path -> FileUtils.copyFolderPath(path, project)));
        resultsList.setComponentPopupMenu(popupMenu);

        searchInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {
                toggleSearchButton();
            }
        });
        ActionListener actionListener = e -> {
            regexCB.setEnabled(!exactFileNameRadioButton.isSelected());
        };
        exactFileNameRadioButton.addActionListener(actionListener);
        fileNameRadioButton.addActionListener(actionListener);
        fullPathRadioButton.addActionListener(actionListener);

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
            toggleSearchButton();
        });

        contentPane.registerKeyboardAction(e -> onCancel(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private JMenuItem createResultsListMenuItem(String text, ResultMenuItemListener listener) {
        JMenuItem mi = new JMenuItem();
        mi.setText(text);
        mi.addActionListener(e -> listener.handleAction(resultsList.getSelectedValue()));
        return mi;
    }

    private SearchOptions createSearchOptions(String searchString){
        SearchOptions so = new SearchOptions();
        String[] sp = new String[searchPathModel.size()];
        Object[] r = searchPathModel.toArray();
        for (int i = 0; i < r.length; i++) {
            sp[i] = (String)r[i];
        }
        so.searchPaths = sp;
        so.searchString = searchString;
        so.caseSensitive = caseCB.isSelected();
        so.regex = regexCB.isSelected();
        so.match = SearchOptions.MatchOption.MATCH_FILE;
        if (fullPathRadioButton.isSelected()) {
            so.match = SearchOptions.MatchOption.MATCH_PATH;
        } else if (exactFileNameRadioButton.isSelected()) {
            so.match = SearchOptions.MatchOption.EXACT_FILE;
        }
        so.recursive = recursiveCB.isSelected();
        so.searchHiddenDirs = hiddenDirsCB.isSelected();
        return so;
    }

    private void onSearch() {
        String searchString = searchInput.getText();
        if (searchString.isEmpty() || searchPathModel.isEmpty()) {
            return;
        }
        Search search = new Search(createSearchOptions(searchString));
        currentSearch = search;
        resultsListModel.update(search.getResults());
        statusLabel.setText("");
        clearBtn.setEnabled(false);
        AtomicInteger i = new AtomicInteger(0);
        search.addListener(new SearchResultListener() {
            @Override
            public void onFinishedResults(Search search) {
                setFinalStatus("Finished");
                resetUI();
            }

            @Override
            public void onReceivedResult(Search search, Result result) {
                setSearchStatus(String.format("Found %s.", result.toString()));
                ApplicationManager.getApplication().invokeLater(() -> {
                    if (!clearBtn.isEnabled()) {
                        clearBtn.setEnabled(true);
                    }
                    resultsListModel.addedResults(1);
                    int lastIndex = resultsList.getModel().getSize() - 1;
                    if (lastIndex >= 0) {
                        resultsList.ensureIndexIsVisible(lastIndex);
                    }
                });
            }

            @Override
            public void onStatusUpdate(String status) {
                int updateFreq = 5000;
                int cur = i.addAndGet(1);
                if (cur == 1 || cur % updateFreq == 0) {
                    setSearchStatus(status);
                }
            }

            @Override
            public void onResultsUpdate(Search search) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    List<Result> r = search.getResults();
                    resultsListModel.update(search.getResults());
                    if (r.size() == 0 && clearBtn.isEnabled()) {
                        clearBtn.setEnabled(false);
                    }
                });
            }
        });
        stopButton.setEnabled(true);
        searchButton.setEnabled(false);
        searchManager.execute(search);
    }

    private void toggleSearchButton() {
        searchButton.setEnabled(searchInput.getText().length() > 0 && !searchPathModel.isEmpty());
    }

    private void setSearchStatus(String status) {
        int n = currentSearch.getResults().size();
        String s = n == 1 ? "" : "s";
        ApplicationManager.getApplication().invokeLater(() -> statusLabel.setText(String.format("%d result%s. %s", n, s, status)));
    }

    private void setFinalStatus(String status) {
        ApplicationManager.getApplication().invokeLater(() -> {
            if (currentSearch == null || currentSearch.getResults() == null) {
                statusLabel.setText("");
                return;
            }
            int numResults = currentSearch.getResults().size();
            if (numResults == 1) {
                statusLabel.setText(String.format("%s. Found 1 result.", status));
            } else {
                statusLabel.setText(String.format("%s. Found %d results.", status, numResults));
            }
        });
    }

    private void resetUI() {
        ApplicationManager.getApplication().invokeLater(() -> {
            stopButton.setEnabled(false);
            searchButton.setEnabled(true);
        });
    }

    private void stopSearching() {
        if (currentSearch != null) {
            searchManager.cancel(currentSearch);
        }
        setFinalStatus("Stopped search");
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
