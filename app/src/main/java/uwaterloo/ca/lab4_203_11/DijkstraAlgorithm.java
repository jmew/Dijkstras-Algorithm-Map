package uwaterloo.ca.lab4_203_11;
import android.graphics.PointF;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
/**
 * Created by Mew on 2015-07-11.
 */
public class DijkstraAlgorithm
{
    public static Map<String, Edge> locations = new HashMap<>();
    public static Map<String, Vertex> verticies = new HashMap<>();

    public static class Vertex implements Comparable<Vertex>{
        public final String name;
        public Edge[] adjacencies;
        public double minDistance = Double.POSITIVE_INFINITY;
        public Vertex previous;
        public Vertex(String argName) { name = argName; }
        public String toString() { return name; }
        public int compareTo(Vertex other)
        {
            return Double.compare(minDistance, other.minDistance);
        }

    }

    static class Edge {
        public final Vertex target;
        public final PointF location;
        public Edge(Vertex argTarget, PointF argLocation)
        { target = argTarget; location = argLocation; }
    }

    public static void computePaths(Vertex source) {
        source.minDistance = 0.;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<>();
        vertexQueue.add(source);

        while (!vertexQueue.isEmpty()) {
            Vertex u = vertexQueue.poll();

            for (Edge e : u.adjacencies)
            {
                Vertex v = e.target;
                double weight = 1.0;
                double distanceThroughU = u.minDistance + weight;
                if (distanceThroughU < v.minDistance) {
                    vertexQueue.remove(v);

                    v.minDistance = distanceThroughU ;
                    v.previous = u;
                    vertexQueue.add(v);
                }
            }
        }
    }

    public static List<Vertex> getShortestPathTo(Vertex target) {
        List<Vertex> path = new ArrayList<>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous)
            path.add(vertex);

