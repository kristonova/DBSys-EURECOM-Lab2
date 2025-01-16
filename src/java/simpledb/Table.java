package simpledb;

public class Table {
    public String name;
    public DbFile file;
    public String pkeyField;

    // Constructor
    public Table(DbFile file, String name, String pkeyField) {
        this.file = file;
        this.name = name;
        this.pkeyField = pkeyField;
    }
}