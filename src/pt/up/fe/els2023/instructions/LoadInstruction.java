package pt.up.fe.els2023.instructions;

import pt.up.fe.els2023.load.JSONLoader;
import pt.up.fe.els2023.load.XMLLoader;
import pt.up.fe.els2023.load.YamlLoader;
import pt.up.fe.els2023.model.DataContext;
import pt.up.fe.els2023.model.table.Table;
import pt.up.fe.els2023.utils.FileUtils;

import java.io.File;
import java.util.Map;

import static java.lang.System.exit;

public class LoadInstruction implements Instruction {
    private final DataContext dataContext;
    private final File file;

    public LoadInstruction(DataContext dataContext, String filePath) {
        this.dataContext = dataContext;
        this.file = new File(filePath);
    }

    @Override
    public void execute() {
        FileUtils.FileTypes fileType = FileUtils.getFileType(file);

        Map<String, Object> contents = switch (fileType) {
            case YAML -> new YamlLoader().load(file);
            case XML -> new XMLLoader().load(file);
            case JSON -> new JSONLoader().load(file);
            // TODO: Add more cases
        };

        Table table = Table.fromContents(contents);

        dataContext.addTable(file.getName(), table);
    }
}
