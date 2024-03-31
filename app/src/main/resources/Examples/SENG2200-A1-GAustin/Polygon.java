public class Polygon {

    private int vertexNum;
    private Point[] polyPoints;
    private int savedPoints = 0;


    Polygon(int num){
        vertexNum = num;
        polyPoints = new Point[vertexNum+1];  
    }

    public double getVertexNum(){
        return vertexNum;
    }

    public void addPoint(Point newPoint){
        int test = 0;
        polyPoints[savedPoints] = newPoint;
        savedPoints++;
    }
  
    public double getArea(){
        double area = 0;
        for(int i = 0; i < vertexNum - 1; i++){
          area += (polyPoints[i].getX() + polyPoints[i+1].getX()) * (polyPoints[i+1].getY() - polyPoints[i].getY());
        }

        area = 0.5 * Math.abs(area);
           return area;
    }

    public String toString(){ 
        String output = "";
        for(int i = 0; i < vertexNum; i++){
            output += polyPoints[i].toString();
        }
        output += ": Area: " + String.format("%5.2f", this.getArea());
        return output;
    }

    public double getVertexDistance(double x, double y){
        double distance = x*x + y*y;
        distance = Math.sqrt(distance);
        return distance;
    }

    public double getMinVertexDistance(){
        double minDistance = this.getVertexDistance(polyPoints[0].getX(), polyPoints[0].getY());

        for(int i = 1; i < vertexNum - 1; i++){
            if( getVertexDistance(polyPoints[i].getX(), polyPoints[i].getY()) < minDistance){
            minDistance = this.getVertexDistance(polyPoints[i].getX(), polyPoints[i].getY());
         }

        }

        return minDistance;
    }

}
