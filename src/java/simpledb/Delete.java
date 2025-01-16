package simpledb;

import java.io.IOException;

/**
 * The delete operator. Delete reads tuples from its child operator and removes
 * them from the table they belong to.
 */
public class Delete extends Operator {

    private static final long serialVersionUID = 1L;
    private final TransactionId transactionId;
    private OpIterator child;
    private boolean deleted;
    private final TupleDesc tupleDesc;

    /**
     * Constructor specifying the transaction that this delete belongs to as
     * well as the child to read from.
     * 
     * @param t
     *            The transaction this delete runs in
     * @param child
     *            The child operator from which to read tuples for deletion
     */
    public Delete(TransactionId t, OpIterator child) {
        // some code goes here
        this.transactionId = t;
        this.child = child;
        this.deleted = false;

        this.tupleDesc = new TupleDesc(new Type[]{Type.INT_TYPE}, new String[]{"Delete Count"});
    }

    public TupleDesc getTupleDesc() {
        // some code goes here
        return tupleDesc;
    }

    public void open() throws DbException, TransactionAbortedException {
        // some code goes here
        super.open();
        child.open();
        deleted = false;
    }

    public void close() {
        // some code goes here
        super.close();
        child.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
        child.rewind();
        deleted = false;
    }

    /**
     * Deletes tuples as they are read from the child operator. Deletes are
     * processed via the buffer pool (which can be accessed via the
     * Database.getBufferPool() method.
     * 
     * @return A 1-field tuple containing the number of deleted records.
     * @see Database#getBufferPool
     * @see BufferPool#deleteTuple
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        // some code goes here
        if (deleted) {
            return null;
        }

        BufferPool bufferPool = Database.getBufferPool();
        int counter = 0;

        try {
            while (child.hasNext()) {
                Tuple tuple = child.next();
                bufferPool.deleteTuple(transactionId, tuple);
                counter++;
            }
        } catch (IOException e) {
            throw new DbException("Error while deleting tuples: " + e.getMessage());
        }

        deleted = true;

        // Create a result tuple with the count of deleted records
        Tuple result = new Tuple(tupleDesc);
        result.setField(0, new IntField(counter));
        return result;
    }

    @Override
    public OpIterator[] getChildren() {
        // some code goes here
        return new OpIterator[]{child};
    }

    @Override
    public void setChildren(OpIterator[] children) {
        // some code goes here
        if (children.length != 1) {
            throw new IllegalArgumentException("Expected exactly one child.");
        }
        this.child = children[0];
    }

}
