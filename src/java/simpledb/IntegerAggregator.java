package simpledb;

import java.util.*;

/**
 * Knows how to compute some aggregate over a set of IntFields.
 */
public class IntegerAggregator implements Aggregator {

    private static final long serialVersionUID = 1L;

    private int gbfield;
    private Type gbfieldtype;
    private int afield;
    private Op what;
    private Map<Field, Integer> aggregateValues;
    private Map<Field, Integer> counts;

    /**
     * Aggregate constructor
     * 
     * @param gbfield
     *            the 0-based index of the group-by field in the tuple, or
     *            NO_GROUPING if there is no grouping
     * @param gbfieldtype
     *            the type of the group by field (e.g., Type.INT_TYPE), or null
     *            if there is no grouping
     * @param afield
     *            the 0-based index of the aggregate field in the tuple
     * @param what
     *            the aggregation operator
     */
    public IntegerAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        this.gbfield = gbfield;
        this.gbfieldtype = gbfieldtype;
        this.afield = afield;
        this.what = what;
        this.aggregateValues = new HashMap<>();
        this.counts = new HashMap<>();
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the
     * constructor
     * 
     * @param tup
     *            the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        Field groupField = gbfield == NO_GROUPING ? null : tup.getField(gbfield);
        IntField aggregateField = (IntField) tup.getField(afield);

        if (!aggregateValues.containsKey(groupField)) {
            aggregateValues.put(groupField, 0);
            counts.put(groupField, 0);
        }

        int newValue = aggregateField.getValue();
        int oldValue = aggregateValues.get(groupField);
        int count = counts.get(groupField);

        switch (what) {
            case SUM:
                aggregateValues.put(groupField, oldValue + newValue);
                break;
            case MIN:
                aggregateValues.put(groupField, count == 0 ? newValue : Math.min(oldValue, newValue));
                break;
            case MAX:
                aggregateValues.put(groupField, count == 0 ? newValue : Math.max(oldValue, newValue));
                break;
            case AVG:
                aggregateValues.put(groupField, oldValue + newValue);
                counts.put(groupField, count + 1);
                break;
            case COUNT:
                aggregateValues.put(groupField, oldValue + 1);
                break;
            default:
                throw new IllegalArgumentException("Unsupported aggregation operator");
        }

        if (what != Op.AVG) {
            counts.put(groupField, count + 1);
        }
    }

    /**
     * Create a OpIterator over group aggregate results.
     * 
     * @return a OpIterator whose tuples are the pair (groupVal, aggregateVal)
     *         if using group, or a single (aggregateVal) if no grouping. The
     *         aggregateVal is determined by the type of aggregate specified in
     *         the constructor.
     */
    public OpIterator iterator() {
        List<Tuple> tuples = new ArrayList<>();
        TupleDesc td = gbfield == NO_GROUPING
                ? new TupleDesc(new Type[] { Type.INT_TYPE })
                : new TupleDesc(new Type[] { gbfieldtype, Type.INT_TYPE });

        for (Map.Entry<Field, Integer> entry : aggregateValues.entrySet()) {
            Field groupField = entry.getKey();
            int aggregateValue = entry.getValue();

            if (what == Op.AVG) {
                aggregateValue /= counts.get(groupField);
            }

            Tuple tuple = new Tuple(td);
            if (gbfield == NO_GROUPING) {
                tuple.setField(0, new IntField(aggregateValue));
            } else {
                tuple.setField(0, groupField);
                tuple.setField(1, new IntField(aggregateValue));
            }
            tuples.add(tuple);
        }

        return new TupleIterator(td, tuples);
    }

}
