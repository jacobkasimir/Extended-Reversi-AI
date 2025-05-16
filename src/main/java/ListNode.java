public class ListNode {
        private int value;
        private ListNode next;

        ListNode(int value) {
            this.value = value;
        }

        public int getValue(){
            return this.value;
        }

        public ListNode getNext(){
            return this.next;
        }

        public void setNext(ListNode node){
            this.next = node;
        }
    }
