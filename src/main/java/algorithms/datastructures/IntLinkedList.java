package algorithms.datastructures;
/**
 * A singly linked list optimized for primitive int values.
 * Provides basic operations such as add, remove, contains.
 */
public class IntLinkedList {

    private ListNode head;
    private int size;


    /**
     * @return the first element of the list.
     */
    public ListNode getHead() {
        return head;
    }

    /**
     * Returns the number of elements in the list.
     *
     * @return the size of the list.
     */
    public int getSize() {
        return size;
    }

    /**
     * Adds an element to the beginning of the list.
     *
     * @param value the int value to add.
     */
    public void add(int value) {
        ListNode newNode = new ListNode(value);
        newNode.setNext(head);
        head = newNode;
        size++;
    }

    /**
     * Adds an element to the beginning of the list if the list does not contain it yet.
     *
     * @param value the int value to add.
     */
    public void addIfNotContains(int value) {
        if (!this.contains(value)){                           
            ListNode newNode = new ListNode(value);
            newNode.setNext(head);
            head = newNode;
            size++;
        }
    }

    /**
     * Adds all elements from another IntLinkedList to this list without checking for duplications.
     * Elements will be added in reverse order due to head insertion.
     *
     * @param list the other IntLinkedList to add from.
     */
    public void addAll(IntLinkedList list) {
        if (list == null || list.size == 0) {
            return;
        }
        ListNode temp = list.head;
        while (temp != null) {
            this.add(temp.getValue());
            temp = temp.getNext();
        }
    }

    /**
     * Removes the first occurrence of the given value from the list.
     *
     * @param value the value to remove.
     * @return true if a node was removed, false otherwise.
     */
    public boolean remove(int value) {
        if (head == null) return false;

        if (head.getValue() == value) {
            head = head.getNext();
            size--;
            return true;
        }

        ListNode current = head;
        while (current.getNext() != null) {
            if (current.getNext().getValue() == value) {
                current.setNext(current.getNext().getNext());
                size--;
                return true;
            }
            current = current.getNext();
        }
        return false;
    }

    /**
     * Checks if the list contains a given value.
     *
     * @param value the value to search for.
     * @return true if the value exists in the list, false otherwise.
     */
    public boolean contains(int value) {
        ListNode current = head;
        while (current != null) {
            if (current.getValue() == value) return true;
            current = current.getNext();
        }
        return false;
    }

    /**
     * Removes all elements from the list.
     */
    public void clear() {
        head = null;
        size = 0;
    }


    /**
     * Checks if the list is empty.
     *
     * @return true if the list is empty, false otherwise.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Converts the list to an int array.
     *
     * @return an array containing all list elements in order.
     */
    public int[] toArray() {
        int[] result = new int[size];
        ListNode current = head;
        int i = 0;
        while (current != null) {
            result[i++] = current.getValue();
            current = current.getNext();
        }
        return result;
    }


    /**
     * Checks if this IntLinkedList is equal to another object.
     * Two IntLinkedLists are considered equal if they have the same size and values.
     *
     * @param obj The object to compare with this IntLinkedList.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj){
        if (!(obj instanceof IntLinkedList)) return false;
        IntLinkedList list = (IntLinkedList) obj;

        if(list.size != this.size) return false;
        ListNode temp = this.head;

        while(temp != null){
            if(!list.contains(temp.getValue())) return false;
            temp = temp.getNext();
        }
        return true;
    }
}