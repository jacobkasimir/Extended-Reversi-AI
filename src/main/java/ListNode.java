/**
 * Represents a node in a linked list data structure.
 * Each node contains a value and a reference to the next node.
 */
public class ListNode {
    /** The value stored in this node */
    private int value;
    
    /** Reference to the next node in the linked list */
    private ListNode next;

    /**
     * Constructs a new node with the specified value.
     *
     * @param value the integer value to be stored in this node
     */
    ListNode(int value) {
        this.value = value;
    }

    /**
     * Returns the value stored in this node.
     *
     * @return the integer value stored in this node
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Returns the next node in the linked list.
     *
     * @return the next ListNode, or null if this is the last node
     */
    public ListNode getNext() {
        return this.next;
    }

    /**
     * Sets the reference to the next node in the linked list.
     *
     * @param node the ListNode to set as the next node
     */
    public void setNext(ListNode node) {
        this.next = node;
    }
}