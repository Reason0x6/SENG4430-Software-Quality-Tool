public class MyPolygons {
    private Node sentinel;
    private Node current;
    private int length;

    MyPolygons(){
        Node newNode = new Node();
        sentinel = newNode;
        current = sentinel;
        newNode.setNext(sentinel);
        newNode.setPrevious(sentinel);
        length = 0;
    }

    public void append(Polygon data)
    {
        // add tail Node for empty list (length has the data of 0)
        if (length == 0)
        {
            Node newNode = new Node(data, current, current);
            newNode.getPrevious().setNext(newNode);
            newNode.getNext().setPrevious(newNode);
            length++;
        }
        else
        {
           while(current.getNext() != sentinel){
               this.step();
           }
            Node newNode = new Node(data, sentinel, current);
            newNode.getPrevious().setNext(newNode);
            newNode.getNext().setPrevious(newNode);
            length++;
        }
  
    }

    public void prepend(Polygon data)
    {
        // add tail Node for empty list (length has the data of 0)
        if (length == 0)
        {
            Node newNode = new Node(data, current, current);
            newNode.getPrevious().setNext(newNode);
            newNode.getNext().setPrevious(newNode);
            length++;       									            // Set the length of linked list to 1
        }
        else
        {
             current = sentinel.getNext();
             Node newNode = new Node(data, sentinel, current);
             newNode.getPrevious().setNext(newNode);
             newNode.getNext().setPrevious(newNode);
             length++;
        }
  
    }

    public void insert(Polygon data)
    {
        if (length == 1)
        {
            this.prepend(data);   									        
        }
        else if(current == sentinel){
            this.prepend(data);   		
        }
        else
        {
           Node newNode = new Node(data, current, current.getPrevious());
            newNode.getPrevious().setNext(newNode);
            newNode.getNext().setPrevious(newNode);
            length++;
        }
    }            						

    public Polygon take(){

        Polygon output = sentinel.getNext().getData();
        current = sentinel.getNext();
        Node temp;								// Creates temp node to use for the deleting
        temp = sentinel.getNext();										// Sets temp to head 
        sentinel.setNext(sentinel.getNext().getNext());										
        temp = null;									// Delete the temp node & its data 
        length --;
        return output;	
    }


    public void step(){
         current = current.getNext();
    } 

    public void resetCurrent(){
        current = sentinel.getNext();
    }

    public int getLength(){
        return length;
    }

    public String print(int pos){
        current = sentinel.getNext();
        String output = "";
        if(pos == 0){
            output = current.getData().toString();
            return output;
        }
        else{
            for(int i = 0; i < pos; i++){
                step();
            }

            output = current.getData().toString();
            return output;
        }
    }

    public double getArea(int pos){

        current = sentinel.getNext();
        double output;
        if(pos == 0){
            output = current.getData().getArea();
            return output;
        }
        else{
            for(int i = 0; i < pos; i++){
                step();
            }

            output = current.getData().getArea();
            return output;
        }
    }

    public Polygon getData(int pos){

        current = sentinel.getNext();
        Polygon output;
        if(pos == 0){
            output = current.getData();
            return output;
        }
        else{
            for(int i = 0; i < pos; i++){
                step();
            }

            output = current.getData();
            return output;
        }
    }

    public double getCurrentArea(){
        return current.getData().getArea();
    }

    public double getCurrentVertexDis(){
        return current.getData().getMinVertexDistance();
    }
    
}