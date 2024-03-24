public class Point {
   private double x;
   private double y;

// Constructors
   Point(){
       x = 0;
       y = 0;
   }
   Point(double xN, double yN){
        x = xN;
        y = yN;
    }

    //Methods
    public String toString(){
        //Formatting x & y to string (sX & sY meaning stringX etc)
        String sX = String.format("%5.2f", x);
        String sY = String.format("%5.2f", y);

        //Concatinating the strings for output
        String output = "( " + sX + " , " + sY + " )";
        return output;
    }
   public double getX(){
      return x;  
   }
   public double getY(){
      return y;  
   }

}
