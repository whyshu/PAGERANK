import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class PageRank {
    public static void main(String []args) throws IOException {
        //Read the links file to get the input graph
        GraphUtils graphUtilsObj=new GraphUtils();
        //Get the input graph from the links.txt file
        HashMap inputGraph=graphUtilsObj.getGraph("links.txt");
        //System.out.println(inputGraph);

        //Get the number of input nodes in the graph
        long numberOfWebsites=graphUtilsObj.getWebsiteCnt("links.txt");

        //Create the H matrix
        double[][] hMatrix=graphUtilsObj.createHMatrix(inputGraph,numberOfWebsites);

        //Get the scaling factor and the number of maximum iterations
        double scalingFactor=graphUtilsObj.getScalingFactorMatrix("readme.txt",numberOfWebsites);
        int maxIter=graphUtilsObj.getMaxIterationsCnt("readme.txt");
        double startTime=System.currentTimeMillis();
        graphUtilsObj.calcPageRank(hMatrix,(int)numberOfWebsites,maxIter,scalingFactor,inputGraph);
        double endTime   = System.currentTimeMillis();
        double totalTime = endTime - startTime;
//        for(int i=0;i<pageRank.length;i++) {
//            System.out.println(pageRank[i]);
//        }
    }
}
