package net.msembodo.datetounixtime;

import com.opencsv.CSVReader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.msembodo.ascii.AsciiFileFX;
import org.tbee.javafx.scene.layout.MigPane;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Main form class.
 */
public class MainForm extends Application {
    MigPane root;
    private List<String[]> ascii;
    private File inputFile;
    private String timeFormat;
    private StringBuilder outAscBuilder;
    private TableView tableView;
    private TableView tableViewOutput;
    private Label statusText;

    @Override
    public void start(Stage stage) {
        initUI(stage);
    }

    private void initUI(Stage stage) {
        root = new MigPane("insets 4", "[grow]", "[grow][grow][]");
        Scene scene = new Scene(root);

        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(stage.widthProperty());

        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");
        Menu helpMenu = new Menu("Help");
        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);

        Menu importMenu = new Menu("Import..");

        MenuItem openMenuItem = new MenuItem("Open");
        MenuItem saveMenuItem = new MenuItem("Save");
        MenuItem convertMenuItem = new MenuItem("Convert");
        MenuItem exitMenuItem = new MenuItem("Exit");
        fileMenu.getItems().addAll(openMenuItem, importMenu, convertMenuItem, saveMenuItem,
                new SeparatorMenuItem(), exitMenuItem);

        MenuItem csvMenuItem = new MenuItem("from CSV");
        importMenu.getItems().addAll(csvMenuItem);

        MenuItem formatMenuItem = new MenuItem("Time format");
        editMenu.getItems().addAll(formatMenuItem);

        MenuItem aboutMenuItem = new MenuItem("About");
        helpMenu.getItems().addAll(aboutMenuItem);

        tableView = new TableView();
        tableViewOutput = new TableView();

        timeFormat = "dd-MM-yyyy HH:mm:ss";

        statusText = new Label("Load file to convert");

        openMenuItem.setOnAction((ActionEvent event) -> {

            ObservableList<ObservableList> data = FXCollections.observableArrayList();
            data.removeAll();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select ASCII file");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ASCII file", "*.asc", "*.ASC"));
            inputFile = fileChooser.showOpenDialog(stage);
            stage.setTitle(inputFile.getAbsolutePath() + " - Date To Unix Time");

            try {
                CSVReader reader = new CSVReader(new FileReader(inputFile.getAbsolutePath()), '\t');
                ascii = reader.readAll();
            }
            catch (FileNotFoundException e) {}
            catch (IOException e) {}

            List<String> columns = new ArrayList<String>();

            for (String col : ascii.get(0))
                columns.add(col);

            TableColumn[] tableColumns = new TableColumn[columns.size()];
            int columnIndex = 0;
            for (String columnName : columns) {
                final int j = columnIndex;
                tableColumns[columnIndex] = new TableColumn(columnName + "\n" + ascii.get(1)[columnIndex]);
                tableColumns[columnIndex].setPrefWidth(150.0);
                tableColumns[columnIndex].setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                columnIndex++;
            }
            tableView.getColumns().addAll(tableColumns);

            ascii.remove(0);
            ascii.remove(0);

