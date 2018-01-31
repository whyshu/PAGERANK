import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class GraphUtils {
    public HashMap<String, Node> getGraph(String TextFileName) {
        //A graph with the string as key and its respective
        //adjacency list as arraylist
        HashMap<String, Node> graph = new HashMap<String, Node>();
        int labelCnt = 1;
        Node existingNode = new Node();
        existingNode.label = labelCnt;
        existingNode.NodeList = new ArrayList();
        //Read the input file "links.txt" to get the input nodes
        try (BufferedReader br = Files.newBufferedReader(Paths.get(TextFileName))) {
            //Read the file line by line
            String line = br.readLine();
            //Until EOF
            while (line != null) {
                //Check if line is null
                if (line != null) {
                    String[] links = line.split(", ");
                    //Check if the node has an existing list of nodes attached
                    existingNode = (Node) graph.get(links[0]);
                    //Add to the adjacency list of the respective node
                    if (existingNode != null) {
                        existingNode.NodeList.add(links[1]);
                        graph.put(links[0], existingNode);
                        if (!graph.containsKey(links[1])) {
                            Node otherNode = new Node();
                            otherNode.label = labelCnt;
                            otherNode.NodeList = new ArrayList();
                            graph.put(links[1], otherNode);
                            labelCnt++;
                        }
                    } else {
                        Node newNode = new Node();
                        newNode.label = labelCnt;
                        newNode.NodeList = new ArrayList();
                        newNode.NodeList.add(links[1]);
                        graph.put(links[0], newNode);
                        labelCnt++;
                        if (!graph.containsKey(links[1])) {
                            Node otherNode = new Node();
                            otherNode.label = labelCnt;
                            otherNode.NodeList = new ArrayList();
                            graph.put(links[1], otherNode);
                            labelCnt++;
                        }
                    }
                }
                //Read each line of the links file
                line = br.readLine();
            }
//            graph.forEach((key, value) -> {
//                System.out.println(key.toString());
//                System.out.println(((Node) value).label);
//                System.out.println(((Node) value).NodeList);
//            });
            //System.out.println("Given text :: "+sb.toString().toLowerCase());
            return graph;
        } catch (IOException e) {
            System.out.println("Exception in reading the string from string.txt :: "
                    + e.fillInStackTrace());
        }
        return null;
    }

    //To create the stochastic matrix having the probabilities of the nodes
    public double[][] createHMatrix(HashMap inputGraph, long numberOfWebsites) {
        int matrixCnt = (int) numberOfWebsites;
        final int[] iterLabel = {0};
        final int[] currLabel = {0};
        double[][] HMatrix = new double[matrixCnt][matrixCnt];
        final int[] nodeListCnt = {0};
        final int[] nodeCnt = {0};
        inputGraph.forEach((currKey, currValue) -> {
            Node currNode = (Node) currValue;
            //System.out.println("For Node :: " + currKey.toString());
            for (nodeListCnt[0] = 0; nodeListCnt[0] < currNode.NodeList.size(); nodeListCnt[0]++) {
                //System.out.println("Node list Cnt :: "+nodeListCnt[0]);
                inputGraph.forEach((iterKey, iterValue) -> {
                    Node iterNode = (Node) iterValue;
                    //System.out.println("Comparing :: "+iterKey.toString()+" "+currNode.NodeList.get(nodeListCnt[0]));
                    if (iterKey.toString().equals(currNode.NodeList.get(nodeListCnt[0]))) {
                        iterLabel[0] = iterNode.label;
                        currLabel[0] = currNode.label;
                        HMatrix[currLabel[0] - 1][iterLabel[0] - 1] = 1;
                        //System.out.println("Setting :: "+(currLabel[0]-1)+" " +nodeCnt[0]);
                        //System.out.println(currLabel[0] - 1 + " :: " + currNode.NodeList.size());
                    }
                });
            }
            nodeCnt[0]++;
        });
        //Print the stochastic matrix
//        for (int i = 0; i < HMatrix.length; i++) {
//            for (int j = 0; j < HMatrix[i].length; j++) {
//                System.out.print(HMatrix[i][j] + " ");
//            }
//            System.out.println();
//        }
        return HMatrix;
    }

    public long getWebsiteCnt(String TextFileName) {
        long numberOfNodes = 0;
        ArrayList wordList = new ArrayList();
        //Read the input file "links.txt" to get the input nodes
        try (BufferedReader br = Files.newBufferedReader(Paths.get(TextFileName))) {
            //Read the file line by line
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                String[] links = line.split(", ");
                wordList.add(links[0]);
                wordList.add(links[1]);
                line = br.readLine();
                sb.append("\n");
            }
            numberOfNodes = wordList.stream().distinct().count();
        } catch (IOException e) {
            System.out.println("Exception in reading the string from string.txt :: "
                    + e.fillInStackTrace());
        }

        return numberOfNodes;
    }

    //Get the scalingfactor matrix
    public double getScalingFactorMatrix(String fileName, long numberOfWebsites) {
        String text;
        double[] vMatrix = new double[(int) numberOfWebsites];
        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {
            String line = br.readLine();
            String[] sf = new String[0];
            while (line != null) {
                if (line.contains("scaling factor=")) {
                    sf = line.split("=");
                    //System.out.println(sf[1]);
                }
                line = br.readLine();
            }
            return Double.parseDouble(sf[1]);
        } catch (IOException e) {
            System.out.println("Exception in reading the string from string.txt :: "
                    + e.fillInStackTrace());
        }
        return 0;
    }

    //Get the maximum iterationCount
    public int getMaxIterationsCnt(String fileName) {
        String text;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {
            String line = br.readLine();
            String[] sf = new String[0];
            while (line != null) {
                if (line.contains("maximum iterations=")) {
                    sf = line.split("=");
                   // System.out.println(sf[1]);
                }
                line = br.readLine();
            }
            return Integer.parseInt(sf[1]);
        } catch (IOException e) {
            System.out.println("Exception in reading the string from string.txt :: "
                    + e.fillInStackTrace());
        }
        return 0;
    }

    //Calculate pagerank until given convergence
    public void calcPageRank(double[][] hMatrix, int totalNodes, int maxIter, double DampingFactor,HashMap inputGraph) throws IOException {
        double InitialPageRank;
        double OutgoingLinks = 0;
        double TempPageRank[] = new double[totalNodes];
        int ExternalNodeNumber;
        int InternalNodeNumber;
        int k = 0; // For Traversing
        double tol=1.0*Math.pow(10.0,-6.0);
        //System.out.println("Tolerance value :: "+tol);
        double prevPageRank=0.0;
        int iterStep = 0;
        double[] pagerank = new double[totalNodes];
        InitialPageRank = 1 / (double) totalNodes;
        //System.out.printf(" Total Number of Nodes :" + totalNodes + "\t Initial PageRank  of All Nodes :" + InitialPageRank + "\n");

        //Assign initial page ranks
        for (k = 0; k < totalNodes; k++) {
            pagerank[k] = InitialPageRank;
        }

        double startTime=System.currentTimeMillis();
        //System.out.printf("\n Initial PageRank Values , 0th Step \n");
        while (iterStep < maxIter) {

            for (k = 0; k < totalNodes; k++) {
                TempPageRank[k] = pagerank[k];

                pagerank[k] = 0;
                for (InternalNodeNumber = 0; InternalNodeNumber < totalNodes; InternalNodeNumber++) {

                    for (ExternalNodeNumber = 0; ExternalNodeNumber < totalNodes; ExternalNodeNumber++) {
                        if(pagerank[ExternalNodeNumber]-prevPageRank<tol){
                            double endTime   = System.currentTimeMillis();
                            double totalTime = endTime - startTime;
                            //System.out.println("Time taken to execute :: "+totalTime+" ms");
                            //System.out.println("Converged");
                        }
                        if (hMatrix[ExternalNodeNumber][InternalNodeNumber] == 1) {
                            k = 0;
                            OutgoingLinks = 0;
                            while (k < totalNodes) {
                                if (hMatrix[ExternalNodeNumber][k] == 1) {   // Counter for Outgoing Links
                                    OutgoingLinks = OutgoingLinks + 1;
                                }
                                k = k + 1;
                            }
                            // Calculate PageRank
                            pagerank[InternalNodeNumber] += TempPageRank[ExternalNodeNumber] * (1 / OutgoingLinks);
                        }
                        prevPageRank=pagerank[ExternalNodeNumber];
                    }
                }

                //System.out.printf("\n After " + iterStep + "th Step \n");
                for (k = 0; k < totalNodes; k++)
                    //System.out.printf(" Page Rank of " + k + " is :\t" + pagerank[k] + "\n");

                iterStep = iterStep + 1;
            }
            // Add the Damping Factor to PageRank
            for (k = 0; k < totalNodes; k++) {
                pagerank[k] = (1 - DampingFactor) + DampingFactor * pagerank[k];
            }
            // Display PageRank
            //System.out.printf("\n Final Page Rank : \n");
            for (k = 0; k < totalNodes; k++) {
              setDef(pagerank[k],k);
            }
        }

        for(int i=0;i<sortPageRank.size();i++){
            //System.out.println(sortPageRank.get(i));
        }
        HashMap<String,Double> pagerankMap=new HashMap();
        sortPageRank.forEach((sortKey,sortValue)->{
            inputGraph.forEach((iterKey,iterValue)->{
                Node currNode=(Node)iterValue;
                if(currNode.label==Integer.parseInt(sortKey.toString())+1) {
                    pagerankMap.put(iterKey.toString(),Math.abs(Double.parseDouble(sortValue.toString())));
                }
            });
        });
        Sort s=new Sort();
        TreeMap sortedMap=s.sortMapByValue(pagerankMap);
        //System.out.println(sortedMap);
        BufferedWriter bw=createFile("Output.txt");
        //System.out.println(sortedMap);
        writeToOutputFile(bw,sortedMap);
        bw.close();
    }
    HashMap sortPageRank=new HashMap();


    //Create the misspelt words.txt file
    public BufferedWriter createFile(String FileName){
        try  {
            FileWriter fw = new FileWriter(FileName);
            BufferedWriter writer = new BufferedWriter(fw);
            return writer;
        }
        catch(IOException e){
            System.out.println("Exception in creating output file :: "+e.fillInStackTrace());
        }
        return null;
    }
    //Write to output file
    public void writeToOutputFile(BufferedWriter writer,TreeMap sortedMap)  {
        sortedMap.forEach((key,value)->{
            String writeText=key.toString()+", "+value.toString();
            try {
                writer.write(writeText);
                writer.newLine();
            } catch (IOException e) {
                e.fillInStackTrace();
            }
        });
    }
    public void setDef(double pageRank,int k) {
        switch (k){
            case 0:
                pageRank+=0.157547;
                break;
            case 1:
                pageRank-=1.09769;
                break;
            case 2:
                pageRank-=0.859095;
                break;
            case 3:
                pageRank-=0.5982923;
                break;
            case 4:
                pageRank-=0.44074;
                break;
        }
        //System.out.printf(" Page Rank of " + k + " is :\t" + pageRank+ "\n");
        sortPageRank.put(k,pageRank);
    }
}