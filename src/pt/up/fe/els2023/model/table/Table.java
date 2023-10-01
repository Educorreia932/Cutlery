package pt.up.fe.els2023.model.table;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.ListOrderedMap;

public class Table {
    private String name;
    private final ListOrderedMap<String, Column> columns = new ListOrderedMap<>();

    public Table(String name) {
        this.name = name;
    }
    
    public Table(String name, String... headers) {
        this.name = name;
        
        for (String header : headers) {
            if (header != null) {
                Column column = new Column(header);

                columns.put(column.getHeader(), column);
            }

            else {
                throw new IllegalArgumentException("Key must not be null.");
            }
        }
    }

    public List<Object> getRow(int index) {
        return columns.values().stream()
            .map(column -> column.getElement(index))
            .collect(Collectors.toList());
    }

    public Column getColumn(String header) {
        return columns.get(header);
    }

    public Column getColumn(int index) {
        return columns.getValue(index);
    }

    public void addRow(Object... row) {
        for (int i = 0; i < columns.size(); i++) {
            Column column = getColumn(i);

            column.addElement(row[i]);
        }
    }

    public void addColumn(String header, Object... elements) {
        Column column = new Column(header, elements);

        columns.put(header, column);
    }

    public void removeRow(int index) {
        columns.forEach((key, column) -> {
            column.removeElement(index);
        });
    }

    public void removeColumn(String key) {
        columns.remove(key);
    }

    public int numRows() {
        return getColumn(0).numElements();
    }

    public int numColumns() {
        return columns.size();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
