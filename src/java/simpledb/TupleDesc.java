package simpledb;

import java.io.Serializable;
import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<TDItem> tdItems;

    /**
     * A help class to facilitate organizing the information of each field
     * */
    public static class TDItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * The type of the field
         * */
        public final Type fieldType;

        /**
         * The name of the field
         * */
        public final String fieldName;

        public TDItem(Type t, String n) {
            this.fieldName = n;
            this.fieldType = t;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TDItem tdItem = (TDItem) o;
            return fieldType == tdItem.fieldType &&
                   Objects.equals(fieldName, tdItem.fieldName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fieldType, fieldName);
        }

        public String toString() {
            return fieldName + "(" + fieldType + ")";
        }
    }

    public TupleDesc(Type[] typeAr, String[] fieldAr) {
        tdItems = new ArrayList<>();
        for (int i = 0; i < typeAr.length; i++) {
            tdItems.add(new TDItem(typeAr[i], fieldAr[i]));
        }
    }

    public TupleDesc(Type[] typeAr) {
        this(typeAr, new String[typeAr.length]);
    }

    /**
     * @return
     *        An iterator which iterates over all the field TDItems
     *        that are included in this TupleDesc
     * */
    public Iterator<TDItem> iterator() {
        return tdItems.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TupleDesc tupleDesc = (TupleDesc) o;
        return tdItems.equals(tupleDesc.tdItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tdItems);
    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
        return tdItems.size();
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     *
     * @param i
     *            index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
        if (i < 0 || i >= tdItems.size()) {
            throw new NoSuchElementException("Index out of bounds");
        }
        return tdItems.get(i).fieldName;
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     *
     * @param i
     *            The index of the field to get the type of. It must be a valid
     *            index.
     * @return the type of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public Type getFieldType(int i) throws NoSuchElementException {
        if (i < 0 || i >= tdItems.size()) {
            throw new NoSuchElementException("Index out of bounds");
        }
        return tdItems.get(i).fieldType;
    }

    /**
     * Find the index of the field with a given name.
     *
     * @param name
     *            name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException
     *             if no field with a matching name is found.
     */
    public int fieldNameToIndex(String name) throws NoSuchElementException {
        if (name == null) {
            throw new NoSuchElementException("Field name is null");
        }
        for (int i = 0; i < tdItems.size(); i++) {
            if (name.equals(tdItems.get(i).fieldName)) {
                return i;
            }
        }
        throw new NoSuchElementException("Field name not found");
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     *         Note that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() {
        int size = 0;
        for (TDItem item : tdItems) {
            size += item.fieldType.getLen();
        }
        return size;
    }

    /**
     * Merge two TupleDescs into one, with td1.numFields + td2.numFields fields,
     * with the first td1.numFields coming from td1 and the remaining from td2.
     *
     * @param td1
     *            The TupleDesc with the first fields of the new TupleDesc
     * @param td2
     *            The TupleDesc with the last fields of the TupleDesc
     * @return the new TupleDesc
     */
    public static TupleDesc merge(TupleDesc td1, TupleDesc td2) {
        int totalFields = td1.numFields() + td2.numFields();
        Type[] typeAr = new Type[totalFields];
        String[] fieldAr = new String[totalFields];

        int i = 0;
        for (; i < td1.numFields(); i++) {
            typeAr[i] = td1.getFieldType(i);
            fieldAr[i] = td1.getFieldName(i);
        }
        for (int j = 0; j < td2.numFields(); j++, i++) {
            typeAr[i] = td2.getFieldType(j);
            fieldAr[i] = td2.getFieldName(j);
        }

        return new TupleDesc(typeAr, fieldAr);
    }

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although
     * the exact format does not matter.
     *
     * @return String describing this descriptor.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tdItems.size(); i++) {
            sb.append(tdItems.get(i).fieldType).append("(").append(tdItems.get(i).fieldName).append(")");
            if (i < tdItems.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}