        Collections.reverse(path);
        return path;
    }

    public static List<PointF> calculateRoute(PointF origin, PointF destination, String mapName) {
        List<PointF> route = new ArrayList<>();

        if (mapName == "E2-3344-new.svg") {
            for (char letter = 'A'; letter <= 'Q'; letter++) {
                verticies.put(String.valueOf(letter), new Vertex(String.valueOf(letter)));
            }

            locations.put("A", new Edge(verticies.get("A"), new PointF((float) 5.4, (float) 3.6)));
            locations.put("B", new Edge(verticies.get("B"), new PointF((float) 5.4, (float) 7.25)));
            locations.put("C", new Edge(verticies.get("C"), new PointF((float) 5.4, (float) 10.9)));
            locations.put("D", new Edge(verticies.get("D"), new PointF((float) 5.4, (float) 14.55)));
            locations.put("E", new Edge(verticies.get("E"), new PointF((float) 5.4, (float) 18.2)));
            locations.put("F", new Edge(verticies.get("F"), new PointF((float) 8.75, (float) 18.2)));
            locations.put("G", new Edge(verticies.get("G"), new PointF((float) 12.1, (float) 18.2)));
            locations.put("H", new Edge(verticies.get("H"), new PointF((float) 12.1, (float) 14.55)));
            locations.put("I", new Edge(verticies.get("I"), new PointF((float) 12.1, (float) 10.9)));
            locations.put("J", new Edge(verticies.get("J"), new PointF((float) 12.1, (float) 7.25)));
            locations.put("K", new Edge(verticies.get("K"), new PointF((float) 12.1, (float) 3.6)));
            locations.put("L", new Edge(verticies.get("L"), new PointF((float) 15.5, (float) 18.2)));
            locations.put("M", new Edge(verticies.get("M"), new PointF((float) 19.0, (float) 18.2)));
            locations.put("N", new Edge(verticies.get("N"), new PointF((float) 19.0, (float) 14.55)));
            locations.put("O", new Edge(verticies.get("O"), new PointF((float) 19.0, (float) 10.9)));
            locations.put("P", new Edge(verticies.get("P"), new PointF((float) 19.0, (float) 7.25)));
            locations.put("Q", new Edge(verticies.get("Q"), new PointF((float) 19.0, (float) 3.6)));

            verticies.get("A").adjacencies = new Edge[]{ locations.get("B") };
            verticies.get("B").adjacencies = new Edge[]{ locations.get("A"), locations.get("C") };
            verticies.get("C").adjacencies = new Edge[]{ locations.get("B"), locations.get("D") };
            verticies.get("D").adjacencies = new Edge[]{ locations.get("C"), locations.get("E") };
            verticies.get("E").adjacencies = new Edge[]{ locations.get("D"), locations.get("F") };
            verticies.get("F").adjacencies = new Edge[]{ locations.get("E"), locations.get("G") };
            verticies.get("G").adjacencies = new Edge[]{ locations.get("F"), locations.get("H"), locations.get("L") };
            verticies.get("H").adjacencies = new Edge[]{ locations.get("G"), locations.get("I") };
            verticies.get("I").adjacencies = new Edge[]{ locations.get("H"), locations.get("J") };
            verticies.get("J").adjacencies = new Edge[]{ locations.get("I"), locations.get("K") };
            verticies.get("K").adjacencies = new Edge[]{ locations.get("J") };
            verticies.get("L").adjacencies = new Edge[]{ locations.get("G"), locations.get("M") };
            verticies.get("M").adjacencies = new Edge[]{ locations.get("L"), locations.get("N") };
            verticies.get("N").adjacencies = new Edge[]{ locations.get("M"), locations.get("O") };
            verticies.get("O").adjacencies = new Edge[]{ locations.get("P"), locations.get("N") };
            verticies.get("P").adjacencies = new Edge[]{ locations.get("Q"), locations.get("O") };
            verticies.get("Q").adjacencies = new Edge[]{ locations.get("P") };
        }
        else {
            for (char letter = 'A'; letter <= 'W'; letter++) {
                verticies.put(String.valueOf(letter), new Vertex(String.valueOf(letter)));
            }

            locations.put("A", new Edge(verticies.get("A"), new PointF((float) 3.2, (float) 3.3)));
            locations.put("B", new Edge(verticies.get("B"), new PointF((float) 3.2, (float) 4.8)));
            locations.put("C", new Edge(verticies.get("C"), new PointF((float) 3.2, (float) 6.3)));
            locations.put("D", new Edge(verticies.get("D"), new PointF((float) 3.2, (float) 7.8)));
            locations.put("E", new Edge(verticies.get("E"), new PointF((float) 3.2, (float) 9.3)));
            locations.put("F", new Edge(verticies.get("F"), new PointF((float) 5.275, (float) 9.3)));
            locations.put("G", new Edge(verticies.get("G"), new PointF((float) 7.35, (float) 9.3)));
            locations.put("H", new Edge(verticies.get("H"), new PointF((float) 7.35, (float) 7.8)));
            locations.put("I", new Edge(verticies.get("I"), new PointF((float) 7.35, (float) 6.3)));
            locations.put("J", new Edge(verticies.get("J"), new PointF((float) 7.35, (float) 4.8)));
            locations.put("K", new Edge(verticies.get("K"), new PointF((float) 7.35, (float) 3.3)));
            locations.put("L", new Edge(verticies.get("L"), new PointF((float) 9.425, (float) 9.3)));
            locations.put("M", new Edge(verticies.get("M"), new PointF((float) 11.5, (float) 9.3)));
            locations.put("N", new Edge(verticies.get("N"), new PointF((float) 11.5, (float) 7.8)));
            locations.put("O", new Edge(verticies.get("O"), new PointF((float) 11.5, (float) 6.3)));
            locations.put("P", new Edge(verticies.get("P"), new PointF((float) 11.5, (float) 4.8)));
            locations.put("Q", new Edge(verticies.get("Q"), new PointF((float) 11.5, (float) 3.3)));
            locations.put("R", new Edge(verticies.get("R"), new PointF((float) 13.575, (float) 9.3)));
            locations.put("S", new Edge(verticies.get("S"), new PointF((float) 15.65, (float) 9.3)));
            locations.put("T", new Edge(verticies.get("T"), new PointF((float) 15.65, (float) 7.8)));
            locations.put("U", new Edge(verticies.get("U"), new PointF((float) 15.65, (float) 6.3)));
            locations.put("V", new Edge(verticies.get("V"), new PointF((float) 15.65, (float) 4.8)));
            locations.put("W", new Edge(verticies.get("W"), new PointF((float) 15.65, (float) 3.3)));

            verticies.get("A").adjacencies = new Edge[]{ locations.get("B") };
            verticies.get("B").adjacencies = new Edge[]{ locations.get("A"), locations.get("C") };
            verticies.get("C").adjacencies = new Edge[]{ locations.get("B"), locations.get("D") };
            verticies.get("D").adjacencies = new Edge[]{ locations.get("C"), locations.get("E") };
            verticies.get("E").adjacencies = new Edge[]{ locations.get("D"), locations.get("F") };
            verticies.get("F").adjacencies = new Edge[]{ locations.get("E"), locations.get("G") };
            verticies.get("G").adjacencies = new Edge[]{ locations.get("F"), locations.get("H"), locations.get("L") };
            verticies.get("H").adjacencies = new Edge[]{ locations.get("G"), locations.get("I") };
            verticies.get("I").adjacencies = new Edge[]{ locations.get("H"), locations.get("J") };
            verticies.get("J").adjacencies = new Edge[]{ locations.get("I"), locations.get("K") };
            verticies.get("K").adjacencies = new Edge[]{ locations.get("J") };
            verticies.get("L").adjacencies = new Edge[]{ locations.get("G"), locations.get("M") };
            verticies.get("M").adjacencies = new Edge[]{ locations.get("L"), locations.get("N"), locations.get("R")  };
            verticies.get("N").adjacencies = new Edge[]{ locations.get("M"), locations.get("O") };
            verticies.get("O").adjacencies = new Edge[]{ locations.get("P"), locations.get("N") };
            verticies.get("P").adjacencies = new Edge[]{ locations.get("Q"), locations.get("O") };
            verticies.get("Q").adjacencies = new Edge[]{ locations.get("P") };
            verticies.get("R").adjacencies = new Edge[]{ locations.get("M"), locations.get("S") };
            verticies.get("S").adjacencies = new Edge[]{ locations.get("R"), locations.get("T") };
            verticies.get("T").adjacencies = new Edge[]{ locations.get("S"), locations.get("U") };
            verticies.get("U").adjacencies = new Edge[]{ locations.get("T"), locations.get("V") };
            verticies.get("V").adjacencies = new Edge[]{ locations.get("U"), locations.get("W") };
            verticies.get("W").adjacencies = new Edge[]{ locations.get("V")};
        }

        String[] nodes = distance(origin, destination);
        computePaths(verticies.get(nodes[0]));
        List<Vertex> path = getShortestPathTo(verticies.get(nodes[1]));
        route.add(origin);
        for (Vertex v : path) {
            route.add(locations.get(String.valueOf(v)).location);
        }
        route.add(destination);
        return route;
    }

    public static String[] distance(PointF origin, PointF destination) {
        double min = 99999;
        String[] minKeys = new String[2];
        Map<String, Double> originDisplacement = new HashMap<>();
        Map<String, Double> destinationDisplacement = new HashMap<>();

        for (Map.Entry<String, Edge> e: locations.entrySet()) {
            originDisplacement.put(String.valueOf(e.getKey()), Math.sqrt(Math.pow((origin.x - e.getValue().location.x), 2) + Math.pow((origin.y - e.getValue().location.y), 2)));
            destinationDisplacement.put(String.valueOf(e.getKey()), Math.sqrt(Math.pow((destination.x - e.getValue().location.x), 2) + Math.pow((destination.y - e.getValue().location.y), 2)));
        }

        for (Map.Entry<String, Double> d: originDisplacement.entrySet()) {
            if (d.getValue() < min) {
                min = d.getValue();
                minKeys[0] = d.getKey();
            }
        }

        min = 99999;
        for (Map.Entry<String, Double> d: destinationDisplacement.entrySet()) {
            if (d.getValue() < min) {
                min = d.getValue();
                minKeys[1] = d.getKey();
            }
        }
        return minKeys;
    }
}