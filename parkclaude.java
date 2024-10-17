import java.util.*;

// Kelas untuk merepresentasikan spot parkir
class ParkingSpot {
    int x, y;
    boolean isOccupied;
    
    public ParkingSpot(int x, int y) {
        this.x = x;
        this.y = y;
        this.isOccupied = false;
    }
}

// Kelas untuk merepresentasikan area parkir
class ParkingLot {
    ParkingSpot[][] spots;
    int width, height;
    
    public ParkingLot(int width, int height) {
        this.width = width;
        this.height = height;
        spots = new ParkingSpot[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                spots[y][x] = new ParkingSpot(x, y);
            }
        }
    }
    
    public void occupyRandomSpots(int count) {
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            int x, y;
            do {
                x = rand.nextInt(width);
                y = rand.nextInt(height);
            } while (spots[y][x].isOccupied);
            spots[y][x].isOccupied = true;
        }
    }
}


// Interface untuk algoritma pencarian
interface SearchAlgorithm {
    SearchResult findParkingSpot(ParkingLot parkingLot, int startX, int startY);
}


// Implementasi A* Search
class AStarSearch implements SearchAlgorithm {
    class Node implements Comparable<Node> {
        ParkingSpot spot;
        int g, h;
        Node parent;

        Node(ParkingSpot spot, int g, int h, Node parent) {
            this.spot = spot;
            this.g = g;
            this.h = h;
            this.parent = parent;
        }

        int f() { return g + h; }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.f(), other.f());
        }
    }

    @Override
    public SearchResult findParkingSpot(ParkingLot parkingLot, int startX, int startY) {
        PriorityQueue<Node> openList = new PriorityQueue<>();
        Set<ParkingSpot> closedList = new HashSet<>();
        int maxSpaceComplexity = 0;

        Node start = new Node(parkingLot.spots[startY][startX], 0, 0, null);
        openList.add(start);

        while (!openList.isEmpty()) {
            maxSpaceComplexity = Math.max(maxSpaceComplexity, openList.size());

            Node current = openList.poll();

            if (!current.spot.isOccupied) {
                return new SearchResult(current.spot, current.g, maxSpaceComplexity);
            }

            closedList.add(current.spot);

            for (int[] dir : new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}}) {
                int newX = current.spot.x + dir[0];
                int newY = current.spot.y + dir[1];

                if (newX >= 0 && newX < parkingLot.width && newY >= 0 && newY < parkingLot.height) {
                    ParkingSpot neighbor = parkingLot.spots[newY][newX];
                    if (!closedList.contains(neighbor)) {
                        int g = current.g + 1;
                        int h = Math.abs(newX - startX) + Math.abs(newY - startY);
                        Node neighborNode = new Node(neighbor, g, h, current);
                        openList.add(neighborNode);
                    }
                }
            }
        }

        return new SearchResult(null, 0, maxSpaceComplexity); // No available parking spot found
    }
}


// Implementasi BFS
class BFSSearch implements SearchAlgorithm {
    @Override
    public SearchResult findParkingSpot(ParkingLot parkingLot, int startX, int startY) {
        Queue<ParkingSpot> queue = new LinkedList<>();
        boolean[][] visited = new boolean[parkingLot.height][parkingLot.width];
        int maxSpaceComplexity = 0;
        int cost = 0;

        ParkingSpot start = parkingLot.spots[startY][startX];
        queue.offer(start);
        visited[startY][startX] = true;

        while (!queue.isEmpty()) {
            maxSpaceComplexity = Math.max(maxSpaceComplexity, queue.size());
            ParkingSpot current = queue.poll();
            cost++;

            if (!current.isOccupied) {
                return new SearchResult(current, cost, maxSpaceComplexity);
            }

            for (int[] dir : new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}}) {
                int newX = current.x + dir[0];
                int newY = current.y + dir[1];

                if (newX >= 0 && newX < parkingLot.width && newY >= 0 && newY < parkingLot.height && !visited[newY][newX]) {
                    ParkingSpot neighbor = parkingLot.spots[newY][newX];
                    queue.offer(neighbor);
                    visited[newY][newX] = true;
                }
            }
        }

        return new SearchResult(null, 0, maxSpaceComplexity); // No available parking spot found
    }
}


// Implementasi Uniform Cost Search
class UniformCostSearch implements SearchAlgorithm {
    class Node implements Comparable<Node> {
        ParkingSpot spot;
        int cost;
        Node parent;

