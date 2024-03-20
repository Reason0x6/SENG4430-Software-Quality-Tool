import java.util.*;
import java.io.*;

public class PA1 {

    public static void main(String[] args) {
        
        //File Variable initalisation
        String read = "";
        String start = "";

        //Read From File
        String fileName = args[0];

        try{ 
            Scanner file = new Scanner(new File (fileName));
        
            while (file.hasNextLine())
            {
                String test = file.nextLine();
                String testStart = "";
                testStart += test.charAt(0);
                if(testStart.equalsIgnoreCase("p")){
                    read += test;
                } 
            }

        String[] polyStrings = read.split("P");
        int polyCount = polyStrings.length - 1;
 
        MyPolygons polyList = new MyPolygons();

        for(int i = 1; i <= polyCount; i++){

            String[] polyData = polyStrings[i].split(" ");

            Polygon newPolygon = new Polygon(Integer.valueOf(polyData[1]));
            for(int j = 2; j < polyData.length; j += 2){

                double xVal = Double.valueOf(polyData[j]);
                double yVal = Double.valueOf(polyData[j+1]);
                Point newPoint = new Point(xVal, yVal);
                newPolygon.addPoint(newPoint);
                
            }

            //Add start point again
            double xVal = Double.valueOf(polyData[2]);
            double yVal = Double.valueOf(polyData[3]);
            Point newPoint = new Point(xVal, yVal);
            newPolygon.addPoint(newPoint);
            polyList.append(newPolygon);
        }

        // Inital System output
        System.out.println("Reading Polygons.............");
        System.out.println("Initial Polygons:");
        //Print read polygons
            for(int i = 0; i < polyCount; i++){
             System.out.println(polyList.print(i));
            }
        System.out.println("...............................................");
        System.out.println("Ordered Polygons (Smallest to Largest):");
            
        // Insertion sort
        MyPolygons sortPolyList = new MyPolygons();

        sortPolyList.append(polyList.take());
        
        for(int i = 0; i < polyCount-1; i++){
            
            Polygon testPoly = polyList.take();

            sortPolyList.resetCurrent();
            for(int j = 0; j < sortPolyList.getLength(); j++){

                // If Smaller
                if(sortPolyList.getCurrentArea() > testPoly.getArea()){
                    sortPolyList.insert(testPoly);
                    break;
                }
                //If same size
                double areaTest = (sortPolyList.getCurrentArea()/testPoly.getArea())*100;
                if( areaTest > 99.95 && areaTest < 100.05){
                   if(sortPolyList.getCurrentVertexDis() > testPoly.getMinVertexDistance()){
                    sortPolyList.insert(testPoly);
                    break;
                   }
                   else{
                    sortPolyList.step();
                    sortPolyList.insert(testPoly);
                    break;
                   }
                }
                //If end of list
                if(j == sortPolyList.getLength()-1){
                    sortPolyList.append(testPoly);
                    break;
                }

                //Step
                else{
                    sortPolyList.step();
                }

            }

        }


        // Print ordered list
        for(int i = 0; i < polyCount; i++){
           System.out.println(sortPolyList.print(i));
        }

        
        
        // Catch Exception if file error
         }catch(Exception e){
            System.out.println("*** Error in Input File. Error Message:\n" + e.toString());
        }
    }


}