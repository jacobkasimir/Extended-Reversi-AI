public class IntLinkedList {

    private ListNode head;
    private int size;

    // Fügt ein Element am Anfang hinzu
    public void add(int value) {
        ListNode newNode = new ListNode(value);
        if (head == null) {
            head = newNode;
        } else {
            newNode.setNext(head);
            head = newNode;
        }
        size++;
    }
    
    public void addAll(IntLinkedList list){
        if(list == null || list.size == 0) {
            return;
        }
        ListNode temp = list.head;
        while (temp != null) { 
            this.add(temp.getValue());
            temp = temp.getNext();
        }

    }

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

    public boolean contains(int value) {
        ListNode current = head;
        while (current != null) {
            if (current.getValue() == value) return true;
            current = current.getNext();
        }
        return false;
    }

    public void clear(){
        this.head = null;
        this.size = 0;
    }

    // Gibt die Größe zurück
    public int getSize() {
        return size;
    }

    // Prüft, ob die Liste leer ist
    public boolean isEmpty() {
        return size == 0;
    }

    public ListNode getHead(){
        return this.head;
    }

    // Gibt alle Werte als Array zurück (optional, zum Debuggen)
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
}