            for (String[] rec : ascii) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 0; i < rec.length; i++)
                    row.add(rec[i]);

                data.add(row);
            }

            tableView.setItems(data);

            statusText.setText("File > Convert");
        });

        csvMenuItem.setOnAction((ActionEvent event) -> {
            ObservableList<ObservableList> data = FXCollections.observableArrayList();
            data.removeAll();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select CSV file");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV file", "*.csv", "*.CSV"));
            inputFile = fileChooser.showOpenDialog(stage);
            stage.setTitle(inputFile.getAbsolutePath() + " - Date To Unix Time");

            try {
                CSVReader reader = new CSVReader(new FileReader(inputFile.getAbsolutePath()));
                ascii = reader.readAll();
            }
            catch (FileNotFoundException e) {}
            catch (IOException e) {}

            List<String> columns = new ArrayList<String>();

            for (String col : ascii.get(0))
                columns.add(col);

            TableColumn[] tableColumns = new TableColumn[columns.size()];
            int columnIndex = 0;
            for (String columnName : columns) {
                final int j = columnIndex;
                tableColumns[columnIndex] = new TableColumn(columnName + "\n" + ascii.get(1)[columnIndex]);
                tableColumns[columnIndex].setPrefWidth(150.0);
                tableColumns[columnIndex].setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                columnIndex++;
            }
            tableView.getColumns().addAll(tableColumns);

            ascii.remove(0);
            ascii.remove(0);

            for (String[] rec : ascii) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 0; i < rec.length; i++)
                    row.add(rec[i]);

                data.add(row);
            }

            tableView.setItems(data);

            statusText.setText("File > Convert");
        });

        convertMenuItem.setOnAction((ActionEvent event) -> {
            ObservableList<ObservableList> data = FXCollections.observableArrayList();

            AsciiFileFX asc = new AsciiFileFX(inputFile.getAbsolutePath());
            asc.removeHeader();
            outAscBuilder = asc.convertDateTime(timeFormat);

            List<String[]> outAsc = new ArrayList<String[]>();
            String[] lines = outAscBuilder.toString().split("[\n]");
            for (String line : lines) {
                String[] tokens = line.split("[\t]");
                outAsc.add(tokens);
            }

            List<String> columns = new ArrayList<String>();

            for (String col : outAsc.get(0))
                    columns.add(col);

            TableColumn[] tableColumns = new TableColumn[columns.size()];
            int columnIndex = 0;
            for (String columnName : columns) {
                final int j = columnIndex;
                tableColumns[columnIndex] = new TableColumn(columnName + "\n" + outAsc.get(1)[columnIndex]);
                tableColumns[columnIndex].setPrefWidth(150.0);
                tableColumns[columnIndex].setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                columnIndex++;
            }
            tableViewOutput.getColumns().addAll(tableColumns);

            outAsc.remove(0);
            outAsc.remove(0);

            for (String[] rec : outAsc) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 0; i < rec.length; i++)
                    row.add(rec[i]);

                data.add(row);
            }

            tableViewOutput.setItems(data);

            statusText.setText("ASCII converted. File > Save");
        });

        saveMenuItem.setOnAction((ActionEvent event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save ASCII file");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ASCII file", "*.asc", "*.ASC"));
            fileChooser.setInitialFileName("out.asc");
            File savedFile = fileChooser.showSaveDialog(stage);

            if (savedFile != null) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(savedFile))) {
                    writer.append(outAscBuilder);
                    statusText.setText("Output file saved to " + savedFile.getAbsolutePath());
                }
                catch (IOException e) {
                    e.printStackTrace();
                    statusText.setText("An ERROR occured while saving the file!");
                }
            }
            else
                statusText.setText("File save cancelled.");
        });

        formatMenuItem.setOnAction((ActionEvent event) -> {
            TextInputDialog fmtDialog = new TextInputDialog(timeFormat);
            fmtDialog.setTitle("Time format");
            fmtDialog.setHeaderText("Edit input time format");
            fmtDialog.setContentText("Please enter time format:");

            Optional<String> result = fmtDialog.showAndWait();
            if (result.isPresent())
                timeFormat = result.get();
        });

        aboutMenuItem.setOnAction((ActionEvent event) -> {
            Alert about = new Alert(Alert.AlertType.INFORMATION);
            about.setTitle("About");
            about.setHeaderText("Date To Unix Time");
            about.setContentText("Written by Martyono Sembodo\n(martyono.sembodo@gmail.com)");

            about.showAndWait();
        });

        exitMenuItem.setOnAction((ActionEvent event) -> {
            Platform.exit();
        });

        createLayout(menuBar, tableView, tableViewOutput, statusText);

        stage.setTitle("Date To Unix Time");
        stage.setScene(scene);
        stage.show();
    }

    private void createLayout(Control...arg) {
        root.add(arg[0], "north");
        root.add(arg[1], "w 1000, h 400, grow, wrap");
        root.add(arg[2], "w 1000, h 400, grow, wrap");
        root.add(arg[3]);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