        Node(ParkingSpot spot, int cost, Node parent) {
            this.spot = spot;
            this.cost = cost;
            this.parent = parent;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.cost, other.cost);
        }
    }

    @Override
    public SearchResult findParkingSpot(ParkingLot parkingLot, int startX, int startY) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        Set<ParkingSpot> visited = new HashSet<>();
        int maxSpaceComplexity = 0;

        Node start = new Node(parkingLot.spots[startY][startX], 0, null);
        pq.offer(start);

        while (!pq.isEmpty()) {
            maxSpaceComplexity = Math.max(maxSpaceComplexity, pq.size());

            Node current = pq.poll();

            if (!current.spot.isOccupied) {
                return new SearchResult(current.spot, current.cost, maxSpaceComplexity);
            }

            if (visited.contains(current.spot)) {
                continue;
            }

            visited.add(current.spot);

            for (int[] dir : new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}}) {
                int newX = current.spot.x + dir[0];
                int newY = current.spot.y + dir[1];

                if (newX >= 0 && newX < parkingLot.width && newY >= 0 && newY < parkingLot.height) {
                    ParkingSpot neighbor = parkingLot.spots[newY][newX];
                    if (!visited.contains(neighbor)) {
                        pq.offer(new Node(neighbor, current.cost + 1, current));
                    }
                }
            }
        }

        return new SearchResult(null, 0, maxSpaceComplexity); // No available parking spot found
    }
}




// Kelas utama untuk menjalankan simulasi
public class parkclaude {
    public static void runScenario(String scenarioName, ParkingLot parkingLot, int startX, int startY) {
        System.out.println("Scenario: " + scenarioName);
        System.out.println("Parking Lot Size: " + parkingLot.width + "x" + parkingLot.height);
        System.out.println("Starting Position: (" + startX + ", " + startY + ")");
        printParkingLot(parkingLot);

        SearchAlgorithm[] algorithms = {
            new AStarSearch(),
            new BFSSearch(),
            new UniformCostSearch()
        };

        for (SearchAlgorithm algorithm : algorithms) {
            long startTime = System.nanoTime();
            SearchResult result = algorithm.findParkingSpot(parkingLot, startX, startY);
            long endTime = System.nanoTime();

            System.out.println(algorithm.getClass().getSimpleName() + ":");
            if (result.spot != null) {
                System.out.println("Found parking spot at (" + result.spot.x + ", " + result.spot.y + ")");
            } else {
                System.out.println("No parking spot found");
            }
            System.out.println("Time taken: " + (endTime - startTime) / 1_000_000.0 + " ms");
            System.out.println("Space Complexity (max nodes in memory): " + result.maxSpaceComplexity);
            System.out.println("Accuracy: " + (result.spot != null && !result.spot.isOccupied ? "Correct" : "Incorrect"));
            System.out.println("Optimality (cost to parking spot): " + result.cost);
            System.out.println();
        }
        System.out.println("-------------------------------\n");
    }

    public static void printParkingLot(ParkingLot parkingLot) {
        for (int y = 0; y < parkingLot.height; y++) {
            for (int x = 0; x < parkingLot.width; x++) {
                System.out.print(parkingLot.spots[y][x].isOccupied ? "X " : "O ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static ParkingLot createPatternedParkingLot(int width, int height) {
        ParkingLot lot = new ParkingLot(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x < width / 2) {
                    lot.spots[y][x].isOccupied = true;
                }
            }
        }
        return lot;
    }

    public static void main(String[] args) {
        // Scenario 1: Almost empty parking lot
        ParkingLot scenario1 = new ParkingLot(10, 10);
        scenario1.occupyRandomSpots(10); // 10% occupied
        runScenario("Almost Empty Parking Lot", scenario1, 0, 0);

        // Scenario 2: Almost full parking lot
        ParkingLot scenario2 = new ParkingLot(10, 10);
        scenario2.occupyRandomSpots(90); // 90% occupied
        runScenario("Almost Full Parking Lot", scenario2, 0, 0);

        // Scenario 3: Patterned parking lot (half full)
        ParkingLot scenario3 = createPatternedParkingLot(10, 10);
        runScenario("Patterned Parking Lot (Half Full)", scenario3, 0, 0);

        // Scenario 4: Large parking lot
        ParkingLot scenario4 = new ParkingLot(20, 20);
        scenario4.occupyRandomSpots(300); // 75% occupied
        runScenario("Large Parking Lot", scenario4, 0, 0);

        // Scenario 5: Small parking lot
        ParkingLot scenario5 = new ParkingLot(5, 5);
        scenario5.occupyRandomSpots(20); // 80% occupied
        runScenario("Small Parking Lot", scenario5, 0, 0);
    }
}

class SearchResult {
    ParkingSpot spot;
    int cost;
    int maxSpaceComplexity;

    public SearchResult(ParkingSpot spot, int cost, int maxSpaceComplexity) {
        this.spot = spot;
        this.cost = cost;
        this.maxSpaceComplexity = maxSpaceComplexity;
    }
}
