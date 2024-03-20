public class Node {
    private Node previous;
    private Node next;
    private Polygon data;

    //Constructors
    Node(){
        previous = null;
        next = null;
        data = null;
    }

    Node(Polygon nData){
        previous = null;
        next = null;
        data = nData;
    }

    public Node(Polygon data, Node next, Node prev) {
        this.next = next;
        this.previous = prev;
        this.data = data;
    }

 
    //Methods - Getters
    public Node getPrevious(){
        return previous;
    }
    public Node getNext(){
        return next;
    }
    public Polygon getData(){
        return data;
    }

    //Methods - Setters
    public void setPrevious(Node nPrevious){
        previous = nPrevious;
    }
    public void setNext(Node nNext){
        next = nNext;
    }
    public void setData(Polygon nData){
        data = nData;
    }
    
}