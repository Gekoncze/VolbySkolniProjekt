package volby;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class MyComboBoxTableCell<S,T> extends TableCell<S,T> {
    private static final String PLACEHOLDER = "<vyberte hodnotu>";
    
    public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn(
            final T... items) {
        return list -> new MyComboBoxTableCell<S,T>(items);
    }

    public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn(
            final StringConverter<T> converter,
            final T... items) {
        return forTableColumn(converter, FXCollections.observableArrayList(items));
    }

    public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn(
            final ObservableList<T> items) {
        return forTableColumn(null, items);
    }

    public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn(
            final StringConverter<T> converter,
            final ObservableList<T> items) {
        return list -> new MyComboBoxTableCell<S,T>(converter, items);
    }

    private final ObservableList<T> items;

    private ComboBox<T> comboBox;

    public MyComboBoxTableCell() {
        this(FXCollections.<T>observableArrayList());
    }

    public MyComboBoxTableCell(T... items) {
        this(FXCollections.observableArrayList(items));
    }

    public MyComboBoxTableCell(StringConverter<T> converter, T... items) {
        this(converter, FXCollections.observableArrayList(items));
    }

    public MyComboBoxTableCell(ObservableList<T> items) {
        this(null, items);
    }

    public MyComboBoxTableCell(StringConverter<T> converter, ObservableList<T> items) {
        this.getStyleClass().add("combo-box-table-cell");
        this.items = items;
        setConverter(converter != null ? converter : new StringConverter<T>() {
            @Override
            public String toString(T object) {
                return object == null ? PLACEHOLDER : object + "";
            }

            @Override
            public T fromString(String string) {
                throw new UnsupportedOperationException();
            }
        });
    }


    private ObjectProperty<StringConverter<T>> converter =
            new SimpleObjectProperty<StringConverter<T>>(this, "converter");

    public final ObjectProperty<StringConverter<T>> converterProperty() {
        return converter;
    }

    public final void setConverter(StringConverter<T> value) {
        converterProperty().set(value);
    }

    public final StringConverter<T> getConverter() {
        return converterProperty().get();
    }

    private BooleanProperty comboBoxEditable =
            new SimpleBooleanProperty(this, "comboBoxEditable");

    public final BooleanProperty comboBoxEditableProperty() {
        return comboBoxEditable;
    }

    public final void setComboBoxEditable(boolean value) {
        comboBoxEditableProperty().set(value);
    }

    public final boolean isComboBoxEditable() {
        return comboBoxEditableProperty().get();
    }

    public ObservableList<T> getItems() {
        return items;
    }

    @Override
    public void startEdit() {
        if (! isEditable() || ! getTableView().isEditable() || ! getTableColumn().isEditable()) {
            return;
        }

        if (comboBox == null) {
            comboBox = createComboBox(this, items, converterProperty());
            comboBox.editableProperty().bind(comboBoxEditableProperty());
            comboBox.setPromptText(PLACEHOLDER);
        }

        comboBox.getSelectionModel().select(getItem());

        super.startEdit();
        setText(null);
        setGraphic(comboBox);
    }

    /** {@inheritDoc} */
    @Override public void cancelEdit() {
        super.cancelEdit();

        setText(getItem() == null ? PLACEHOLDER : getItem() + "");
        setGraphic(null);
    }

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        updateItem(this, getConverter(), null, null, comboBox);
    }
    
    static <T> ComboBox<T> createComboBox(final Cell<T> cell,
                                          final ObservableList<T> items,
                                          final ObjectProperty<StringConverter<T>> converter) {
        ComboBox<T> comboBox = new ComboBox<T>(items);
        comboBox.converterProperty().bind(converter);
        comboBox.setMaxWidth(Double.MAX_VALUE);
        comboBox.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            if (cell.isEditing()) {
                cell.commitEdit(newValue);
            }
        });
        return comboBox;
    }
    
    static <T> void updateItem(final Cell<T> cell,
                               final StringConverter<T> converter,
                               final HBox hbox,
                               final Node graphic,
                               final ComboBox<T> comboBox) {
        if (cell.isEmpty()) {
            cell.setText(null);
            cell.setGraphic(null);
        } else {
            if (cell.isEditing()) {
                if (comboBox != null) {
                    comboBox.getSelectionModel().select(cell.getItem());
                }
                cell.setText(null);

                if (graphic != null) {
                    hbox.getChildren().setAll(graphic, comboBox);
                    cell.setGraphic(hbox);
                } else {
                    cell.setGraphic(comboBox);
                }
            } else {
                cell.setText(getItemText(cell, converter));
                cell.setGraphic(graphic);
            }
        }
    };
    
    private static <T> String getItemText(Cell<T> cell, StringConverter<T> converter) {
        return converter == null ?
            cell.getItem() == null ? "" : cell.getItem().toString() :
            converter.toString(cell.getItem());
    }

    public ComboBox<T> getComboBox() {
        return comboBox;
    }
}
